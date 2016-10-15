package net.lightbody.bmp.mitm.util;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.io.CharStreams;

import net.lightbody.bmp.mitm.TrustSource;
import net.lightbody.bmp.mitm.exception.SslContextInitializationException;
import net.lightbody.bmp.mitm.trustmanager.InsecureTrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;

import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SupportedCipherSuiteFilter;

/**
 * Utility for creating SSLContexts.
 */
public class SslUtil {
    private static final Logger log = LoggerFactory.getLogger(SslUtil.class);

    /**
     * Classpath resource containing a list of default ciphers.
     */
    private static final String DEFAULT_CIPHERS_LIST_RESOURCE = "/default-ciphers.txt";

    /**
     * The default cipher list to prefer when creating client or server connections. Stored as a lazily-loaded singleton
     * due to the relatively expensive initialization time, especially when determining the enabled JDK ciphers.
     * If OpenSsl support is enabled, this simply returns the list provided by {@link #getBuiltInCipherList()}.
     * If OpenSsl is not available, retrieves the default ciphers enabled on java SSLContexts. If the enabled JDK cipher
     * list cannot be read, returns the list provided by {@link #getBuiltInCipherList()}.
     */
    private static final Supplier<List<String>> defaultCipherList = Suppliers.memoize(new Supplier<List<String>>() {
        @Override
        public List<String> get() {
            List<String> ciphers;
            if (OpenSsl.isAvailable()) {
                // TODO: consider switching to the list of all available ciphers using OpenSsl.availableCipherSuites()
                ciphers = getBuiltInCipherList();
            } else {
                ciphers = getEnabledJdkCipherSuites();

                if (ciphers.isEmpty()) {
                    // could not retrieve the list of enabled ciphers from the JDK SSLContext, so use the hard-coded list
                    ciphers = getBuiltInCipherList();
                }
            }

            return ciphers;
        }
    });

    /**
     * Creates a netty SslContext for use when connecting to upstream servers. Retrieves the list of trusted root CAs
     * from the trustSource. When trustSource is true, no upstream certificate verification will be performed.
     * <b>This will make it possible for attackers to MITM communications with the upstream server</b>, so always
     * supply an appropriate trustSource except in extraordinary circumstances (e.g. testing with dynamically-generated
     * certificates).
     *
     * @param cipherSuites    cipher suites to allow when connecting to the upstream server
     * @param trustSource     the trust store that will be used to validate upstream servers' certificates, or null to accept all upstream server certificates
     * @return an SSLContext to connect to upstream servers with
     */
    public static SslContext getUpstreamServerSslContext(Collection<String> cipherSuites, TrustSource trustSource) {
        SslContextBuilder sslContextBuilder = SslContextBuilder.forClient();

        if (trustSource == null) {
            log.warn("Disabling upstream server certificate verification. This will allow attackers to intercept communications with upstream servers.");

            sslContextBuilder.trustManager(InsecureTrustManagerFactory.INSTANCE);
        } else {
            sslContextBuilder.trustManager(trustSource.getTrustedCAs());
        }

        sslContextBuilder.ciphers(cipherSuites, SupportedCipherSuiteFilter.INSTANCE);

        try {
            return sslContextBuilder.build();
        } catch (SSLException e) {
            throw new SslContextInitializationException("Error creating new SSL context for connection to upstream server", e);
        }
    }

    /**
     * Returns the X509Certificate for the server this session is connected to. The certificate may be null.
     *
     * @param sslSession SSL session connected to upstream server
     * @return the X.509 certificate from the upstream server, or null if no certificate is available
     */
    public static X509Certificate getServerCertificate(SSLSession sslSession) {
        Certificate[] peerCertificates;
        try {
            peerCertificates = sslSession.getPeerCertificates();
        } catch (SSLPeerUnverifiedException e) {
            peerCertificates = null;
        }

        if (peerCertificates != null && peerCertificates.length > 0) {
            Certificate peerCertificate = peerCertificates[0];
            if (peerCertificate != null && peerCertificate instanceof X509Certificate) {
                return (X509Certificate) peerCertificates[0];
            }
        }

        // no X.509 certificate was found for this server
        return null;
    }

    /**
     * Returns the list of default "enabled" ciphers for server TLS connections, as reported by the default Java security provider.
     * This is most likely a subset of "available" ciphers.
     *
     * @return list of default server ciphers, or an empty list if the default cipher list cannot be loaded
     */
    public static List<String> getEnabledJdkCipherSuites() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, null);

            String[] defaultCiphers = sslContext.getServerSocketFactory().getDefaultCipherSuites();

            return Arrays.asList(defaultCiphers);
        } catch (Throwable t) {
            log.info("Unable to load default JDK server cipher list from SSLContext");

            // log the actual exception for debugging
            log.debug("An error occurred while initializing an SSLContext or ServerSocketFactory", t);

            return Collections.emptyList();
        }
    }

    /**
     * Returns a reasonable default cipher list for new client and server SSL connections. Not all of the ciphers may be supported
     * by the underlying SSL implementation (OpenSsl or JDK). The default list itself may also vary between OpenSsl and JDK
     * implementations. See {@link #defaultCipherList} for implementation details.
     *
     * @return default ciphers for client and server connections
     */
    public static List<String> getDefaultCipherList() {
        return defaultCipherList.get();
    }

    /**
     * Returns ciphers from the hard-coded list of "reasonable" default ciphers in {@link #DEFAULT_CIPHERS_LIST_RESOURCE}.
     *
     * @return ciphers from the {@link #DEFAULT_CIPHERS_LIST_RESOURCE}
     */
    public static List<String> getBuiltInCipherList() {
        try (InputStream cipherListStream = SslUtil.class.getResourceAsStream(DEFAULT_CIPHERS_LIST_RESOURCE)) {
            if (cipherListStream == null) {
                return Collections.emptyList();
            }

            Reader reader = new InputStreamReader(cipherListStream, Charset.forName("UTF-8"));

            return CharStreams.readLines(reader);
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

}
