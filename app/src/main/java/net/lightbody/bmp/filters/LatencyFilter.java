package net.lightbody.bmp.filters;

import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Adds latency to a response before sending it to the client. This filter always adds the specified latency, even if the latency
 * between the proxy and the remote server already exceeds this value.
 */
public class LatencyFilter extends HttpFiltersAdapter {
    private static final Logger log = LoggerFactory.getLogger(HttpFiltersAdapter.class);

    private final int latencyMs;

    public LatencyFilter(HttpRequest originalRequest, int latencyMs) {
        super(originalRequest);

        this.latencyMs = latencyMs;
    }

    @Override
    public HttpObject proxyToClientResponse(HttpObject httpObject) {
        if (httpObject instanceof HttpResponse) {
            if (latencyMs > 0) {
                try {
                    TimeUnit.MILLISECONDS.sleep(latencyMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();

                    log.warn("Interrupted while adding latency to response", e);
                }
            }
        }

        return super.proxyToClientResponse(httpObject);
    }
}
