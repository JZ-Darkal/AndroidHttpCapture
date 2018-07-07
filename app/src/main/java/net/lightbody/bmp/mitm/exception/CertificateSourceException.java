package net.lightbody.bmp.mitm.exception;

/**
 * Indicates that a {@link net.lightbody.bmp.mitm.CertificateAndKeySource} encountered an error while loading a
 * certificate and/or private key from a KeyStore, PEM file, or other source.
 */
public class CertificateSourceException extends RuntimeException {
    private static final long serialVersionUID = 6195838041376082083L;

    public CertificateSourceException() {
    }

    public CertificateSourceException(String message) {
        super(message);
    }

    public CertificateSourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public CertificateSourceException(Throwable cause) {
        super(cause);
    }
}
