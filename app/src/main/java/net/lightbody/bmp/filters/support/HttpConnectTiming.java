package net.lightbody.bmp.filters.support;

/**
 * Holds the connection-related timing information from an HTTP CONNECT request, so it can be added to the HAR timings for the first
 * "real" request to the same host. The HTTP CONNECT and the "real" HTTP requests are processed in different HarCaptureFilter instances.
 * <p/>
 * <b>Note:</b> The connect time must include the ssl time. According to the HAR spec at <a href="https://dvcs.w3.org/hg/webperf/raw-file/tip/specs/HAR/Overview.htm">https://dvcs.w3.org/hg/webperf/raw-file/tip/specs/HAR/Overview.htm</a>:
 <pre>
 ssl [number, optional] (new in 1.2) - Time required for SSL/TLS negotiation. If this field is defined then the time is also
 included in the connect field (to ensure backward compatibility with HAR 1.1). Use -1 if the timing does not apply to the
 current request.
 </pre>
 */
public class HttpConnectTiming {
    private volatile long blockedTimeNanos = -1;
    private volatile long dnsTimeNanos = -1;
    private volatile long connectTimeNanos = -1;
    private volatile long sslHandshakeTimeNanos = -1;

    public void setConnectTimeNanos(long connectTimeNanos) {
        this.connectTimeNanos = connectTimeNanos;
    }

    public void setSslHandshakeTimeNanos(long sslHandshakeTimeNanos) {
        this.sslHandshakeTimeNanos = sslHandshakeTimeNanos;
    }

    public void setBlockedTimeNanos(long blockedTimeNanos) {
        this.blockedTimeNanos = blockedTimeNanos;
    }

    public void setDnsTimeNanos(long dnsTimeNanos) {
        this.dnsTimeNanos = dnsTimeNanos;
    }

    public long getConnectTimeNanos() {
        return connectTimeNanos;
    }

    public long getSslHandshakeTimeNanos() {
        return sslHandshakeTimeNanos;
    }

    public long getBlockedTimeNanos() {
        return blockedTimeNanos;
    }

    public long getDnsTimeNanos() {
        return dnsTimeNanos;
    }
}
