package net.lightbody.bmp.mitm.keys;

import net.lightbody.bmp.mitm.exception.KeyGeneratorException;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.ECGenParameterSpec;

/**
 * A {@link KeyGenerator} that creates Elliptic Curve key pairs.
 */
public class ECKeyGenerator implements KeyGenerator {
    private static final String EC_KEY_GEN_ALGORITHM = "EC";

    private static final String DEFAULT_NAMED_CURVE = "secp256r1";

    private final String namedCurve;

    /**
     * Create a {@link KeyGenerator} that will create EC key pairs using the secp256r1 named curve (NIST P-256)
     * supported by modern web browsers.
     */
    public ECKeyGenerator() {
        this.namedCurve = DEFAULT_NAMED_CURVE;
    }

    /**
     * Create a {@link KeyGenerator} that will create EC key pairs using the specified named curve.
     */
    public ECKeyGenerator(String namedCurve) {
        this.namedCurve = namedCurve;
    }

    @Override
    public KeyPair generate() {
        // obtain an EC key pair generator for the specified named curve
        KeyPairGenerator generator;
        try {
            generator = java.security.KeyPairGenerator.getInstance(EC_KEY_GEN_ALGORITHM);
            ECGenParameterSpec ecName = new ECGenParameterSpec(namedCurve);
            generator.initialize(ecName);
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            throw new KeyGeneratorException("Unable to generate EC public/private key pair using named curve: " + namedCurve, e);
        }

        return generator.generateKeyPair();
    }

    @Override
    public String toString() {
        return "EC (" + namedCurve + ")";
    }
}
