package net.lightbody.bmp.exception;

/**
 * A checked exception wrapper for {@link java.nio.charset.UnsupportedCharsetException}. This exception is checked to prevent
 * situations where an unsupported character set in e.g. a Content-Type header causes the proxy to fail completely, rather
 * than fallback to some suitable default behavior, such as not parsing the text contents of a message.
 */
public class UnsupportedCharsetException extends Exception {
    public UnsupportedCharsetException(java.nio.charset.UnsupportedCharsetException e) {
        super(e);

        if (e == null) {
            throw new IllegalArgumentException("net.lightbody.bmp.exception.UnsupportedCharsetException must be initialized with a non-null instance of java.nio.charset.UnsupportedCharsetException");
        }
    }

    /**
     * @return the underlying {@link java.nio.charset.UnsupportedCharsetException} that this exception wraps.
     */
    public java.nio.charset.UnsupportedCharsetException getUnsupportedCharsetExceptionCause() {
        return (java.nio.charset.UnsupportedCharsetException) this.getCause();
    }
}
