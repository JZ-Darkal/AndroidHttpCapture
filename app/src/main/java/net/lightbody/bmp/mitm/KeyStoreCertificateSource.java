package net.lightbody.bmp.mitm;

import net.lightbody.bmp.mitm.exception.CertificateSourceException;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.X509Certificate;

/**
 * A {@link CertificateAndKeySource} that loads the root certificate and private key from a Java KeyStore. The
 * KeyStore must contain a certificate and a private key, specified by the privateKeyAlias value. The KeyStore must
 * already be loaded and initialized; to load the KeyStore from a file or classpath resource, use
 * {@link KeyStoreFileCertificateSource}, {@link PemFileCertificateSource}, or a custom
 * implementation of {@link CertificateAndKeySource}.
 */
public class KeyStoreCertificateSource implements CertificateAndKeySource {
    private final KeyStore keyStore;
    private final String keyStorePassword;
    private final String privateKeyAlias;

    public KeyStoreCertificateSource(KeyStore keyStore, String privateKeyAlias, String keyStorePassword) {
        if (keyStore == null) {
            throw new IllegalArgumentException("KeyStore cannot be null");
        }

        if (privateKeyAlias == null) {
            throw new IllegalArgumentException("Private key alias cannot be null");
        }

        if (keyStorePassword == null) {
            throw new IllegalArgumentException("KeyStore password cannot be null");
        }

        this.keyStore = keyStore;
        this.keyStorePassword = keyStorePassword;
        this.privateKeyAlias = privateKeyAlias;
    }

    @Override
    public CertificateAndKey load() {
        try {
            KeyStore.Entry entry;
            try {
                entry = keyStore.getEntry(privateKeyAlias, new KeyStore.PasswordProtection(keyStorePassword.toCharArray()));
            } catch (UnrecoverableEntryException e) {
                throw new CertificateSourceException("Unable to load private key with alias " + privateKeyAlias + " from KeyStore. Verify the KeyStore password is correct.", e);
            }

            if (entry == null) {
                throw new CertificateSourceException("Unable to find entry in keystore with alias: " + privateKeyAlias);
            }

            if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
                throw new CertificateSourceException("Entry in KeyStore with alias " + privateKeyAlias + " did not contain a private key entry");
            }

            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) entry;

            PrivateKey privateKey = privateKeyEntry.getPrivateKey();

            if (!(privateKeyEntry.getCertificate() instanceof X509Certificate)) {
                throw new CertificateSourceException("Certificate for private key in KeyStore was not an X509Certificate. Private key alias: " + privateKeyAlias
                        + ". Certificate type: " + (privateKeyEntry.getCertificate() != null ? privateKeyEntry.getCertificate().getClass().getName() : null));
            }

            X509Certificate x509Certificate = (X509Certificate) privateKeyEntry.getCertificate();

            return new CertificateAndKey(x509Certificate, privateKey);
        } catch (KeyStoreException | NoSuchAlgorithmException e) {
            throw new CertificateSourceException("Error accessing keyStore", e);
        }
    }

}
