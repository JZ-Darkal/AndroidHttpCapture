package net.lightbody.bmp;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarLog;
import net.lightbody.bmp.core.har.HarNameVersion;
import net.lightbody.bmp.core.har.HarPage;
import net.lightbody.bmp.core.har.PageRefFilteredHar;
import net.lightbody.bmp.filters.AddHeadersFilter;
import net.lightbody.bmp.filters.AutoBasicAuthFilter;
import net.lightbody.bmp.filters.BlacklistFilter;
import net.lightbody.bmp.filters.BrowserMobHttpFilterChain;
import net.lightbody.bmp.filters.HarCaptureFilter;
import net.lightbody.bmp.filters.HttpConnectHarCaptureFilter;
import net.lightbody.bmp.filters.HttpsHostCaptureFilter;
import net.lightbody.bmp.filters.HttpsOriginalHostCaptureFilter;
import net.lightbody.bmp.filters.LatencyFilter;
import net.lightbody.bmp.filters.RegisterRequestFilter;
import net.lightbody.bmp.filters.RequestFilter;
import net.lightbody.bmp.filters.RequestFilterAdapter;
import net.lightbody.bmp.filters.ResolvedHostnameCacheFilter;
import net.lightbody.bmp.filters.ResponseFilter;
import net.lightbody.bmp.filters.ResponseFilterAdapter;
import net.lightbody.bmp.filters.RewriteUrlFilter;
import net.lightbody.bmp.filters.UnregisterRequestFilter;
import net.lightbody.bmp.filters.WhitelistFilter;
import net.lightbody.bmp.mitm.KeyStoreFileCertificateSource;
import net.lightbody.bmp.mitm.TrustSource;
import net.lightbody.bmp.mitm.keys.ECKeyGenerator;
import net.lightbody.bmp.mitm.keys.RSAKeyGenerator;
import net.lightbody.bmp.mitm.manager.ImpersonatingMitmManager;
import net.lightbody.bmp.proxy.ActivityMonitor;
import net.lightbody.bmp.proxy.BlacklistEntry;
import net.lightbody.bmp.proxy.CaptureType;
import net.lightbody.bmp.proxy.RewriteRule;
import net.lightbody.bmp.proxy.Whitelist;
import net.lightbody.bmp.proxy.auth.AuthType;
import net.lightbody.bmp.proxy.dns.AdvancedHostResolver;
import net.lightbody.bmp.proxy.dns.DelegatingHostResolver;
import net.lightbody.bmp.util.BrowserMobHttpUtil;
import net.lightbody.bmp.util.BrowserMobProxyUtil;
import org.littleshoot.proxy.ChainedProxy;
import org.littleshoot.proxy.ChainedProxyAdapter;
import org.littleshoot.proxy.ChainedProxyManager;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersSource;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.HttpProxyServerBootstrap;
import org.littleshoot.proxy.MitmManager;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.littleshoot.proxy.impl.ProxyUtils;
import org.littleshoot.proxy.impl.ThreadPoolConfiguration;
import org.littleshoot.proxy.mitm.Authority;
import org.littleshoot.proxy.mitm.CertificateSniffingMitmManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

/**
 * A LittleProxy-based implementation of {@link net.lightbody.bmp.BrowserMobProxy}.
 */
public class BrowserMobProxyServer implements BrowserMobProxy {
    private static final Logger log = LoggerFactory.getLogger(BrowserMobProxyServer.class);

    private static final HarNameVersion HAR_CREATOR_VERSION = new HarNameVersion("BrowserMob Proxy", BrowserMobProxyUtil.getVersionString());

    /* Default MITM resources */
    private static final String RSA_KEYSTORE_RESOURCE = "/sslSupport/ca-keystore-rsa.p12";
    private static final String EC_KEYSTORE_RESOURCE = "/sslSupport/ca-keystore-ec.p12";
    private static final String KEYSTORE_TYPE = "PKCS12";
    private static final String KEYSTORE_PRIVATE_KEY_ALIAS = "key";
    private static final String KEYSTORE_PASSWORD = "password";

    /**
     * The default pseudonym to use when adding the Via header to proxied requests.
     */
    public static final String VIA_HEADER_ALIAS = "browsermobproxy";

    /**
     * True only after the proxy has been successfully started.
     */
    private final AtomicBoolean started = new AtomicBoolean(false);

    /**
     * True only after the proxy has been successfully started, then successfully stopped or aborted.
     */
    private final AtomicBoolean stopped = new AtomicBoolean(false);

    /**
     * Tracks the current page count, for use when auto-generating HAR page names.
     */
    private final AtomicInteger harPageCount = new AtomicInteger(0);

    /**
     * When true, MITM will be disabled. The proxy will no longer intercept HTTPS requests, but they will still be proxied.
     */
    private volatile boolean mitmDisabled = false;

    /**
     * The MITM manager that will be used for HTTPS requests.
     */
    private volatile MitmManager mitmManager;

    /**
     * The list of filterFactories that will generate the filters that implement browsermob-proxy behavior.
     */
    private final List<HttpFiltersSource> filterFactories = new CopyOnWriteArrayList<>();

    /**
     * List of rejected URL patterns
     */
    private volatile Collection<BlacklistEntry> blacklistEntries = new CopyOnWriteArrayList<>();

    /**
     * List of URLs to rewrite
     */
    private volatile CopyOnWriteArrayList<RewriteRule> rewriteRules = new CopyOnWriteArrayList<>();

    /**
     * The LittleProxy instance that performs all proxy operations.
     */
    private volatile HttpProxyServer proxyServer;

    /**
     * No capture types are enabled by default.
     */
    private volatile EnumSet<CaptureType> harCaptureTypes = EnumSet.noneOf(CaptureType.class);

    /**
     * The current HAR being captured.
     */
    private volatile Har har;
    /**
     * The current HarPage to which new requests will be associated.
     */
    private volatile HarPage currentHarPage;
    /**
     * Maximum bandwidth to consume when reading responses from servers.
     */
    private volatile long readBandwidthLimitBps;
    /**
     * Maximum bandwidth to consume when writing requests to servers.
     */
    private volatile long writeBandwidthLimitBps;
    /**
     * List of accepted URL patterns. Unlisted URL patterns will be rejected with the response code contained in the Whitelist.
     */
    private final AtomicReference<Whitelist> whitelist = new AtomicReference<>(Whitelist.WHITELIST_DISABLED);

    /**
     * Additional headers that will be sent with every request. The map is declared as a ConcurrentMap to indicate that writes may be performed
     * by other threads concurrently (e.g. due to an incoming REST call), but the concurrencyLevel is set to 1 because modifications to the
     * additionalHeaders are rare, and in most cases happen only once, at start-up.
     */
    private volatile ConcurrentMap<String, String> additionalHeaders = new MapMaker().concurrencyLevel(1).makeMap();

    /**
     * The amount of time to wait while connecting to a server.
     */
    private volatile int connectTimeoutMs;

    /**
     * The amount of time a connection to a server can remain idle while receiving data from the server.
     */
    private volatile int idleConnectionTimeoutSec;

    /**
     * The amount of time to wait before forwarding the response to the client.
     */
    private volatile int latencyMs;

    /**
     * Set to true once the HAR capture filter has been added to the filter chain.
     */
    private final AtomicBoolean harCaptureFilterEnabled = new AtomicBoolean(false);

    /**
     * Set to true when LittleProxy has been bootstrapped with the default chained proxy. This allows modifying the chained proxy
     * after the proxy has been started.
     */
    private final AtomicBoolean bootstrappedWithDefaultChainedProxy = new AtomicBoolean(false);

    /**
     * The address of an upstream chained proxy to route traffic through.
     */
    private volatile InetSocketAddress upstreamProxyAddress;

    /**
     * The chained proxy manager that manages upstream proxies.
     */
    private volatile ChainedProxyManager chainedProxyManager;

    /**
     * The address of the network interface from which the proxy will initiate connections.
     */
    private volatile InetAddress serverBindAddress;

    /**
     * The TrustSource that will be used to validate servers' certificates. If null, will not validate server certificates.
     */
    private volatile TrustSource trustSource = TrustSource.defaultTrustSource();

    /**
     * When true, use Elliptic Curve keys and certificates when impersonating upstream servers.
     */
    private volatile boolean useEcc = false;

    /**
     * Resolver to use when resolving hostnames to IP addresses. This is a bridge between {@link org.littleshoot.proxy.HostResolver} and
     * {@link net.lightbody.bmp.proxy.dns.AdvancedHostResolver}. It allows the resolvers to be changed on-the-fly without re-bootstrapping the
     * littleproxy server. The default resolver (native JDK resolver) can be changed using {@link #setHostNameResolver(net.lightbody.bmp.proxy.dns.AdvancedHostResolver)} and
     * supplying one of the pre-defined resolvers in {@link ClientUtil}, such as {@link ClientUtil#createDnsJavaWithNativeFallbackResolver()}
     * or {@link ClientUtil#createDnsJavaResolver()}. You can also build your own resolver, or use {@link net.lightbody.bmp.proxy.dns.ChainedHostResolver}
     * to chain together multiple DNS resolvers.
     */
    private final DelegatingHostResolver delegatingResolver = new DelegatingHostResolver(ClientUtil.createNativeCacheManipulatingResolver());

    private final ActivityMonitor activityMonitor = new ActivityMonitor();

    /**
     * The acceptor and worker thread configuration for the Netty thread pools.
     */
    private volatile ThreadPoolConfiguration threadPoolConfiguration;

    /**
     * A mapping of hostnames to base64-encoded Basic auth credentials that will be added to the Authorization header for
     * matching requests.
     */
    private final ConcurrentMap<String, String> basicAuthCredentials = new MapMaker()
            .concurrencyLevel(1)
            .makeMap();

    /**
     * Base64-encoded credentials to use to authenticate with the upstream proxy.
     */
    private volatile String chainedProxyCredentials;

    public BrowserMobProxyServer() {
    }

    @Override
    public void start(int port, InetAddress clientBindAddress, InetAddress serverBindAddress) {
        boolean notStarted = started.compareAndSet(false, true);
        if (!notStarted) {
            throw new IllegalStateException("Proxy server is already started. Not restarting.");
        }

        InetSocketAddress clientBindSocket;
        if (clientBindAddress == null) {
            // if no client bind address was specified, bind to the wildcard address
            clientBindSocket = new InetSocketAddress(port);
        } else {
            clientBindSocket = new InetSocketAddress(clientBindAddress, port);
        }

        this.serverBindAddress = serverBindAddress;

        // initialize all the default BrowserMob filter factories that provide core BMP functionality
        addBrowserMobFilters();

        HttpProxyServerBootstrap bootstrap = DefaultHttpProxyServer.bootstrap()
                .withFiltersSource(new HttpFiltersSource() {
                    @Override
                    public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext channelHandlerContext) {
                        return new BrowserMobHttpFilterChain(BrowserMobProxyServer.this, originalRequest, channelHandlerContext);
                    }

                    @Override
                    public int getMaximumRequestBufferSizeInBytes() {
                        return getMaximumRequestBufferSize();
                    }

                    @Override
                    public int getMaximumResponseBufferSizeInBytes() {
                        return getMaximumResponseBufferSize();
                    }
                })
                .withServerResolver(delegatingResolver)
                .withAddress(clientBindSocket)
                .withConnectTimeout(connectTimeoutMs)
                .withIdleConnectionTimeout(idleConnectionTimeoutSec)
                .withProxyAlias(VIA_HEADER_ALIAS);

        if (serverBindAddress != null) {
            bootstrap.withNetworkInterface(new InetSocketAddress(serverBindAddress, 0));
        }


        if (!mitmDisabled) {
//            if (mitmManager == null) {
//                mitmManager = ImpersonatingMitmManager.builder()
//                        .rootCertificateSource(new KeyStoreFileCertificateSource(
//                                KEYSTORE_TYPE,
//                                useEcc ? EC_KEYSTORE_RESOURCE : RSA_KEYSTORE_RESOURCE,
//                                KEYSTORE_PRIVATE_KEY_ALIAS,
//                                KEYSTORE_PASSWORD))
//                        .serverKeyGenerator(useEcc ? new ECKeyGenerator() : new RSAKeyGenerator())
//                        .trustSource(trustSource)
//                        .build();
//            }
//
//            bootstrap.withManInTheMiddle(mitmManager);

            try {
                bootstrap.withManInTheMiddle(new CertificateSniffingMitmManager(
                        new Authority()));
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        if (readBandwidthLimitBps > 0 || writeBandwidthLimitBps > 0) {
            bootstrap.withThrottling(readBandwidthLimitBps, writeBandwidthLimitBps);
        }

        if (chainedProxyManager != null) {
            bootstrap.withChainProxyManager(chainedProxyManager);
        } else if (upstreamProxyAddress != null) {
            // indicate that the proxy was bootstrapped with the default chained proxy manager, which allows changing the
            // chained proxy after the proxy is started.
            bootstrappedWithDefaultChainedProxy.set(true);

            bootstrap.withChainProxyManager(new ChainedProxyManager() {
                @Override
                public void lookupChainedProxies(HttpRequest httpRequest, Queue<ChainedProxy> chainedProxies) {
                    final InetSocketAddress upstreamProxy = upstreamProxyAddress;
                    if (upstreamProxy != null) {
                        chainedProxies.add(new ChainedProxyAdapter() {
                            @Override
                            public InetSocketAddress getChainedProxyAddress() {
                                return upstreamProxy;
                            }

                            @Override
                            public void filterRequest(HttpObject httpObject) {
                                String chainedProxyAuth = chainedProxyCredentials;
                                if (chainedProxyAuth != null) {
                                    if (httpObject instanceof HttpRequest) {
                                        HttpHeaders.addHeader((HttpRequest)httpObject, HttpHeaders.Names.PROXY_AUTHORIZATION, "Basic " + chainedProxyAuth);
                                    }
                                }
                            }
                        });
                    }
                }
            });
        }

        if (threadPoolConfiguration != null) {
            bootstrap.withThreadPoolConfiguration(threadPoolConfiguration);
        }

        proxyServer = bootstrap.start();
    }

    @Override
    public boolean isStarted() {
        return started.get();
    }

    @Override
    public void start(int port) {
        this.start(port, null, null);
    }

    @Override
    public void start(int port, InetAddress bindAddress) {
        this.start(port, bindAddress, null);

    }

    @Override
    public void start() {
        this.start(0);
    }

    @Override
    public void stop() {
        stop(true);
    }

    @Override
    public void abort() {
        stop(false);
    }

    protected void stop(boolean graceful) {
        if (isStarted()) {
            if (stopped.compareAndSet(false, true)) {
                if (proxyServer != null) {
                    if (graceful) {
                        proxyServer.stop();
                    } else {
                        proxyServer.abort();
                    }
                } else {
                    log.warn("Attempted to stop proxy server, but proxy was never successfully started.");
                }
            } else {
                throw new IllegalStateException("Proxy server is already stopped. Cannot re-stop.");
            }
        } else {
            throw new IllegalStateException("Proxy server has not been started");
        }
    }

    @Override
    public InetAddress getClientBindAddress() {
        if (started.get()) {
            return proxyServer.getListenAddress().getAddress();
        } else {
            return null;
        }
    }

    @Override
    public int getPort() {
        if (started.get()) {
            return proxyServer.getListenAddress().getPort();
        } else {
            return 0;
        }
    }

    @Override
    public InetAddress getServerBindAddress() {
        return serverBindAddress;
    }

    @Override
    public Har getHar() {
        return har;
    }

    @Override
    public Har getHar(Set<String> pageRef) {
        return new PageRefFilteredHar(getHar(), pageRef);
    }

    @Override
    public Har getHar(String pageRef) {
        return new PageRefFilteredHar(getHar(), pageRef);
    }

    @Override
    public Har newHar() {
        return newHar(null);
    }

    @Override
    public Har newHar(String initialPageRef) {
        return newHar(initialPageRef, null);
    }

    @Override
    public Har newHar(String initialPageRef, String initialPageTitle) {
        Har oldHar = getHar();

        addHarCaptureFilter();

        harPageCount.set(0);

        this.har = new Har(new HarLog(HAR_CREATOR_VERSION,this));

        newPage(initialPageRef, initialPageTitle);

        return oldHar;
    }

    @Override
    public void setHarCaptureTypes(Set<CaptureType> harCaptureSettings) {
        if (harCaptureSettings == null || harCaptureSettings.isEmpty()) {
            harCaptureTypes = EnumSet.noneOf(CaptureType.class);
        } else {
            harCaptureTypes = EnumSet.copyOf(harCaptureSettings);
        }
    }

    @Override
    public void setHarCaptureTypes(CaptureType... captureTypes) {
        if (captureTypes == null) {
            setHarCaptureTypes(EnumSet.noneOf(CaptureType.class));
        } else {
            setHarCaptureTypes(EnumSet.copyOf(Arrays.asList(captureTypes)));
        }
    }

    @Override
    public EnumSet<CaptureType> getHarCaptureTypes() {
        return EnumSet.copyOf(harCaptureTypes);
    }

    @Override
    public void enableHarCaptureTypes(Set<CaptureType> captureTypes) {
        harCaptureTypes.addAll(captureTypes);
    }

    @Override
    public void enableHarCaptureTypes(CaptureType... captureTypes) {
        if (captureTypes == null) {
            enableHarCaptureTypes(EnumSet.noneOf(CaptureType.class));
        } else {
            enableHarCaptureTypes(EnumSet.copyOf(Arrays.asList(captureTypes)));
        }
    }

    @Override
    public void disableHarCaptureTypes(Set<CaptureType> captureTypes) {
        harCaptureTypes.removeAll(captureTypes);

    }

    @Override
    public void disableHarCaptureTypes(CaptureType... captureTypes) {
        if (captureTypes == null) {
            disableHarCaptureTypes(EnumSet.noneOf(CaptureType.class));
        } else {
            disableHarCaptureTypes(EnumSet.copyOf(Arrays.asList(captureTypes)));
        }
    }

    @Override
    public Har newPage() {
        return newPage(null);
    }

    @Override
    public Har newPage(String pageRef) {
        return newPage(pageRef, null);
    }

    @Override
    public Har newPage(String pageRef, String pageTitle) {
        if (har == null) {
            throw new IllegalStateException("No HAR exists for this proxy. Use newHar() to create a new HAR before calling newPage().");
        }

        Har endOfPageHar = null;

        if (currentHarPage != null) {
            String currentPageRef = currentHarPage.getId();

            // end the previous page, so that page-wide timings are populated
            endPage();

            // the interface requires newPage() to return the Har as it was immediately after the previous page was ended.
            endOfPageHar = BrowserMobProxyUtil.copyHarThroughPageRef(har, currentPageRef);
        }

        if (pageRef == null) {
            pageRef = "Page " + harPageCount.getAndIncrement();
        }

        if (pageTitle == null) {
            pageTitle = pageRef;
        }

        HarPage newPage = new HarPage(pageRef, pageTitle);
        har.getLog().addPage(newPage);

        currentHarPage = newPage;

        return endOfPageHar;
    }

    @Override
    public Har endHar() {
        Har oldHar = getHar();

        // end the page and populate timings
        endPage();

        this.har = null;

        return oldHar;
    }

    @Override
    public void setReadBandwidthLimit(long bytesPerSecond) {
        this.readBandwidthLimitBps = bytesPerSecond;

        if (isStarted()) {
            proxyServer.setThrottle(this.readBandwidthLimitBps, this.writeBandwidthLimitBps);
        }
    }

    @Override
    public long getReadBandwidthLimit() {
        return readBandwidthLimitBps;
    }

    @Override
    public void setWriteBandwidthLimit(long bytesPerSecond) {
        this.writeBandwidthLimitBps = bytesPerSecond;

        if (isStarted()) {
            proxyServer.setThrottle(this.readBandwidthLimitBps, this.writeBandwidthLimitBps);
        }
    }

    @Override
    public long getWriteBandwidthLimit() {
        return writeBandwidthLimitBps;
    }

    public void endPage() {
        if (har == null) {
            throw new IllegalStateException("No HAR exists for this proxy. Use newHar() to create a new HAR.");
        }

        HarPage previousPage = this.currentHarPage;
        this.currentHarPage = null;

        if (previousPage == null) {
            return;
        }

        previousPage.getPageTimings().setOnLoad(new Date().getTime() - previousPage.getStartedDateTime().getTime());
    }

    @Override
    public void addHeaders(Map<String, String> headers) {
        ConcurrentMap<String, String> newHeaders = new MapMaker().concurrencyLevel(1).makeMap();
        newHeaders.putAll(headers);

        this.additionalHeaders = newHeaders;
    }

    @Override
    public void setLatency(long latency, TimeUnit timeUnit) {
        this.latencyMs = (int) TimeUnit.MILLISECONDS.convert(latency, timeUnit);
    }

    @Override
    public void autoAuthorization(String domain, String username, String password, AuthType authType) {
        switch (authType) {
            case BASIC:
                // base64 encode the "username:password" string
                String base64EncodedCredentials = BrowserMobHttpUtil.base64EncodeBasicCredentials(username, password);

                basicAuthCredentials.put(domain, base64EncodedCredentials);
                break;

            default:
                throw new UnsupportedOperationException("AuthType " + authType + " is not supported for HTTP Authorization");
        }
    }

    @Override
    public void stopAutoAuthorization(String domain) {
        basicAuthCredentials.remove(domain);
    }

    @Override
    public void chainedProxyAuthorization(String username, String password, AuthType authType) {
        switch (authType) {
            case BASIC:
                chainedProxyCredentials = BrowserMobHttpUtil.base64EncodeBasicCredentials(username, password);
                break;

            default:
                throw new UnsupportedOperationException("AuthType " + authType + " is not supported for Proxy Authorization");
        }
    }

    @Override
    public void setConnectTimeout(int connectTimeout, TimeUnit timeUnit) {
        this.connectTimeoutMs = (int) TimeUnit.MILLISECONDS.convert(connectTimeout, timeUnit);

        if (isStarted()) {
            proxyServer.setConnectTimeout((int) TimeUnit.MILLISECONDS.convert(connectTimeout, timeUnit));
        }
    }

    /**
     * The LittleProxy implementation only allows idle connection timeouts to be specified in seconds. idleConnectionTimeouts greater than
     * 0 but less than 1 second will be set to 1 second; otherwise, values will be truncated (i.e. 1500ms will become 1s).
     */
    @Override
    public void setIdleConnectionTimeout(int idleConnectionTimeout, TimeUnit timeUnit) {
        long timeout = TimeUnit.SECONDS.convert(idleConnectionTimeout, timeUnit);
        if (timeout == 0 && idleConnectionTimeout > 0) {
            this.idleConnectionTimeoutSec = 1;
        } else {
            this.idleConnectionTimeoutSec = (int) timeout;
        }

        if (isStarted()) {
            proxyServer.setIdleConnectionTimeout(idleConnectionTimeoutSec);
        }
    }

    @Override
    public void setRequestTimeout(int requestTimeout, TimeUnit timeUnit) {
        //TODO: implement Request Timeouts using LittleProxy. currently this only sets an idle connection timeout, if the idle connection
        // timeout is higher than the specified requestTimeout.
        if (idleConnectionTimeoutSec == 0 || idleConnectionTimeoutSec > TimeUnit.SECONDS.convert(requestTimeout, timeUnit)) {
            setIdleConnectionTimeout(requestTimeout, timeUnit);
        }
    }

    @Override
    public void rewriteUrl(String pattern, String replace) {
        rewriteRules.add(new RewriteRule(pattern, replace));
    }

    @Override
    public void rewriteUrls(Map<String, String> rewriteRules) {
        List<RewriteRule> newRules = new ArrayList<>(rewriteRules.size());
        for (Map.Entry<String, String> rewriteRule : rewriteRules.entrySet()) {
            RewriteRule newRule = new RewriteRule(rewriteRule.getKey(), rewriteRule.getValue());
            newRules.add(newRule);
        }

        this.rewriteRules = new CopyOnWriteArrayList<>(newRules);
    }

    @Override
    public void clearRewriteRules() {
        rewriteRules.clear();
    }

    @Override
    public void blacklistRequests(String pattern, int responseCode) {
        blacklistEntries.add(new BlacklistEntry(pattern, responseCode));
    }

    @Override
    public void blacklistRequests(String pattern, int responseCode, String method) {
        blacklistEntries.add(new BlacklistEntry(pattern, responseCode, method));
    }

    @Override
    public void setBlacklist(Collection<BlacklistEntry> blacklist) {
        this.blacklistEntries = new CopyOnWriteArrayList<>(blacklist);
    }

    @Override
    public Collection<BlacklistEntry> getBlacklist() {
        return Collections.unmodifiableCollection(blacklistEntries);
    }

    @Override
    public boolean isWhitelistEnabled() {
        return whitelist.get().isEnabled();
    }

    @Override
    public Collection<String> getWhitelistUrls() {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        for (Pattern pattern : whitelist.get().getPatterns()) {
            builder.add(pattern.pattern());
        }

        return builder.build();
    }

    @Override
    public int getWhitelistStatusCode() {
        return whitelist.get().getStatusCode();
    }

    @Override
    public void clearBlacklist() {
        blacklistEntries.clear();
    }

    @Override
    public void whitelistRequests(Collection<String> urlPatterns, int statusCode) {
        this.whitelist.set(new Whitelist(urlPatterns, statusCode));
    }

    @Override
    public void addWhitelistPattern(String urlPattern) {
        // to make sure this method is threadsafe, we need to guarantee that the "snapshot" of the whitelist taken at the beginning
        // of the method has not been replaced by the time we have constructed a new whitelist at the end of the method
        boolean whitelistUpdated = false;
        while (!whitelistUpdated) {
            Whitelist currentWhitelist = this.whitelist.get();
            if (!currentWhitelist.isEnabled()) {
                throw new IllegalStateException("Whitelist is disabled. Cannot add patterns to a disabled whitelist.");
            }

            // retrieve the response code and list of patterns from the current whitelist, the construct a new list of patterns that contains
            // all of the old whitelist's patterns + this new pattern
            int statusCode = currentWhitelist.getStatusCode();
            List<String> newPatterns = new ArrayList<>(currentWhitelist.getPatterns().size() + 1);
            for (Pattern pattern : currentWhitelist.getPatterns()) {
                newPatterns.add(pattern.pattern());
            }
            newPatterns.add(urlPattern);

            // create a new (immutable) Whitelist object with the new pattern list and status code
            Whitelist newWhitelist = new Whitelist(newPatterns, statusCode);

            // replace the current whitelist with the new whitelist only if the current whitelist has not changed since we started
            whitelistUpdated = this.whitelist.compareAndSet(currentWhitelist, newWhitelist);
        }
    }

    /**
     * Whitelist the specified request patterns, returning the specified responseCode for non-whitelisted
     * requests.
     *
     * @param patterns     regular expression strings matching URL patterns to whitelist. if empty or null,
     *                     the whitelist will be enabled but will not match any URLs.
     * @param responseCode the HTTP response code to return for non-whitelisted requests
     */
    public void whitelistRequests(String[] patterns, int responseCode) {
        if (patterns == null || patterns.length == 0) {
            this.enableEmptyWhitelist(responseCode);
        } else {
            this.whitelistRequests(Arrays.asList(patterns), responseCode);
        }
    }

    @Override
    public void enableEmptyWhitelist(int statusCode) {
        whitelist.set(new Whitelist(statusCode));
    }

    @Override
    public void disableWhitelist() {
        whitelist.set(Whitelist.WHITELIST_DISABLED);
    }

    @Override
    public void addHeader(String name, String value) {
        additionalHeaders.put(name, value);
    }

    @Override
    public void removeHeader(String name) {
        additionalHeaders.remove(name);
    }

    @Override
    public void removeAllHeaders() {
        additionalHeaders.clear();
    }

    @Override
    public Map<String, String> getAllHeaders() {
        return ImmutableMap.copyOf(additionalHeaders);
    }

    @Override
    public void setHostNameResolver(AdvancedHostResolver resolver) {
        delegatingResolver.setResolver(resolver);
    }

    @Override
    public AdvancedHostResolver getHostNameResolver() {
        return delegatingResolver.getResolver();
    }

    @Override
    public boolean waitForQuiescence(long quietPeriod, long timeout, TimeUnit timeUnit) {
        return activityMonitor.waitForQuiescence(quietPeriod, timeout, timeUnit);
    }

    /**
     * Instructs this proxy to route traffic through an upstream proxy.
     *
     * <b>Note:</b> Using {@link #setChainedProxyManager(ChainedProxyManager)} will supersede any value set by this method. A chained
     * proxy must be set before the proxy is started, though it can be changed after the proxy is started.
     *
     * @param chainedProxyAddress address of the upstream proxy
     */
    @Override
    public void setChainedProxy(InetSocketAddress chainedProxyAddress) {
        if (isStarted() && !bootstrappedWithDefaultChainedProxy.get()) {
            throw new IllegalStateException("Cannot set a chained proxy after the proxy is started if the proxy was started without a chained proxy.");
        }

        upstreamProxyAddress = chainedProxyAddress;
    }

    @Override
    public InetSocketAddress getChainedProxy() {
        return upstreamProxyAddress;
    }

    /**
     * Allows access to the LittleProxy {@link ChainedProxyManager} for fine-grained control of the chained proxies. To enable a single
     * chained proxy, {@link BrowserMobProxy#setChainedProxy(InetSocketAddress)} is generally more convenient.
     *
     * <b>Note:</b> The chained proxy manager must be enabled before calling {@link #start()}.
     *
     * @param chainedProxyManager chained proxy manager to enable
     */
    public void setChainedProxyManager(ChainedProxyManager chainedProxyManager) {
        if (isStarted()) {
            throw new IllegalStateException("Cannot configure chained proxy manager after proxy has started.");
        }

        this.chainedProxyManager = chainedProxyManager;
    }

    /**
     * Configures the Netty thread pool used by the LittleProxy back-end. See {@link ThreadPoolConfiguration} for details.
     *
     * @param threadPoolConfiguration thread pool configuration to use
     */
    public void setThreadPoolConfiguration(ThreadPoolConfiguration threadPoolConfiguration) {
        if (isStarted()) {
            throw new IllegalStateException("Cannot configure thread pool after proxy has started.");
        }

        this.threadPoolConfiguration = threadPoolConfiguration;
    }

    @Override
    public void addFirstHttpFilterFactory(HttpFiltersSource filterFactory) {
        filterFactories.add(0, filterFactory);
    }

    @Override
    public void addLastHttpFilterFactory(HttpFiltersSource filterFactory) {
        filterFactories.add(filterFactory);
    }

    /**
     * <b>Note:</b> The current implementation of this method forces a maximum response size of 2 MiB. To adjust the maximum response size, or
     * to disable aggregation (which disallows access to the {@link net.lightbody.bmp.util.HttpMessageContents}), you may add the filter source
     * directly: <code>addFirstHttpFilterFactory(new ResponseFilterAdapter.FilterSource(filter, bufferSizeInBytes));</code>
     */
    @Override
    public void addResponseFilter(ResponseFilter filter) {
        addLastHttpFilterFactory(new ResponseFilterAdapter.FilterSource(filter));
    }

    /**
     * <b>Note:</b> The current implementation of this method forces a maximum request size of 2 MiB. To adjust the maximum request size, or
     * to disable aggregation (which disallows access to the {@link net.lightbody.bmp.util.HttpMessageContents}), you may add the filter source
     * directly: <code>addFirstHttpFilterFactory(new RequestFilterAdapter.FilterSource(filter, bufferSizeInBytes));</code>
     */
    @Override
    public void addRequestFilter(RequestFilter filter) {
        addFirstHttpFilterFactory(new RequestFilterAdapter.FilterSource(filter));
    }

    @Override
    public Map<String, String> getRewriteRules() {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        for (RewriteRule rewriteRule : rewriteRules) {
            builder.put(rewriteRule.getPattern().pattern(), rewriteRule.getReplace());
        }

        return builder.build();
    }

    @Override
    public void removeRewriteRule(String urlPattern) {
        // normally removing elements from the list we are iterating over would not be possible, but since this is a CopyOnWriteArrayList
        // the iterator it returns is a "snapshot" of the list that will not be affected by removal (and that does not support removal, either)
        for (RewriteRule rewriteRule : rewriteRules) {
            if (rewriteRule.getPattern().pattern().equals(urlPattern)) {
                rewriteRules.remove(rewriteRule);
            }
        }
    }

    public boolean isStopped() {
        return stopped.get();
    }

    public HarPage getCurrentHarPage() {
        return currentHarPage;
    }

    public void addHttpFilterFactory(HttpFiltersSource filterFactory) {
        filterFactories.add(filterFactory);
    }

    public List<HttpFiltersSource> getFilterFactories() {
        return filterFactories;
    }

    @Override
    public void setMitmDisabled(boolean mitmDisabled) throws IllegalStateException {
        if (isStarted()) {
            throw new IllegalStateException("Cannot disable MITM after the proxy has been started");
        }

        this.mitmDisabled = mitmDisabled;
    }

    @Override
    public void setMitmManager(MitmManager mitmManager) {
        this.mitmManager = mitmManager;
    }

    @Override
    public void setTrustAllServers(boolean trustAllServers) {
        if (isStarted()) {
            throw new IllegalStateException("Cannot disable upstream server verification after the proxy has been started");
        }

        if (trustAllServers) {
            trustSource = null;
        } else {
            if (trustSource == null) {
                trustSource = TrustSource.defaultTrustSource();
            }
        }
    }

    @Override
    public void setTrustSource(TrustSource trustSource) {
        if (isStarted()) {
            throw new IllegalStateException("Cannot change TrustSource after proxy has been started");
        }

        this.trustSource = trustSource;
    }

    public boolean isMitmDisabled() {
        return this.mitmDisabled;
    }

    public void setUseEcc(boolean useEcc) {
        this.useEcc = useEcc;
    }

    /**
     * Adds the basic browsermob-proxy filters, except for the relatively-expensive HAR capture filter.
     */
    protected void addBrowserMobFilters() {
        addHttpFilterFactory(new HttpFiltersSourceAdapter() {
            @Override
            public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                return new ResolvedHostnameCacheFilter(originalRequest, ctx);
            }
        });

        addHttpFilterFactory(new HttpFiltersSourceAdapter() {
            @Override
            public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                return new RegisterRequestFilter(originalRequest, ctx, activityMonitor);
            }
        });

        addHttpFilterFactory(new HttpFiltersSourceAdapter() {
            @Override
            public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                return new HttpsOriginalHostCaptureFilter(originalRequest, ctx);
            }
        });

        addHttpFilterFactory(new HttpFiltersSourceAdapter() {
            @Override
            public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                return new BlacklistFilter(originalRequest, ctx, getBlacklist());
            }
        });

        addHttpFilterFactory(new HttpFiltersSourceAdapter() {
            @Override
            public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                Whitelist currentWhitelist = whitelist.get();
                return new WhitelistFilter(originalRequest, ctx, isWhitelistEnabled(), currentWhitelist.getStatusCode(), currentWhitelist.getPatterns());
            }
        });

        addHttpFilterFactory(new HttpFiltersSourceAdapter() {
            @Override
            public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                return new AutoBasicAuthFilter(originalRequest, ctx, basicAuthCredentials);
            }
        });

        addHttpFilterFactory(new HttpFiltersSourceAdapter() {
            @Override
            public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                return new RewriteUrlFilter(originalRequest, ctx, rewriteRules);
            }
        });

        addHttpFilterFactory(new HttpFiltersSourceAdapter() {
            @Override
            public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                return new HttpsHostCaptureFilter(originalRequest, ctx);
            }
        });

        addHttpFilterFactory(new HttpFiltersSourceAdapter() {
            @Override
            public HttpFilters filterRequest(HttpRequest originalRequest) {
                return new AddHeadersFilter(originalRequest, additionalHeaders);
            }
        });

        addHttpFilterFactory(new HttpFiltersSourceAdapter() {
            @Override
            public HttpFilters filterRequest(HttpRequest originalRequest) {
                return new LatencyFilter(originalRequest, latencyMs);
            }
        });

        addHttpFilterFactory(new HttpFiltersSourceAdapter() {
            @Override
            public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                return new UnregisterRequestFilter(originalRequest, ctx, activityMonitor);
            }
        });
    }

    private int getMaximumRequestBufferSize() {
        int maxBufferSize = 0;
        for (HttpFiltersSource source : filterFactories) {
            int requestBufferSize = source.getMaximumRequestBufferSizeInBytes();
            if (requestBufferSize > maxBufferSize) {
                maxBufferSize = requestBufferSize;
            }
        }

        return maxBufferSize;
    }

    private int getMaximumResponseBufferSize() {
        int maxBufferSize = 0;
        for (HttpFiltersSource source : filterFactories) {
            int requestBufferSize = source.getMaximumResponseBufferSizeInBytes();
            if (requestBufferSize > maxBufferSize) {
                maxBufferSize = requestBufferSize;
            }
        }

        return maxBufferSize;
    }

    /**
     * Enables the HAR capture filter if it has not already been enabled. The filter will be added to the end of the filter chain.
     * The HAR capture filter is relatively expensive, so this method is only called when a HAR is requested.
     */
    protected void addHarCaptureFilter() {
        if (harCaptureFilterEnabled.compareAndSet(false, true)) {
            // the HAR capture filter is (relatively) expensive, so only enable it when a HAR is being captured. furthermore,
            // restricting the HAR capture filter to requests where the HAR exists, as well as  excluding HTTP CONNECTs
            // from the HAR capture filter, greatly simplifies the filter code.
            addHttpFilterFactory(new HttpFiltersSourceAdapter() {
                @Override
                public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                    Har har = getHar();
                    if (har != null && !ProxyUtils.isCONNECT(originalRequest)) {
                        return new HarCaptureFilter(originalRequest, ctx, har, getCurrentHarPage() == null ? null : getCurrentHarPage().getId(), getHarCaptureTypes());
                    } else {
                        return null;
                    }
                }
            });

            // HTTP CONNECTs are a special case, since they require special timing and error handling
            addHttpFilterFactory(new HttpFiltersSourceAdapter() {
                @Override
                public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                    Har har = getHar();
                    if (har != null && ProxyUtils.isCONNECT(originalRequest)) {
                        return new HttpConnectHarCaptureFilter(originalRequest, ctx, har, getCurrentHarPage() == null ? null : getCurrentHarPage().getId());
                    } else {
                        return null;
                    }
                }
            });
        }
    }
}
