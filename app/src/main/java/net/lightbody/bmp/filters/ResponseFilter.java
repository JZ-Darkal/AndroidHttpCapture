package net.lightbody.bmp.filters;

import net.lightbody.bmp.util.HttpMessageContents;
import net.lightbody.bmp.util.HttpMessageInfo;

import io.netty.handler.codec.http.HttpResponse;

/**
 * A functional interface to simplify modification and manipulation of responses.
 */
public interface ResponseFilter {
    /**
     * Implement this method to filter an HTTP response. The URI, headers, status line, etc. are available in the {@code response} parameter,
     * while the contents of the message are available in the {@code contents} parameter. The response can be modified directly, while the
     * contents may be modified using the {@link HttpMessageContents#setTextContents(String)} or {@link HttpMessageContents#setBinaryContents(byte[])}
     * methods.
     *
     * @param response    The response object, including URI, headers, status line, etc. Modifications to the response object will be reflected in the client response.
     * @param contents    The response contents.
     * @param messageInfo Additional information relating to the HTTP message.
     */
    void filterResponse(HttpResponse response, HttpMessageContents contents, HttpMessageInfo messageInfo);
}
