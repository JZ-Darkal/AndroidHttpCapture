package net.lightbody.bmp.mitm.exception;

import java.io.IOException;

/**
 * A convenience exception that wraps checked {@link IOException}s. (The built-in java.io.UncheckedIOException is only
 * available on Java 8.)
 */
public class UncheckedIOException extends RuntimeException {
    public UncheckedIOException(String message, IOException cause) {
        super(message, cause);
    }

    public UncheckedIOException(IOException cause) {
        super(cause);
    }
}
