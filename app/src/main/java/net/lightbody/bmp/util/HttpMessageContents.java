package net.lightbody.bmp.util;

import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpHeaders;
import net.lightbody.bmp.exception.UnsupportedCharsetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * Helper class to wrap the contents of an {@link io.netty.handler.codec.http.HttpMessage}. Contains convenience methods to extract and
 * manipulate the contents of the wrapped {@link io.netty.handler.codec.http.HttpMessage}.
 *
 * TODO: Currently this class only wraps FullHttpMessages, since it must modify the Content-Length header; determine if this may be applied to chunked messages as well
 */
public class HttpMessageContents {
    private static final Logger log = LoggerFactory.getLogger(HttpMessageContents.class);

    private final FullHttpMessage httpMessage;

    // caches for contents, to avoid repeated re-extraction of data
    private volatile String textContents;
    private volatile byte[] binaryContents;

    public HttpMessageContents(FullHttpMessage httpMessage) {
        this.httpMessage = httpMessage;
    }

    /**
     * Replaces the contents of the wrapped HttpMessage with the specified text contents, encoding them in the character set specified by the
     * message's Content-Type header. Note that this method does not update the Content-Type header, so if the content type will change as a
     * result of this call, the Content-Type header should be updated before calling this method.
     *
     * @param newContents new message contents
     */
    public void setTextContents(String newContents) {
        HttpObjectUtil.replaceTextHttpEntityBody(httpMessage, newContents);

        // replaced the contents, so clear the local cache
        textContents = null;
        binaryContents = null;
    }

    /**
     * Replaces the contents of the wrapped HttpMessage with the specified binary contents. Note that this method does not update the
     * Content-Type header, so if the content type will change as a result of this call, the Content-Type header should be updated before
     * calling this method.
     *
     * @param newBinaryContents new message contents
     */
    public void setBinaryContents(byte[] newBinaryContents) {
        HttpObjectUtil.replaceBinaryHttpEntityBody(httpMessage, newBinaryContents);

        // replaced the contents, so clear the local cache
        binaryContents = null;
        textContents = null;
    }

    /**
     * Retrieves the contents of this message as a String, decoded according to the message's Content-Type header. This method caches
     * the contents, so repeated calls to this method should not incur a penalty; however, modifications to the message contents
     * outside of this class will result in stale data returned from this method.
     *
     * @return String representation of the entity body
     * @throws java.nio.charset.UnsupportedCharsetException if the character set declared in the message is not supported on this platform
     */
    public String getTextContents() throws java.nio.charset.UnsupportedCharsetException {
        // avoid re-extracting the contents if this method is called repeatedly
        if (textContents == null) {
            textContents = HttpObjectUtil.extractHttpEntityBody(httpMessage);
        }

        return textContents;
    }

    /**
     * Retrieves the binary contents of this message. This method caches the contents, so repeated calls to this method should not incur a
     * penalty; however, modifications to the message contents outside of this class will result in stale data returned from this method.
     *
     * @return binary contents of the entity body
     */
    public byte[] getBinaryContents() {
        // avoid re-extracting the contents if this method is called repeatedly
        if (binaryContents == null) {
            binaryContents = HttpObjectUtil.extractBinaryHttpEntityBody(httpMessage);
        }

        return binaryContents;
    }

    /**
     * Retrieves the Content-Type header of this message. If no Content-Type is present, returns the assumed default Content-Type (see
     * {@link BrowserMobHttpUtil#UNKNOWN_CONTENT_TYPE}).
     *
     * @return the message's content type
     */
    public String getContentType() {
        String contentTypeHeader = HttpHeaders.getHeader(httpMessage, HttpHeaders.Names.CONTENT_TYPE);
        if (contentTypeHeader == null || contentTypeHeader.isEmpty()) {
            return BrowserMobHttpUtil.UNKNOWN_CONTENT_TYPE;
        } else {
            return contentTypeHeader;
        }
    }

    /**
     * Retrieves the character set of the entity body. If the Content-Type is not a textual type, this value is meaningless.
     * If no character set is specified, this method will return the default ISO-8859-1 character set. If the Content-Type
     * specifies a character set, but the character set is not supported on this platform, this method throws an
     * {@link java.nio.charset.UnsupportedCharsetException}.
     *
     * @return the entity body's character set
     * @throws java.nio.charset.UnsupportedCharsetException if the character set declared in the message is not supported on this platform
     */
    public Charset getCharset() throws java.nio.charset.UnsupportedCharsetException {
        String contentTypeHeader = getContentType();

        Charset charset = null;
        try {
            charset = BrowserMobHttpUtil.readCharsetInContentTypeHeader(contentTypeHeader);
        } catch (UnsupportedCharsetException e) {
            java.nio.charset.UnsupportedCharsetException cause = e.getUnsupportedCharsetExceptionCause();
            log.error("Character set specified in Content-Type header is not supported on this platform. Content-Type header: {}", contentTypeHeader, cause);

            throw cause;
        }

        if (charset == null) {
            return BrowserMobHttpUtil.DEFAULT_HTTP_CHARSET;
        }

        return charset;
    }

    /**
     * Returns true if this message's Content-Type header indicates that it contains a textual data type. See {@link BrowserMobHttpUtil#hasTextualContent(String)}.
     *
     * @return true if the Content-Type header is a textual type, otherwise false
     */
    public boolean isText() {
        return BrowserMobHttpUtil.hasTextualContent(getContentType());
    }
}
