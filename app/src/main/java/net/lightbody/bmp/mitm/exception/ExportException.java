package net.lightbody.bmp.mitm.exception;

/**
 * Indicates an error occurred while exporting/serializing a certificate, private key, KeyStore, etc.
 */
public class ExportException extends RuntimeException {
    private static final long serialVersionUID = -3505301862887355206L;

    public ExportException() {
    }

    public ExportException(String message) {
        super(message);
    }

    public ExportException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExportException(Throwable cause) {
        super(cause);
    }
}
