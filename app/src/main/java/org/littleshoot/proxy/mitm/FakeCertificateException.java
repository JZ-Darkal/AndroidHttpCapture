package org.littleshoot.proxy.mitm;

public class FakeCertificateException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public FakeCertificateException(String message, Throwable cause) {
        super(message, cause);
    }

}
