package net.lightbody.bmp.filters;

import net.lightbody.bmp.proxy.BlacklistEntry;

import java.util.Collection;
import java.util.Collections;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * Applies blacklist entries to this request. The filter does not make a defensive copy of the blacklist entries, so there is no guarantee
 * that the blacklist at the time of construction will contain the same values when the filter is actually invoked, if the entries are modified concurrently.
 */
public class BlacklistFilter extends HttpsAwareFiltersAdapter {
    private final Collection<BlacklistEntry> blacklistedUrls;

    public BlacklistFilter(HttpRequest originalRequest, ChannelHandlerContext ctx, Collection<BlacklistEntry> blacklistedUrls) {
        super(originalRequest, ctx);

        if (blacklistedUrls != null) {
            this.blacklistedUrls = blacklistedUrls;
        } else {
            this.blacklistedUrls = Collections.emptyList();
        }
    }

    @Override
    public HttpResponse clientToProxyRequest(HttpObject httpObject) {
        if (httpObject instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) httpObject;

            String url = getFullUrl(httpRequest);

            for (BlacklistEntry entry : blacklistedUrls) {
                if (HttpMethod.CONNECT.equals(httpRequest.getMethod()) && entry.getHttpMethodPattern() == null) {
                    // do not allow CONNECTs to be blacklisted unless a method pattern is explicitly specified
                    continue;
                }

                if (entry.matches(url, httpRequest.getMethod().name())) {
                    HttpResponseStatus status = HttpResponseStatus.valueOf(entry.getStatusCode());
                    HttpResponse resp = new DefaultFullHttpResponse(httpRequest.getProtocolVersion(), status);
                    HttpHeaders.setContentLength(resp, 0L);

                    return resp;
                }
            }
        }

        return null;
    }
}
