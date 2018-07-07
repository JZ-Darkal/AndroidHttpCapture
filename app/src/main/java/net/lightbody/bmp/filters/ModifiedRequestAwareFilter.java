package net.lightbody.bmp.filters;

import io.netty.handler.codec.http.HttpRequest;

/**
 * Indicates that a filter wishes to capture the final HttpRequest that is sent to the server, reflecting all
 * modifications from request filters. {@link BrowserMobHttpFilterChain#clientToProxyRequest(io.netty.handler.codec.http.HttpObject)}
 * will invoke the {@link #setModifiedHttpRequest(HttpRequest)} method <b>after</b> all filters have processed the initial
 * {@link HttpRequest} object.
 */
public interface ModifiedRequestAwareFilter {
    /**
     * Notifies implementing classes of the modified HttpRequest that will be sent to the server, reflecting all
     * modifications from filters.
     *
     * @param modifiedHttpRequest the modified HttpRequest sent to the server
     */
    void setModifiedHttpRequest(HttpRequest modifiedHttpRequest);
}
