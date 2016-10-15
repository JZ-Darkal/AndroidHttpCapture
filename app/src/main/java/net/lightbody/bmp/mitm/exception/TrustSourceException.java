package net.lightbody.bmp.mitm.exception;

/**
 * Indicates that an error occurred while attempting to create or populate a {@link net.lightbody.bmp.mitm.TrustSource}.
 */
public class TrustSourceException extends RuntimeException {
    public TrustSourceException() {
    }

    public TrustSourceException(String message) {
        super(message);
    }

    public TrustSourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public TrustSourceException(Throwable cause) {
        super(cause);
    }
}
