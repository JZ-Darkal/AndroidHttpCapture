package net.lightbody.bmp.filters;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.littleshoot.proxy.impl.ProxyUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

/**
 * Checks this request against the whitelist, and returns the modified response if the request is not in the whitelist. The filter does not
 * make a defensive copy of the whitelist URLs, so there is no guarantee that the whitelist URLs at the time of construction will contain the
 * same values when the filter is actually invoked, if the URL collection is modified concurrently.
 */
public class WhitelistFilter extends HttpsAwareFiltersAdapter {
    private final boolean whitelistEnabled;
    private final int whitelistResponseCode;
    private final Collection<Pattern> whitelistUrls;

    public WhitelistFilter(HttpRequest originalRequest, ChannelHandlerContext ctx, boolean whitelistEnabled,int whitelistResponseCode,
                           Collection<Pattern> whitelistUrls) {
        super(originalRequest, ctx);

        this.whitelistEnabled = whitelistEnabled;
        this.whitelistResponseCode = whitelistResponseCode;
        if (whitelistUrls != null) {
            this.whitelistUrls = whitelistUrls;
        } else {
            this.whitelistUrls = Collections.emptyList();
        }
    }

    @Override
    public HttpResponse clientToProxyRequest(HttpObject httpObject) {
        if (!whitelistEnabled) {
            return null;
        }

        if (httpObject instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) httpObject;

            // do not allow HTTP CONNECTs to be short-circuited
            if (ProxyUtils.isCONNECT(httpRequest)) {
                return null;
            }

            boolean urlWhitelisted = false;

            String url = getFullUrl(httpRequest);

            for (Pattern pattern : whitelistUrls) {
                if (pattern.matcher(url).matches()) {
                    urlWhitelisted = true;
                    break;
                }
            }

            if (!urlWhitelisted) {
                HttpResponseStatus status = HttpResponseStatus.valueOf(whitelistResponseCode);
                HttpResponse resp = new DefaultFullHttpResponse(httpRequest.getProtocolVersion(), status);
                HttpHeaders.setContentLength(resp, 0L);

                return resp;
            }
        }

        return null;
    }
}
