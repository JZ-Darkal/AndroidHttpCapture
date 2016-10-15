package net.lightbody.bmp.mitm.exception;

/**
 * Indicates an exception occurred while generating a key pair.
 */
public class KeyGeneratorException extends RuntimeException {
    private static final long serialVersionUID = 7607159769324427808L;

    public KeyGeneratorException() {
    }

    public KeyGeneratorException(String message) {
        super(message);
    }

    public KeyGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyGeneratorException(Throwable cause) {
        super(cause);
    }
}
