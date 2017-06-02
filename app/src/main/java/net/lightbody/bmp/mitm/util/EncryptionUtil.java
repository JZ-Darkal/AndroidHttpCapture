package net.lightbody.bmp.mitm.util;

import net.lightbody.bmp.mitm.exception.ExportException;
import net.lightbody.bmp.mitm.exception.ImportException;

import org.apache.commons.io.FileUtils;

import javax.crypto.Cipher;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.DSAKey;
import java.security.interfaces.ECKey;
import java.security.interfaces.RSAKey;
import java.util.Random;

/**
 * A collection of simple JCA-related utilities.
 */
public class EncryptionUtil {
    /**
     * Creates a signature algorithm string using the specified message digest and the encryption type corresponding
     * to the supplied signingKey. Useful when generating the signature algorithm to be used to sign server certificates
     * using the CA root certificate's signingKey.
     * <p/>
     * For example, if the root certificate has an RSA private key, and you
     * wish to use the SHA256 message digest, this method will return the string "SHA256withRSA". See the
     * "Signature Algorithms" section of http://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html
     * for a list of JSSE-supported signature algorithms.
     *
     * @param messageDigest digest to use to sign the certificate, such as SHA512
     * @param signingKey    private key that will be used to sign the certificate
     * @return a JCA-compatible signature algorithm
     */
    public static String getSignatureAlgorithm(String messageDigest, Key signingKey) {
        return messageDigest + "with" + getDigitalSignatureType(signingKey);
    }

    /**
     * Returns the type of digital signature used with the specified signing key.
     *
     * @param signingKey private key that will be used to sign a certificate (or something else)
     * @return a string representing the digital signature type (ECDSA, RSA, etc.)
     */
    public static String getDigitalSignatureType(Key signingKey) {
        if (signingKey instanceof ECKey) {
            return "ECDSA";
        } else if (signingKey instanceof RSAKey) {
            return "RSA";
        } else if (signingKey instanceof DSAKey) {
            return "DSA";
        } else {
            throw new IllegalArgumentException("Cannot determine digital signature encryption type for unknown key type: " + signingKey.getClass().getCanonicalName());
        }

    }

    /**
     * Creates a random BigInteger greater than 0 with the specified number of bits.
     *
     * @param bits number of bits to generate
     * @return random BigInteger
     */
    public static BigInteger getRandomBigInteger(int bits) {
        return new BigInteger(bits, new Random());
    }

    /**
     * Returns true if the key is an RSA public or private key.
     */
    public static boolean isRsaKey(Key key) {
        return "RSA".equals(key.getAlgorithm());
    }

    /**
     * Returns true if the key is an elliptic curve public or private key.
     */
    public static boolean isEcKey(Key key) {
        return "EC".equals(key.getAlgorithm());
    }

    /**
     * Convenience method to write PEM data to a file. The file will be encoded in the US_ASCII character set.
     *
     * @param file file to write to
     * @param pemDataToWrite PEM data to write to the file
     */
    public static void writePemStringToFile(File file, String pemDataToWrite) {
        try {
            FileUtils.write(file, pemDataToWrite);
        } catch (IOException e) {
            throw new ExportException("Unable to write PEM string to file: " + file.getName(), e);
        }
    }

    /**
     * Convenience method to read PEM data from a file. The file encoding must be US_ASCII.
     *
     * @param file file to read from
     * @return PEM data from file
     */
    public static String readPemStringFromFile(File file) {
        try {
            byte[] fileContents = FileUtils.readFileToByteArray(file);
            return new String(fileContents, Charset.forName("US-ASCII"));
        } catch (IOException e) {
            throw new ImportException("Unable to read PEM-encoded data from file: " + file.getName());
        }
    }

    /**
     * Determines if unlimited-strength cryptography is allowed, i.e. if this JRE has then the unlimited strength policy
     * files installed.
     *
     * @return true if unlimited strength cryptography is allowed, otherwise false
     */
    public static boolean isUnlimitedStrengthAllowed() {
        try {
            return Cipher.getMaxAllowedKeyLength("AES") >= 256;
        } catch (NoSuchAlgorithmException e) {
            return false;
        }

    }
}
