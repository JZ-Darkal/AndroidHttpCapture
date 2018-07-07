package net.lightbody.bmp.mitm.util;

/**
 * Default values for basic MITM properties.
 */
public class MitmConstants {
    /**
     * The default message digest to use when signing certificates (CA or server). On 64-bit systems this is set to
     * SHA512, on 32-bit systems this is SHA256. On 64-bit systems, SHA512 generally performs better than SHA256; see
     * this question for details: http://crypto.stackexchange.com/questions/26336/sha512-faster-than-sha256. SHA384 is
     * SHA512 with a smaller output size.
     */
    public static final String DEFAULT_MESSAGE_DIGEST = is32BitJvm() ? "SHA256": "SHA384";

    /**
     * The default {@link java.security.KeyStore} type to use when creating KeyStores (e.g. for impersonated server
     * certificates). PKCS12 is widely supported.
     */
    public static final String DEFAULT_KEYSTORE_TYPE = "PKCS12";

    /**
     * Uses the non-portable system property sun.arch.data.model to help determine if we are running on a 32-bit JVM.
     * Since the majority of modern systems are 64 bits, this method "assumes" 64 bits and only returns true if
     * sun.arch.data.model explicitly indicates a 32-bit JVM.
     *
     * @return true if we can determine definitively that this is a 32-bit JVM, otherwise false
     */
    private static boolean is32BitJvm() {
        Integer bits = Integer.getInteger("sun.arch.data.model");

        return bits != null && bits == 32;

    }
}
