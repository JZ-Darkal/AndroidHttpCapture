package net.lightbody.bmp.mitm.manager;

import android.os.Build;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SupportedCipherSuiteFilter;
import net.lightbody.bmp.mitm.CertificateAndKey;
import net.lightbody.bmp.mitm.CertificateAndKeySource;
import net.lightbody.bmp.mitm.CertificateInfo;
import net.lightbody.bmp.mitm.CertificateInfoGenerator;
import net.lightbody.bmp.mitm.HostnameCertificateInfoGenerator;
import net.lightbody.bmp.mitm.RootCertificateGenerator;
import net.lightbody.bmp.mitm.TrustSource;
import net.lightbody.bmp.mitm.exception.MitmException;
import net.lightbody.bmp.mitm.exception.SslContextInitializationException;
import net.lightbody.bmp.mitm.keys.ECKeyGenerator;
import net.lightbody.bmp.mitm.keys.KeyGenerator;
import net.lightbody.bmp.mitm.keys.RSAKeyGenerator;
import net.lightbody.bmp.mitm.stats.CertificateGenerationStatistics;
import net.lightbody.bmp.mitm.tools.DefaultSecurityProviderTool;
import net.lightbody.bmp.mitm.tools.SecurityProviderTool;
import net.lightbody.bmp.mitm.util.EncryptionUtil;
import net.lightbody.bmp.mitm.util.MitmConstants;
import net.lightbody.bmp.mitm.util.SslUtil;
import net.lightbody.bmp.util.HttpUtil;
import org.littleshoot.proxy.MitmManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * An {@link MitmManager} that will create SSLEngines for clients that present impersonated certificates for upstream servers. The impersonated
 * certificates will be signed using the certificate and private key specified in an {@link #rootCertificateSource}. The impersonated server
 * certificates will be created by the {@link #securityProviderTool} based on the {@link CertificateInfo} returned by the {@link #certificateInfoGenerator}.
 */
public class ImpersonatingMitmManager implements MitmManager {
    private static final Logger log = LoggerFactory.getLogger(ImpersonatingMitmManager.class);

    /**
     * Cipher suites allowed on proxy connections to upstream servers.
     */
    private final List<String> serverCipherSuites;

    /**
     * Cipher suites allowed on client connections to the proxy.
     */
    private final List<String> clientCipherSuites;

    /**
     * The SSLContext that will be used for communications with all upstream servers. This can be reused, so store it as a lazily-loaded singleton.
     */
    private final Supplier<SslContext> upstreamServerSslContext = Suppliers.memoize(new Supplier<SslContext>() {
        @Override
        public SslContext get() {
            return SslUtil.getUpstreamServerSslContext(serverCipherSuites, trustSource);
        }
    });

    /**
     * Cache for impersonating netty SslContexts. SslContexts can be safely reused, so caching the impersonating contexts avoids
     * repeatedly re-impersonating upstream servers.
     */
    private final Cache<String, SslContext> sslContextCache;

    /**
     * Generator used to create public and private keys for the server certificates.
     */
    private final KeyGenerator serverKeyGenerator;

    /**
     * The source of the CA's {@link CertificateAndKey} that will be used to sign generated server certificates.
     */
    private final CertificateAndKeySource rootCertificateSource;

    /**
     * The message digest used to sign the server certificate, such as SHA512.
     * See https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#MessageDigest for information
     * on supported message digests.
     */
    private final String serverCertificateMessageDigest;

    /**
     * The source of trusted root CAs. May be null, which disables all upstream certificate validation. Disabling upstream
     * certificate validation allows attackers to intercept communciations and should only be used during testing.
     */
    private final TrustSource trustSource;

    /**
     * Utility used to generate {@link CertificateInfo} objects when impersonating an upstream server.
     */
    private final CertificateInfoGenerator certificateInfoGenerator;

    /**
     * Tool implementation that is used to generate, sign, and otherwise manipulate server certificates.
     */
    private final SecurityProviderTool securityProviderTool;

    /**
     * The CA root root certificate used to sign generated server certificates. {@link CertificateAndKeySource#load()}
     * is only called once to retrieve the CA root certificate, which will be used to impersonate all server certificates.
     */
    private Supplier<CertificateAndKey> rootCertificate = Suppliers.memoize(new Supplier<CertificateAndKey>() {
        @Override
        public CertificateAndKey get() {
            return rootCertificateSource.load();
        }
    });

    /**
     * Simple server certificate generation statistics.
     */
    private final CertificateGenerationStatistics statistics = new CertificateGenerationStatistics();

    /**
     * Creates a new ImpersonatingMitmManager. In general, use {@link ImpersonatingMitmManager.Builder}
     * to construct new instances.
     */
    public ImpersonatingMitmManager(CertificateAndKeySource rootCertificateSource,
                                    KeyGenerator serverKeyGenerator,
                                    String serverMessageDigest,
                                    TrustSource trustSource,
                                    int sslContextCacheConcurrencyLevel,
                                    long cacheExpirationIntervalMs,
                                    SecurityProviderTool securityProviderTool,
                                    CertificateInfoGenerator certificateInfoGenerator,
                                    Collection<String> serverCipherSuites,
                                    Collection<String> clientCipherSuites) {
        if (rootCertificateSource == null) {
            throw new IllegalArgumentException("CA root certificate source cannot be null");
        }

        if (serverKeyGenerator == null) {
            throw new IllegalArgumentException("Server key generator cannot be null");
        }

        if (serverMessageDigest == null) {
            throw new IllegalArgumentException("Server certificate message digest cannot be null");
        }

        if (securityProviderTool == null) {
            throw new IllegalArgumentException("The certificate tool implementation cannot be null");
        }

        if (certificateInfoGenerator == null) {
            throw new IllegalArgumentException("Certificate info generator cannot be null");
        }

        this.rootCertificateSource = rootCertificateSource;

        this.trustSource = trustSource;

        this.serverCertificateMessageDigest = serverMessageDigest;

        this.serverKeyGenerator = serverKeyGenerator;

        this.sslContextCache = CacheBuilder.newBuilder()
                .concurrencyLevel(sslContextCacheConcurrencyLevel)
                .expireAfterAccess(cacheExpirationIntervalMs, TimeUnit.MILLISECONDS)
                .build();

        this.securityProviderTool = securityProviderTool;

        this.certificateInfoGenerator = certificateInfoGenerator;

        this.serverCipherSuites = ImmutableList.copyOf(serverCipherSuites);
        log.debug("Allowed ciphers for proxy connections to upstream servers (some ciphers may not be available): {}", serverCipherSuites);

        this.clientCipherSuites = ImmutableList.copyOf(clientCipherSuites);
        log.debug("Allowed ciphers for client connections to proxy (some ciphers may not be available): {}", clientCipherSuites);
    }

    @Override
    public SSLEngine serverSslEngine() {
        try {
            SSLEngine sslEngine = upstreamServerSslContext.get().newEngine(ByteBufAllocator.DEFAULT);

            return sslEngine;
        } catch (RuntimeException e) {
            throw new MitmException("Error creating SSLEngine for connection to upstream server", e);
        }
    }

    @Override
    public SSLEngine serverSslEngine(String peerHost, int peerPort) {
        try {
            SSLEngine sslEngine = upstreamServerSslContext.get().newEngine(ByteBufAllocator.DEFAULT, peerHost, peerPort);

            // support SNI by setting the endpoint identification algorithm. this requires Java 7+.
            SSLParameters sslParams = new SSLParameters();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                sslParams.setEndpointIdentificationAlgorithm("HTTPS");
            }
            sslEngine.setSSLParameters(sslParams);

            return sslEngine;
        } catch (RuntimeException e) {
            throw new MitmException("Error creating SSLEngine for connection to upstream server: " + peerHost + ":" + peerPort, e);
        }
    }

    @Override
    public SSLEngine clientSslEngineFor(HttpRequest httpRequest, SSLSession sslSession) {
        String requestedHostname = HttpUtil.getHostFromRequest(httpRequest);

        try {
            SslContext ctx = getHostnameImpersonatingSslContext(requestedHostname, sslSession);

            return ctx.newEngine(ByteBufAllocator.DEFAULT);
        } catch (RuntimeException e) {
            throw new MitmException("Error creating SSLEngine for connection to client to impersonate upstream host: " + requestedHostname, e);
        }
    }

    /**
     * Retrieves an SSLContext that impersonates the specified hostname. If an impersonating SSLContext has already been
     * created for this hostname and is stored in the cache, it will be reused. Otherwise, a certificate will be created
     * which impersonates the specified hostname.
     *
     * @param hostnameToImpersonate the hostname for which the impersonated SSLContext is being requested
     * @param sslSession the upstream server SSLSession
     * @return SSLContext which will present an impersonated certificate
     */
    private SslContext getHostnameImpersonatingSslContext(final String hostnameToImpersonate, final SSLSession sslSession) {
        try {
            return sslContextCache.get(hostnameToImpersonate, new Callable<SslContext>() {
                @Override
                public SslContext call() throws Exception {
                    return createImpersonatingSslContext(sslSession, hostnameToImpersonate);
                }
            });
        } catch (ExecutionException e) {
            throw new SslContextInitializationException("An error occurred while impersonating the remote host: " + hostnameToImpersonate, e);
        }

        //TODO: generate wildcard certificates, rather than one certificate per host, to reduce the number of certs generated
    }

    /**
     * Creates an SSLContext that will present an impersonated certificate for the specified hostname to the client.
     * This is a convenience method for {@link #createImpersonatingSslContext(CertificateInfo)} that generates the
     * {@link CertificateInfo} from the specified hostname using the {@link #certificateInfoGenerator}.
     *
     * @param sslSession sslSession between the proxy and the upstream server
     * @param hostnameToImpersonate hostname (supplied by the client's HTTP CONNECT) that will be impersonated
     * @return an SSLContext presenting a certificate matching the hostnameToImpersonate
     */
    private SslContext createImpersonatingSslContext(SSLSession sslSession, String hostnameToImpersonate) {
        // get the upstream server's certificate so the certificateInfoGenerator can (optionally) use it to construct a forged certificate
        X509Certificate originalCertificate = SslUtil.getServerCertificate(sslSession);

        // get the CertificateInfo that will be used to populate the impersonated X509Certificate
        CertificateInfo certificateInfo = certificateInfoGenerator.generate(Collections.singletonList(hostnameToImpersonate), originalCertificate);

        SslContext sslContext = createImpersonatingSslContext(certificateInfo);

        return sslContext;
    }

    /**
     * Generates an {@link SslContext} using an impersonated certificate containing the information in the specified
     * certificateInfo.
     *
     * @param certificateInfo certificate information to impersonate
     * @return an SslContext that will present the impersonated certificate to the client
     */
    private SslContext createImpersonatingSslContext(CertificateInfo certificateInfo) {
        long impersonationStart = System.currentTimeMillis();

        // generate a public and private key pair for the forged certificate. the SslContext will send the impersonated certificate to clients
        // to impersonate the real upstream server, and will use the private key to encrypt the channel.
        KeyPair serverKeyPair = serverKeyGenerator.generate();

        // get the CA root certificate and private key that will be used to sign the forced certificate
        X509Certificate caRootCertificate = rootCertificate.get().getCertificate();
        PrivateKey caPrivateKey = rootCertificate.get().getPrivateKey();
        if (caRootCertificate == null || caPrivateKey == null) {
            throw new IllegalStateException("A CA root certificate and private key are required to sign a server certificate. Root certificate was: "
                    + caRootCertificate + ". Private key was: " + caPrivateKey);
        }

        // determine if the server private key was signed with an RSA private key. though TLS no longer requires the server
        // certificate to use the same private key type as the root certificate, Java bug JDK-8136442 prevents Java from creating a opening an SSL socket
        // if the CA and server certificates are not of the same type. see https://bugs.openjdk.java.net/browse/JDK-8136442
        // note this only applies to RSA CAs signing EC server certificates; Java seems to properly handle EC CAs signing
        // RSA server certificates.
        if (EncryptionUtil.isEcKey(serverKeyPair.getPrivate()) && EncryptionUtil.isRsaKey(caPrivateKey)) {
            log.warn("CA private key is an RSA key and impersonated server private key is an Elliptic Curve key. JDK bug 8136442 may prevent the proxy server from creating connections to clients due to 'no cipher suites in common'.");
        }

        // create the forged server certificate and sign it with the root certificate and private key
        CertificateAndKey impersonatedCertificateAndKey = securityProviderTool.createServerCertificate(
                certificateInfo,
                caRootCertificate,
                caPrivateKey,
                serverKeyPair,
                serverCertificateMessageDigest);

        X509Certificate[] certChain = {impersonatedCertificateAndKey.getCertificate(), caRootCertificate};
        SslContext sslContext;
        try {
            sslContext = SslContextBuilder.forServer(impersonatedCertificateAndKey.getPrivateKey(), certChain)
                    .ciphers(clientCipherSuites, SupportedCipherSuiteFilter.INSTANCE)
                    .build();

        } catch (SSLException e) {
            throw new MitmException("Error creating SslContext for connection to client using impersonated certificate and private key", e);
        }

        long impersonationFinish = System.currentTimeMillis();

        statistics.certificateCreated(impersonationStart, impersonationFinish);

        log.debug("Impersonated certificate for {} in {}ms", certificateInfo.getCommonName(), impersonationFinish - impersonationStart);

        return sslContext;
    }

    /**
     * Returns basic certificate generation statistics for this MitmManager.
     */
    public CertificateGenerationStatistics getStatistics() {
        return this.statistics;
    }

    /**
     * Convenience method to return a new {@link Builder} instance default default values: a {@link RootCertificateGenerator}
     * that dynamically generates an RSA root certificate and RSA server certificates.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Convenience method to return a new {@link Builder} instance that will dynamically create EC root certificates and
     * EC server certificates, but otherwise uses default values.
     */
    public static Builder builderWithECC() {
        return new Builder()
                .serverKeyGenerator(new ECKeyGenerator())
                .rootCertificateSource(RootCertificateGenerator.builder()
                        .keyGenerator(new ECKeyGenerator())
                        .build());
    }

    /**
     * A Builder for {@link ImpersonatingMitmManager}s. Initialized with suitable default values suitable for most purposes.
     */
    public static class Builder {
        private CertificateAndKeySource rootCertificateSource = RootCertificateGenerator.builder().build();

        private KeyGenerator serverKeyGenerator = new RSAKeyGenerator();

        private TrustSource trustSource = TrustSource.defaultTrustSource();

        private int cacheConcurrencyLevel = 8;
        private long cacheExpirationIntervalMs = TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES);

        private String serverMessageDigest = MitmConstants.DEFAULT_MESSAGE_DIGEST;

        private SecurityProviderTool securityProviderTool = new DefaultSecurityProviderTool();

        private CertificateInfoGenerator certificateInfoGenerator = new HostnameCertificateInfoGenerator();

        private Collection<String> serverCiphers;

        private Collection<String> clientCiphers;

        /**
         * The source of the CA root certificate that will be used to sign the impersonated server certificates. Custom
         * certificates can be used by supplying an implementation of {@link CertificateAndKeySource}, such as
         * {@link net.lightbody.bmp.mitm.PemFileCertificateSource}. Alternatively, a new root certificate can be generated
         * and saved (for later import into browsers) using {@link RootCertificateGenerator}.
         *
         * @param certificateAndKeySource impersonation materials source to use
         */
        public Builder rootCertificateSource(CertificateAndKeySource certificateAndKeySource) {
            this.rootCertificateSource = certificateAndKeySource;
            return this;
        }

        /**
         * The message digest that will be used when signing server certificates with the root certificate's private key.
         */
        public Builder serverMessageDigest(String serverMessageDigest) {
            this.serverMessageDigest = serverMessageDigest;
            return this;
        }

        /**
         * When true, no upstream certificate verification will be performed. <b>This will make it possible for
         * attackers to MITM communications with the upstream server</b>, so use trustAllServers only when testing.
         * Calling this method with 'true' will remove any trustSource set with {@link #trustSource(TrustSource)}.
         * Calling this method with 'false' has no effect unless trustAllServers was previously called with 'true'.
         * To set a specific TrustSource, use {@link #trustSource(TrustSource)}.
         */
        public Builder trustAllServers(boolean trustAllServers) {
            if (trustAllServers) {
                this.trustSource = null;
            } else {
                // if the TrustSource was previously removed, restore it to the default. otherwise keep the existing TrustSource.
                if (this.trustSource == null) {
                    this.trustSource = TrustSource.defaultTrustSource();
                }
            }

            return this;
        }

        /**
         * The TrustSource that supplies the trusted root CAs used to validate upstream servers' certificates.
         */
        public Builder trustSource(TrustSource trustSource) {
            this.trustSource = trustSource;
            return this;
        }

        /**
         * The {@link KeyGenerator} that will be used to generate the server public and private keys.
         */
        public Builder serverKeyGenerator(KeyGenerator serverKeyGenerator) {
            this.serverKeyGenerator = serverKeyGenerator;
            return this;
        }

        /**
         * The concurrency level for the SSLContext cache. Increase this beyond the default value for high-volume proxy servers.
         */
        public Builder cacheConcurrencyLevel(int cacheConcurrencyLevel) {
            this.cacheConcurrencyLevel = cacheConcurrencyLevel;
            return this;
        }

        /**
         * The length of time SSLContexts with forged certificates will be kept in the cache.
         */
        public Builder cacheExpirationInterval(long cacheExpirationInterval, TimeUnit timeUnit) {
            this.cacheExpirationIntervalMs = TimeUnit.MILLISECONDS.convert(cacheExpirationInterval, timeUnit);
            return this;
        }

        /**
         * The {@link CertificateInfoGenerator} that will populate {@link CertificateInfo} objects containing certificate data for
         * forced X509Certificates.
         */
        public Builder certificateInfoGenerator(CertificateInfoGenerator certificateInfoGenerator) {
            this.certificateInfoGenerator = certificateInfoGenerator;
            return this;
        }

        /**
         * The cipher suites allowed on connections to upstream servers. Cipher suite names should be specified in Java
         * format, rather than OpenSSL format (e.g., TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384), even when using OpenSSL.
         * Ciphers will be preferred in the order they are returned by the collection's iterator.
         */
        public Builder serverCiphers(Collection<String> serverCiphers) {
            this.serverCiphers = serverCiphers;
            return this;
        }

        /**
         * The cipher suites allowed on client connections to the proxy. Cipher suite names should be specified in Java
         * format, rather than OpenSSL format (e.g., TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384), even when using OpenSSL.
         * Ciphers will be preferred in the order they are returned by the collection's iterator.
         */
        public Builder clientCiphers(Collection<String> clientCiphers) {
            this.clientCiphers = clientCiphers;
            return this;
        }

        /**
         * The {@link SecurityProviderTool} implementation that will be used to generate certificates.
         */
        public Builder certificateTool(SecurityProviderTool securityProviderTool) {
            this.securityProviderTool = securityProviderTool;
            return this;
        }

        public ImpersonatingMitmManager build() {
            if (clientCiphers == null) {
                clientCiphers = SslUtil.getDefaultCipherList();
            }

            if (serverCiphers == null) {
                serverCiphers = SslUtil.getDefaultCipherList();
            }

            return new ImpersonatingMitmManager(
                    rootCertificateSource,
                    serverKeyGenerator,
                    serverMessageDigest,
                    trustSource,
                    cacheConcurrencyLevel,
                    cacheExpirationIntervalMs,
                    securityProviderTool,
                    certificateInfoGenerator,
                    serverCiphers,
                    clientCiphers
            );
        }
    }
}
