package net.lightbody.bmp.util;

import com.google.common.io.CharStreams;
import net.lightbody.bmp.mitm.exception.UncheckedIOException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

/**
 * Utility class for dealing with classpath resources.
 */
public class ClasspathResourceUtil {
    /**
     * Retrieves a classpath resource using the {@link ClasspathResourceUtil} classloader and converts it to a String using the specified
     * character set. If any error occurs while reading the resource, this method throws
     * {@link net.lightbody.bmp.mitm.exception.UncheckedIOException}. If the classpath resource cannot be found, this
     * method throws a FileNotFoundException wrapped in an UncheckedIOException.
     *
     * @param resource classpath resource to load
     * @param charset charset to use to decode the classpath resource
     * @return a String
     * @throws UncheckedIOException if the classpath resource cannot be found or cannot be read for any reason
     */
    public static String classpathResourceToString(String resource, Charset charset) throws UncheckedIOException {
        if (resource == null) {
            throw new IllegalArgumentException("Classpath resource to load cannot be null");
        }

        if (charset == null) {
            throw new IllegalArgumentException("Character set cannot be null");
        }

        try {
            InputStream resourceAsStream = ClasspathResourceUtil.class.getResourceAsStream(resource);
            if (resourceAsStream == null) {
                throw new UncheckedIOException(new FileNotFoundException("Unable to locate classpath resource: " + resource));
            }

            // the classpath resource was found and opened. wrap it in a Reader and return its contents.
            Reader resourceReader = new InputStreamReader(resourceAsStream, charset);

            return CharStreams.toString(resourceReader);
        } catch (IOException e) {
            throw new UncheckedIOException("Error occurred while reading classpath resource", e);
        }
    }
}
