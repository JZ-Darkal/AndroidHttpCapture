package net.lightbody.bmp.mitm.exception;

/**
 * Indicates a problem creating a certificate (server or CA).
 */
public class CertificateCreationException extends RuntimeException {
    private static final long serialVersionUID = 592999944486567944L;

    public CertificateCreationException() {
    }

    public CertificateCreationException(String message) {
        super(message);
    }

    public CertificateCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CertificateCreationException(Throwable cause) {
        super(cause);
    }
}
