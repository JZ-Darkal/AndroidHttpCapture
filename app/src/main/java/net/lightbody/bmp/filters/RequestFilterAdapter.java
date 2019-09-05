package net.lightbody.bmp.filters;

import net.lightbody.bmp.util.HttpMessageContents;
import net.lightbody.bmp.util.HttpMessageInfo;

import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * A filter adapter for {@link RequestFilter} implementations. Executes the filter when the {@link HttpFilters#clientToProxyRequest(HttpObject)}
 * method is invoked.
 */
public class RequestFilterAdapter extends HttpsAwareFiltersAdapter {
    private final RequestFilter requestFilter;

    public RequestFilterAdapter(HttpRequest originalRequest, ChannelHandlerContext ctx, RequestFilter requestFilter) {
        super(originalRequest, ctx);

        this.requestFilter = requestFilter;
    }

    @Override
    public HttpResponse clientToProxyRequest(HttpObject httpObject) {
        // only filter when the original HttpRequest comes through. the RequestFilterAdapter is not designed to filter
        // any subsequent HttpContents.
        if (httpObject instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) httpObject;

            HttpMessageContents contents;
            if (httpObject instanceof FullHttpMessage) {
                FullHttpMessage httpContent = (FullHttpMessage) httpObject;
                contents = new HttpMessageContents(httpContent);
            } else {
                // the HTTP object is not a FullHttpMessage, which means that message contents are not available on this request and cannot be modified.
                contents = null;
            }

            HttpMessageInfo messageInfo = new HttpMessageInfo(originalRequest, ctx, isHttps(), getFullUrl(httpRequest), getOriginalUrl());

            HttpResponse response = requestFilter.filterRequest(httpRequest, contents, messageInfo);
            if (response != null) {
                return response;
            }
        }

        return null;
    }

    /**
     * A {@link HttpFiltersSourceAdapter} for {@link RequestFilterAdapter}s. By default, this FilterSource enables HTTP message aggregation
     * and sets a maximum request buffer size of 2 MiB.
     */
    public static class FilterSource extends HttpFiltersSourceAdapter {
        private static final int DEFAULT_MAXIMUM_REQUEST_BUFFER_SIZE = 2097152;

        private final RequestFilter filter;
        private final int maximumRequestBufferSizeInBytes;

        /**
         * Creates a new filter source that will invoke the specified filter and uses the {@link #DEFAULT_MAXIMUM_REQUEST_BUFFER_SIZE} as
         * the maximum buffer size.
         *
         * @param filter RequestFilter to invoke
         */
        public FilterSource(RequestFilter filter) {
            this.filter = filter;
            this.maximumRequestBufferSizeInBytes = DEFAULT_MAXIMUM_REQUEST_BUFFER_SIZE;
        }

        /**
         * Creates a new filter source that will invoke the specified filter and uses the maximumRequestBufferSizeInBytes as the maximum
         * buffer size. Set maximumRequestBufferSizeInBytes to 0 to disable aggregation. <b>If message aggregation is disabled,
         * the {@link HttpMessageContents} will not be available for modification.</b> (<b>Note:</b> HTTP message aggregation will
         * be enabled if <i>any</i> filter has a maximum request or response buffer size greater than 0. See
         * {@link org.littleshoot.proxy.HttpFiltersSource#getMaximumRequestBufferSizeInBytes()} for details.)
         *
         * @param filter                          RequestFilter to invoke
         * @param maximumRequestBufferSizeInBytes maximum buffer size when aggregating Requests for filtering
         */
        public FilterSource(RequestFilter filter, int maximumRequestBufferSizeInBytes) {
            this.filter = filter;
            this.maximumRequestBufferSizeInBytes = maximumRequestBufferSizeInBytes;
        }

        @Override
        public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
            return new RequestFilterAdapter(originalRequest, ctx, filter);
        }

        @Override
        public int getMaximumRequestBufferSizeInBytes() {
            return maximumRequestBufferSizeInBytes;
        }
    }
}
