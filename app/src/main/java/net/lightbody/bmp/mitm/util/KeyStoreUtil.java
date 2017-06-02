package net.lightbody.bmp.mitm.util;

import net.lightbody.bmp.mitm.exception.KeyStoreAccessException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Utility for loading, saving, and manipulating {@link KeyStore}s.
 */
public class KeyStoreUtil {
    /**
     * Creates and initializes an empty KeyStore using the specified keyStoreType.
     *
     * @param keyStoreType type of key store to initialize, or null to use the system default
     * @param provider     JCA provider to use, or null to use the system default
     * @return a new KeyStore
     */
    public static KeyStore createEmptyKeyStore(String keyStoreType, String provider) {
        if (keyStoreType == null) {
            keyStoreType = KeyStore.getDefaultType();
        }

        KeyStore keyStore;
        try {
            if (provider == null) {
                keyStore = KeyStore.getInstance(keyStoreType);
            } else {
                keyStore = KeyStore.getInstance(keyStoreType, provider);
            }
            keyStore.load(null, null);
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | NoSuchProviderException | IOException e) {
            throw new KeyStoreAccessException("Error creating or initializing new KeyStore of type: " + keyStoreType, e);
        }
        return keyStore;
    }

    /**
     * Creates a new KeyStore containing the specified root certificate and private key.
     *
     * @param keyStoreType       type of the generated KeyStore, such as PKCS12 or JKS
     * @param certificate        root certificate to add to the KeyStore
     * @param privateKeyAlias    alias for the private key in the KeyStore
     * @param privateKey         private key to add to the KeyStore
     * @param privateKeyPassword password for the private key
     * @param provider           JCA provider to use, or null to use the system default
     * @return new KeyStore containing the root certificate and private key
     */
    public static KeyStore createRootCertificateKeyStore(String keyStoreType, X509Certificate certificate, String privateKeyAlias, PrivateKey privateKey, String privateKeyPassword, String provider) {
        if (privateKeyPassword == null) {
            throw new IllegalArgumentException("Must specify a KeyStore password");
        }

        KeyStore newKeyStore = KeyStoreUtil.createEmptyKeyStore(keyStoreType, provider);

        try {
            newKeyStore.setKeyEntry(privateKeyAlias, privateKey, privateKeyPassword.toCharArray(), new Certificate[]{certificate});
        } catch (KeyStoreException e) {
            throw new KeyStoreAccessException("Unable to store certificate and private key in KeyStore", e);
        }
        return newKeyStore;
    }

    /**
     * Retrieve the KeyManagers for the specified KeyStore.
     *
     * @param keyStore            the KeyStore to retrieve KeyManagers from
     * @param keyStorePassword    the KeyStore password
     * @param keyManagerAlgorithm key manager algorithm to use, or null to use the system default
     * @param provider            JCA provider to use, or null to use the system default
     * @return KeyManagers for the specified KeyStore
     */
    public static KeyManager[] getKeyManagers(KeyStore keyStore, String keyStorePassword, String keyManagerAlgorithm, String provider) {
        if (keyManagerAlgorithm == null) {
            keyManagerAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
        }

        try {
            KeyManagerFactory kmf;
            if (provider == null) {
                kmf = KeyManagerFactory.getInstance(keyManagerAlgorithm);
            } else {
                kmf = KeyManagerFactory.getInstance(keyManagerAlgorithm, provider);
            }

            kmf.init(keyStore, keyStorePassword.toCharArray());

            return kmf.getKeyManagers();
        } catch (NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException | NoSuchProviderException e) {
            throw new KeyStoreAccessException("Unable to get KeyManagers for KeyStore", e);
        }
    }
}
