package net.lightbody.bmp.mitm.exception;

/**
 * Indicates an error occurred while attempting to create a new {@link javax.net.ssl.SSLContext}.
 */
public class SslContextInitializationException extends RuntimeException {
    private static final long serialVersionUID = 6744059714710316821L;

    public SslContextInitializationException() {
    }

    public SslContextInitializationException(String message) {
        super(message);
    }

    public SslContextInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SslContextInitializationException(Throwable cause) {
        super(cause);
    }
}
