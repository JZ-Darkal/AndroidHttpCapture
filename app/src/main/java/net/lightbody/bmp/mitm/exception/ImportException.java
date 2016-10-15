package net.lightbody.bmp.mitm.exception;

/**
 * Indicates that an error occurred while importing a certificate, private key, or KeyStore.
 */
public class ImportException extends RuntimeException {
    private static final long serialVersionUID = 584414535648926010L;

    public ImportException() {
    }

    public ImportException(String message) {
        super(message);
    }

    public ImportException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImportException(Throwable cause) {
        super(cause);
    }
}
