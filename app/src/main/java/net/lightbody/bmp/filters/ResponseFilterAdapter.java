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
 * A filter adapter for {@link ResponseFilter} implementations. Executes the filter when the {@link HttpFilters#serverToProxyResponse(HttpObject)}
 * method is invoked.
 */
public class ResponseFilterAdapter extends HttpsAwareFiltersAdapter implements ModifiedRequestAwareFilter {
    private final ResponseFilter responseFilter;

    /**
     * The final HttpRequest sent to the server, reflecting all modifications from request filters.
     */
    private HttpRequest modifiedHttpRequest;

    public ResponseFilterAdapter(HttpRequest originalRequest, ChannelHandlerContext ctx, ResponseFilter responseFilter) {
        super(originalRequest, ctx);

        this.responseFilter = responseFilter;
    }

    @Override
    public HttpObject serverToProxyResponse(HttpObject httpObject) {
        // only filter when the original HttpResponse comes through. the ResponseFilterAdapter is not designed to filter
        // any subsequent HttpContents.
        if (httpObject instanceof HttpResponse) {
            HttpResponse httpResponse = (HttpResponse) httpObject;

            HttpMessageContents contents;
            if (httpObject instanceof FullHttpMessage) {
                FullHttpMessage httpContent = (FullHttpMessage) httpObject;
                contents = new HttpMessageContents(httpContent);
            } else {
                // the HTTP object is not a FullHttpMessage, which means that message contents will not be available on this response and cannot be modified.
                contents = null;
            }

            HttpMessageInfo messageInfo = new HttpMessageInfo(originalRequest, ctx, isHttps(), getFullUrl(modifiedHttpRequest), getOriginalUrl());

            responseFilter.filterResponse(httpResponse, contents, messageInfo);
        }

        return super.serverToProxyResponse(httpObject);
    }

    @Override
    public void setModifiedHttpRequest(HttpRequest modifiedHttpRequest) {
        this.modifiedHttpRequest = modifiedHttpRequest;
    }

    /**
     * A {@link HttpFiltersSourceAdapter} for {@link ResponseFilterAdapter}s. By default, this FilterSource enables HTTP message aggregation
     * and sets a maximum response buffer size of 2 MiB.
     */
    public static class FilterSource extends HttpFiltersSourceAdapter {
        private static final int DEFAULT_MAXIMUM_RESPONSE_BUFFER_SIZE = 2097152;

        private final ResponseFilter filter;
        private final int maximumResponseBufferSizeInBytes;

        /**
         * Creates a new filter source that will invoke the specified filter and uses the {@link #DEFAULT_MAXIMUM_RESPONSE_BUFFER_SIZE} as
         * the maximum buffer size.
         *
         * @param filter ResponseFilter to invoke
         */
        public FilterSource(ResponseFilter filter) {
            this.filter = filter;
            this.maximumResponseBufferSizeInBytes = DEFAULT_MAXIMUM_RESPONSE_BUFFER_SIZE;
        }

        /**
         * Creates a new filter source that will invoke the specified filter and uses the maximumResponseBufferSizeInBytes as the maximum
         * buffer size. Set maximumResponseBufferSizeInBytes to 0 to disable aggregation. <b>If message aggregation is disabled,
         * the {@link HttpMessageContents} will not be available for modification.</b> (<b>Note:</b> HTTP message aggregation will
         * be enabled if <i>any</i> filter has a maximum request or response buffer size greater than 0. See
         * {@link org.littleshoot.proxy.HttpFiltersSource#getMaximumResponseBufferSizeInBytes()} for details.)
         *
         * @param filter                           ResponseFilter to invoke
         * @param maximumResponseBufferSizeInBytes maximum buffer size when aggregating responses for filtering
         */
        public FilterSource(ResponseFilter filter, int maximumResponseBufferSizeInBytes) {
            this.filter = filter;
            this.maximumResponseBufferSizeInBytes = maximumResponseBufferSizeInBytes;
        }

        @Override
        public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
            return new ResponseFilterAdapter(originalRequest, ctx, filter);
        }

        @Override
        public int getMaximumResponseBufferSizeInBytes() {
            return maximumResponseBufferSizeInBytes;
        }
    }
}
