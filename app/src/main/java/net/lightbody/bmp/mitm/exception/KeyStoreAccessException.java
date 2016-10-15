package net.lightbody.bmp.mitm.exception;

/**
 * Indicates an error occurred while accessing a java KeyStore.
 */
public class KeyStoreAccessException extends RuntimeException {
    private static final long serialVersionUID = -5560417886988154298L;

    public KeyStoreAccessException() {
    }

    public KeyStoreAccessException(String message) {
        super(message);
    }

    public KeyStoreAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyStoreAccessException(Throwable cause) {
        super(cause);
    }
}
