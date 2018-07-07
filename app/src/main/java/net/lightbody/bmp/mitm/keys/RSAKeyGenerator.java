package net.lightbody.bmp.mitm.keys;

import net.lightbody.bmp.mitm.exception.KeyGeneratorException;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

/**
 * A {@link KeyGenerator} that creates RSA key pairs.
 */
public class RSAKeyGenerator implements KeyGenerator {
    private static final String RSA_KEY_GEN_ALGORITHM = "RSA";

    /**
     * Use a default RSA key size of 2048, since Chrome, Firefox, and possibly other browsers have begun to distrust
     * certificates signed with 1024-bit RSA keys.
     */
    private static final int DEFAULT_KEY_SIZE = 2048;

    private final int keySize;

    /**
     * Create a {@link KeyGenerator} that will create a 2048-bit RSA key pair.
     */
    public RSAKeyGenerator() {
        this.keySize = DEFAULT_KEY_SIZE;
    }

    /**
     * Create a {@link KeyGenerator} that will create an RSA key pair of the specified keySize.
     */
    public RSAKeyGenerator(int keySize) {
        this.keySize = keySize;
    }

    @Override
    public KeyPair generate() {
        // obtain an RSA key pair generator for the specified key size
        KeyPairGenerator generator;
        try {
            generator = KeyPairGenerator.getInstance(RSA_KEY_GEN_ALGORITHM);
            generator.initialize(keySize);
        } catch (NoSuchAlgorithmException e) {
            throw new KeyGeneratorException("Unable to generate " + keySize + "-bit RSA public/private key pair", e);
        }

        return generator.generateKeyPair();
    }

    @Override
    public String toString() {
        return "RSA (" + keySize + ")";
    }
}

