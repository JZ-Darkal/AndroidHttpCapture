package net.lightbody.bmp.mitm.exception;

/**
 * Indicates a general problem occurred while attempting to man-in-the-middle communications between the client and the
 * upstream server.
 */
public class MitmException extends RuntimeException {
    private static final long serialVersionUID = -1960691906515767537L;

    public MitmException() {
    }

    public MitmException(String message) {
        super(message);
    }

    public MitmException(String message, Throwable cause) {
        super(message, cause);
    }

    public MitmException(Throwable cause) {
        super(cause);
    }
}
