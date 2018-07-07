package net.lightbody.bmp.filters;

import com.google.common.cache.CacheBuilder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarRequest;
import net.lightbody.bmp.core.har.HarResponse;
import net.lightbody.bmp.core.har.HarTimings;
import net.lightbody.bmp.filters.support.HttpConnectTiming;
import net.lightbody.bmp.filters.util.HarCaptureUtil;
import net.lightbody.bmp.util.HttpUtil;
import org.littleshoot.proxy.impl.ProxyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * This filter captures HAR data for HTTP CONNECT requests. CONNECTs are "meta" requests that must be made before HTTPS
 * requests, but are not populated as separate requests in the HAR. Most information from HTTP CONNECTs (such as SSL
 * handshake time, dns resolution time, etc.) is populated in the HAR entry for the first "true" request following the
 * CONNECT. This filter captures the timing-related information and makes it available to subsequent filters through
 * static methods. This filter also handles HTTP CONNECT errors and creates HAR entries for those errors, since there
 * would otherwise not be any record in the HAR of the error (if the CONNECT fails, there will be no subsequent "real"
 * request in which to record the error).
 *
 */
public class HttpConnectHarCaptureFilter extends HttpsAwareFiltersAdapter implements ModifiedRequestAwareFilter {
    private static final Logger log = LoggerFactory.getLogger(HttpConnectHarCaptureFilter.class);

    /**
     * The currently active HAR at the time the current request is received.
     */
    private final Har har;

    /**
     * The currently active page ref at the time the current request is received.
     */
    private final String currentPageRef;

    /**
     * The time this CONNECT began. Used to populate the HAR entry in case of failure.
     */
    private volatile Date requestStartTime;

    /**
     * True if this filter instance processed a {@link #proxyToServerResolutionSucceeded(String, java.net.InetSocketAddress)} call, indicating
     * that the hostname was resolved and populated in the HAR (if this is not a CONNECT).
     */
//    private volatile boolean addressResolved = false;
    private volatile InetAddress resolvedAddress;

    /**
     * Populated by proxyToServerResolutionStarted when DNS resolution starts. If any previous filters already resolved the address, their resolution time
     * will not be included in this time. See {@link HarCaptureFilter#dnsResolutionStartedNanos}.
     */
    private volatile long dnsResolutionStartedNanos;

    private volatile long dnsResolutionFinishedNanos;

    private volatile long connectionQueuedNanos;
    private volatile long connectionStartedNanos;
    private volatile long connectionSucceededTimeNanos;
    private volatile long sendStartedNanos;
    private volatile long sendFinishedNanos;

    private volatile long responseReceiveStartedNanos;
    private volatile long sslHandshakeStartedNanos;

    /**
     * The address of the client making the request. Captured in the constructor and used when calculating and capturing ssl handshake and connect
     * timing information for SSL connections.
     */
    private final InetSocketAddress clientAddress;

    /**
     * Stores HTTP CONNECT timing information for this request, if it is an HTTP CONNECT.
     */
    private final HttpConnectTiming httpConnectTiming;

    /**
     * The maximum amount of time to save timing information between an HTTP CONNECT and the subsequent HTTP request. Typically this is done
     * immediately, but if for some reason it is not (e.g. due to a client crash or dropped connection), the timing information will be
     * kept for this long before being evicted to prevent a memory leak. If a subsequent request does come through after eviction, it will still
     * be recorded, but the timing information will not be populated in the HAR.
     */
    private static final int HTTP_CONNECT_TIMING_EVICTION_SECONDS = 60;

    /**
     * Concurrency of the httpConnectTiming map. Should be approximately equal to the maximum number of simultaneous connection
     * attempts (but not necessarily simultaneous connections). A lower value will inhibit performance.
     * TODO: tune this value for a large number of concurrent requests. develop a non-cache-based mechanism of passing ssl timings to subsequent requests.
     */
    private static final int HTTP_CONNECT_TIMING_CONCURRENCY_LEVEL = 50;

    /**
     * Stores SSL connection timing information from HTTP CONNNECT requests. This timing information is stored in the first HTTP request
     * after the CONNECT, not in the CONNECT itself, so it needs to be stored across requests.
     *
     * This is the only state stored across multiple requests.
     */
    private static final ConcurrentMap<InetSocketAddress, HttpConnectTiming> httpConnectTimes =
            CacheBuilder.newBuilder()
                    .expireAfterWrite(HTTP_CONNECT_TIMING_EVICTION_SECONDS, TimeUnit.SECONDS)
                    .concurrencyLevel(HTTP_CONNECT_TIMING_CONCURRENCY_LEVEL)
                    .<InetSocketAddress, HttpConnectTiming>build()
                    .asMap();

    private volatile HttpRequest modifiedHttpRequest;

    public HttpConnectHarCaptureFilter(HttpRequest originalRequest, ChannelHandlerContext ctx, Har har, String currentPageRef) {
        super(originalRequest, ctx);

        if (har == null) {
            throw new IllegalStateException("Attempted har capture when har is null");
        }

        if (!ProxyUtils.isCONNECT(originalRequest)) {
            throw new IllegalStateException("Attempted HTTP CONNECT har capture on non-HTTP CONNECT request");
        }

        this.har = har;
        this.currentPageRef = currentPageRef;

        this.clientAddress = (InetSocketAddress) ctx.channel().remoteAddress();

        // create and cache an HTTP CONNECT timing object to capture timing-related information
        this.httpConnectTiming = new HttpConnectTiming();
        httpConnectTimes.put(clientAddress, httpConnectTiming);
    }

    @Override
    public HttpResponse clientToProxyRequest(HttpObject httpObject) {
        if (httpObject instanceof HttpRequest) {
            // store the CONNECT start time in case of failure, so we can populate the HarEntry with it
            requestStartTime = new Date();
        }

        return null;
    }

    @Override
    public void proxyToServerResolutionFailed(String hostAndPort) {
        // since this is a CONNECT, which is not handled by the HarCaptureFilter, we need to create and populate the
        // entire HarEntry and add it to this har.
        HarEntry harEntry = createHarEntryForFailedCONNECT(HarCaptureUtil.getResolutionFailedErrorMessage(hostAndPort));
        har.getLog().addEntry(harEntry);

        // record the amount of time we attempted to resolve the hostname in the HarTimings object
        if (dnsResolutionStartedNanos > 0L) {
            harEntry.getTimings().setDns(System.nanoTime() - dnsResolutionStartedNanos, TimeUnit.NANOSECONDS);
        }

        httpConnectTimes.remove(clientAddress);
    }

    @Override
    public void proxyToServerConnectionFailed() {
        // since this is a CONNECT, which is not handled by the HarCaptureFilter, we need to create and populate the
        // entire HarEntry and add it to this har.
        HarEntry harEntry = createHarEntryForFailedCONNECT(HarCaptureUtil.getConnectionFailedErrorMessage());
        har.getLog().addEntry(harEntry);

        // record the amount of time we attempted to connect in the HarTimings object
        if (connectionStartedNanos > 0L) {
            harEntry.getTimings().setConnect(System.nanoTime() - connectionStartedNanos, TimeUnit.NANOSECONDS);
        }

        httpConnectTimes.remove(clientAddress);
    }

    @Override
    public void proxyToServerConnectionSucceeded(ChannelHandlerContext serverCtx) {
        this.connectionSucceededTimeNanos = System.nanoTime();

        if (connectionStartedNanos > 0L) {
            httpConnectTiming.setConnectTimeNanos(connectionSucceededTimeNanos - connectionStartedNanos);
        } else {
            httpConnectTiming.setConnectTimeNanos(0L);
        }

        if (sslHandshakeStartedNanos > 0L) {
            httpConnectTiming.setSslHandshakeTimeNanos(connectionSucceededTimeNanos - sslHandshakeStartedNanos);
        } else {
            httpConnectTiming.setSslHandshakeTimeNanos(0L);
        }
    }

    @Override
    public void proxyToServerConnectionSSLHandshakeStarted() {
        this.sslHandshakeStartedNanos = System.nanoTime();
    }

    @Override
    public void serverToProxyResponseTimedOut() {
        HarEntry harEntry = createHarEntryForFailedCONNECT(HarCaptureUtil.getResponseTimedOutErrorMessage());
        har.getLog().addEntry(harEntry);

        // include this timeout time in the HarTimings object
        long timeoutTimestampNanos = System.nanoTime();

        // if the proxy started to send the request but has not yet finished, we are currently "sending"
        if (sendStartedNanos > 0L && sendFinishedNanos == 0L) {
            harEntry.getTimings().setSend(timeoutTimestampNanos - sendStartedNanos, TimeUnit.NANOSECONDS);
        }
        // if the entire request was sent but the proxy has not begun receiving the response, we are currently "waiting"
        else if (sendFinishedNanos > 0L && responseReceiveStartedNanos == 0L) {
            harEntry.getTimings().setWait(timeoutTimestampNanos - sendFinishedNanos, TimeUnit.NANOSECONDS);
        }
        // if the proxy has already begun to receive the response, we are currenting "receiving"
        else if (responseReceiveStartedNanos > 0L) {
            harEntry.getTimings().setReceive(timeoutTimestampNanos - responseReceiveStartedNanos, TimeUnit.NANOSECONDS);
        }
    }

    @Override
    public void proxyToServerConnectionQueued() {
        this.connectionQueuedNanos = System.nanoTime();
    }


    @Override
    public InetSocketAddress proxyToServerResolutionStarted(String resolvingServerHostAndPort) {
        dnsResolutionStartedNanos = System.nanoTime();

        if (connectionQueuedNanos > 0L) {
            httpConnectTiming.setBlockedTimeNanos(dnsResolutionStartedNanos - connectionQueuedNanos);
        } else {
            httpConnectTiming.setBlockedTimeNanos(0L);
        }

        return null;
    }

    @Override
    public void proxyToServerResolutionSucceeded(String serverHostAndPort, InetSocketAddress resolvedRemoteAddress) {
        this.dnsResolutionFinishedNanos = System.nanoTime();

        if (dnsResolutionStartedNanos > 0L) {
            httpConnectTiming.setDnsTimeNanos(dnsResolutionFinishedNanos - dnsResolutionStartedNanos);
        } else {
            httpConnectTiming.setDnsTimeNanos(0L);
        }

        // the address *should* always be resolved at this point
        this.resolvedAddress = resolvedRemoteAddress.getAddress();
    }

    @Override
    public void proxyToServerConnectionStarted() {
        this.connectionStartedNanos = System.nanoTime();
    }

    @Override
    public void proxyToServerRequestSending() {
        this.sendStartedNanos = System.nanoTime();
    }

    @Override
    public void proxyToServerRequestSent() {
        this.sendFinishedNanos = System.nanoTime();
    }

    @Override
    public void serverToProxyResponseReceiving() {
        this.responseReceiveStartedNanos = System.nanoTime();
    }

    /**
     * Populates timing information in the specified harEntry for failed rquests. Populates as much timing information
     * as possible, up to the point of failure.
     *
     * @param harEntry HAR entry to populate timing information in
     */
    private void populateTimingsForFailedCONNECT(HarEntry harEntry) {
        HarTimings timings = harEntry.getTimings();

        if (connectionQueuedNanos > 0L && dnsResolutionStartedNanos > 0L) {
            timings.setBlocked(dnsResolutionStartedNanos - connectionQueuedNanos, TimeUnit.NANOSECONDS);
        }

        if (dnsResolutionStartedNanos > 0L && dnsResolutionFinishedNanos > 0L) {
            timings.setDns(dnsResolutionFinishedNanos - dnsResolutionStartedNanos, TimeUnit.NANOSECONDS);
        }

        if (connectionStartedNanos > 0L && connectionSucceededTimeNanos > 0L) {
            timings.setConnect(connectionSucceededTimeNanos - connectionStartedNanos, TimeUnit.NANOSECONDS);

            if (sslHandshakeStartedNanos > 0L) {
                timings.setSsl(connectionSucceededTimeNanos - this.sslHandshakeStartedNanos, TimeUnit.NANOSECONDS);
            }
        }

        if (sendStartedNanos > 0L && sendFinishedNanos >= 0L) {
            timings.setSend(sendFinishedNanos - sendStartedNanos, TimeUnit.NANOSECONDS);
        }

        if (sendFinishedNanos > 0L && responseReceiveStartedNanos >= 0L) {
            timings.setWait(responseReceiveStartedNanos - sendFinishedNanos, TimeUnit.NANOSECONDS);
        }

        // since this method is for HTTP CONNECT failures only, we can't populate a "received" time, since that would
        // require the CONNECT to be successful, in which case this method wouldn't be called.
    }

    /**
     * Creates a {@link HarEntry} for a failed CONNECT request. Initializes and populates the entry, including the
     * {@link HarRequest}, {@link HarResponse}, and {@link HarTimings}. (Note: only successful timing information is
     * populated in the timings object; the calling method must populate the timing information for the final, failed
     * step. For example, if DNS resolution failed, this method will populate the network 'blocked' time, but not the DNS
     * time.) Populates the specified errorMessage in the {@link HarResponse}'s error field.
     *
     * @param errorMessage error message to place in the har response
     * @return a new HAR entry
     */
    private HarEntry createHarEntryForFailedCONNECT(String errorMessage) {
        HarEntry harEntry = new HarEntry(currentPageRef);
        harEntry.setStartedDateTime(requestStartTime);

        HarRequest request = createRequestForFailedConnect(originalRequest);
        harEntry.setRequest(request);

        HarResponse response = HarCaptureUtil.createHarResponseForFailure();
        harEntry.setResponse(response);

        response.setError(errorMessage);

        populateTimingsForFailedCONNECT(harEntry);

        populateServerIpAddress(harEntry);


        return harEntry;
    }

    private void populateServerIpAddress(HarEntry harEntry) {
        // populate the server IP address if it was resolved as part of this request. otherwise, populate the IP address from the cache.
        if (resolvedAddress != null) {
            harEntry.setServerIPAddress(resolvedAddress.getHostAddress());
        } else {
            String serverHost = HttpUtil.getHostFromRequest(modifiedHttpRequest);
            if (serverHost != null && !serverHost.isEmpty()) {
                String resolvedAddress = ResolvedHostnameCacheFilter.getPreviouslyResolvedAddressForHost(serverHost);
                if (resolvedAddress != null) {
                    harEntry.setServerIPAddress(resolvedAddress);
                } else {
                    // the resolvedAddress may be null if the ResolvedHostnameCacheFilter has expired the entry (which is unlikely),
                    // or in the far more common case that the proxy is using a chained proxy to connect to connect to the
                    // remote host. since the chained proxy handles IP address resolution, the IP address in the HAR must be blank.
                    log.trace("Unable to find cached IP address for host: {}. IP address in HAR entry will be blank.", serverHost);
                }
            } else {
                log.warn("Unable to identify host from request uri: {}", modifiedHttpRequest.getUri());
            }
        }
    }

    /**
     * Creates a new {@link HarRequest} object for this failed HTTP CONNECT. Does not populate fields within the request,
     * such as the error message.
     *
     * @param httpConnectRequest the HTTP CONNECT request that failed
     * @return a new HAR request object
     */
    private HarRequest createRequestForFailedConnect(HttpRequest httpConnectRequest) {
        String url = getFullUrl(httpConnectRequest);

        return new HarRequest(httpConnectRequest.getMethod().toString(), url, httpConnectRequest.getProtocolVersion().text());
    }

    /**
     * Retrieves and removes (thus "consumes") the SSL timing information from the connection cache for the specified address.
     *
     * @param clientAddress the address of the client connection that established the HTTP tunnel
     * @return the timing information for the tunnel previously established from the clientAddress
     */
    public static HttpConnectTiming consumeConnectTimingForConnection(InetSocketAddress clientAddress) {
        return httpConnectTimes.remove(clientAddress);
    }

    @Override
    public void setModifiedHttpRequest(HttpRequest modifiedHttpRequest) {
        this.modifiedHttpRequest = modifiedHttpRequest;
    }
}
