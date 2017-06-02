package net.lightbody.bmp.filters;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import net.lightbody.bmp.util.HttpMessageContents;
import net.lightbody.bmp.util.HttpMessageInfo;

/**
 * A functional interface to simplify modification and manipulation of requests.
 */
public interface RequestFilter {
    /**
     * Implement this method to filter an HTTP request. The HTTP method, URI, headers, etc. are available in the {@code request} parameter,
     * while the contents of the message are available in the {@code contents} parameter. The request can be modified directly, while the
     * contents may be modified using the {@link HttpMessageContents#setTextContents(String)} or {@link HttpMessageContents#setBinaryContents(byte[])}
     * methods. The request can be "short-circuited" by returning a non-null value.
     *
     * @param request The request object, including method, URI, headers, etc. Modifications to the request object will be reflected in the request sent to the server.
     * @param contents The request contents.
     * @param messageInfo Additional information relating to the HTTP message.
     * @return if the return value is non-null, the proxy will suppress the request and send the specified response to the client immediately
     */
    HttpResponse filterRequest(HttpRequest request, HttpMessageContents contents, HttpMessageInfo messageInfo);
}
