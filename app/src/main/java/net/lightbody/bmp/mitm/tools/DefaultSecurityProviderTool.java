package net.lightbody.bmp.mitm.tools;

import com.google.common.io.CharStreams;

import net.lightbody.bmp.mitm.CertificateAndKey;
import net.lightbody.bmp.mitm.CertificateInfo;
import net.lightbody.bmp.mitm.exception.ImportException;
import net.lightbody.bmp.mitm.exception.KeyStoreAccessException;
import net.lightbody.bmp.mitm.util.KeyStoreUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;

/**
 * A {@link SecurityProviderTool} implementation that uses the default system Security provider where possible, but uses the
 * Bouncy Castle provider for operations that the JCA does not provide or implement (e.g. certificate generation and signing).
 */
public class DefaultSecurityProviderTool implements SecurityProviderTool {
    private final SecurityProviderTool bouncyCastle = new BouncyCastleSecurityProviderTool();

    @Override
    public CertificateAndKey createCARootCertificate(CertificateInfo certificateInfo, KeyPair keyPair, String messageDigest) {
        return bouncyCastle.createCARootCertificate(certificateInfo, keyPair, messageDigest);
    }

    @Override
    public CertificateAndKey createServerCertificate(CertificateInfo certificateInfo, X509Certificate caRootCertificate, PrivateKey caPrivateKey, KeyPair serverKeyPair, String messageDigest) {
        return bouncyCastle.createServerCertificate(certificateInfo, caRootCertificate, caPrivateKey, serverKeyPair, messageDigest);
    }

    @Override
    public KeyStore createServerKeyStore(String keyStoreType,
                                         CertificateAndKey serverCertificateAndKey,
                                         X509Certificate rootCertificate,
                                         String privateKeyAlias,
                                         String password) {
        if (password == null) {
            throw new IllegalArgumentException("KeyStore password cannot be null");
        }

        if (privateKeyAlias == null) {
            throw new IllegalArgumentException("Private key alias cannot be null");
        }

        // create a KeyStore containing the impersonated certificate's private key and a certificate chain with the
        // impersonated cert and our root certificate
        KeyStore impersonatedCertificateKeyStore = KeyStoreUtil.createEmptyKeyStore(keyStoreType, null);

        // create the certificate chain back for the impersonated certificate back to the root certificate
        Certificate[] chain = {serverCertificateAndKey.getCertificate(), rootCertificate};

        try {
            // place the impersonated certificate and its private key in the KeyStore
            impersonatedCertificateKeyStore.setKeyEntry(privateKeyAlias, serverCertificateAndKey.getPrivateKey(), password.toCharArray(), chain);
        } catch (KeyStoreException e) {
            throw new KeyStoreAccessException("Error storing impersonated certificate and private key in KeyStore", e);
        }

        return impersonatedCertificateKeyStore;
    }

    @Override
    public KeyStore createRootCertificateKeyStore(String keyStoreType, CertificateAndKey rootCertificateAndKey, String privateKeyAlias, String password) {
        return KeyStoreUtil.createRootCertificateKeyStore(keyStoreType, rootCertificateAndKey.getCertificate(), privateKeyAlias, rootCertificateAndKey.getPrivateKey(), password, null);
    }

    @Override
    public String encodePrivateKeyAsPem(PrivateKey privateKey, String passwordForPrivateKey, String encryptionAlgorithm) {
        return bouncyCastle.encodePrivateKeyAsPem(privateKey, passwordForPrivateKey, encryptionAlgorithm);
    }

    @Override
    public String encodeCertificateAsPem(Certificate certificate) {
        return bouncyCastle.encodeCertificateAsPem(certificate);
    }

    @Override
    public PrivateKey decodePemEncodedPrivateKey(Reader privateKeyReader, String password) {
        return bouncyCastle.decodePemEncodedPrivateKey(privateKeyReader, password);
    }

    @Override
    public X509Certificate decodePemEncodedCertificate(Reader certificateReader) {
        // JCA supports reading PEM-encoded X509Certificates fairly easily, so there is no need to use BC to read the cert
        Certificate certificate;

        // the JCA CertificateFactory takes an InputStream, so convert the reader to a stream first. converting to a String first
        // is not ideal, but is relatively straightforward. (PEM certificates should only contain US_ASCII-compatible characters.)
        try (InputStream certificateAsStream = new ByteArrayInputStream(CharStreams.toString(certificateReader).getBytes(StandardCharsets.US_ASCII))) {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            certificate = certificateFactory.generateCertificate(certificateAsStream);
        } catch (CertificateException | IOException e) {
            throw new ImportException("Unable to read PEM-encoded X509Certificate", e);
        }

        if (!(certificate instanceof X509Certificate)) {
            throw new ImportException("Attempted to import non-X.509 certificate as X.509 certificate");
        }

        return (X509Certificate) certificate;
    }

    /**
     * Loads the KeyStore from the specified InputStream. The InputStream is not closed after the KeyStore has been read.
     *
     * @param file         file containing a KeyStore
     * @param keyStoreType KeyStore type, such as "JKS" or "PKCS12"
     * @param password     password of the KeyStore
     * @return KeyStore loaded from the input stream
     */
    @Override
    public KeyStore loadKeyStore(File file, String keyStoreType, String password) {
        KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance(keyStoreType);
        } catch (KeyStoreException e) {
            throw new KeyStoreAccessException("Unable to get KeyStore instance of type: " + keyStoreType, e);
        }

        try (InputStream keystoreAsStream = new FileInputStream(file)) {
            keyStore.load(keystoreAsStream, password.toCharArray());
        } catch (IOException e) {
            throw new ImportException("Unable to read KeyStore from file: " + file.getName(), e);
        } catch (CertificateException | NoSuchAlgorithmException e) {
            throw new ImportException("Error while reading KeyStore", e);
        }

        return keyStore;
    }

    /**
     * Exports the keyStore to the specified file.
     *
     * @param file             file to save the KeyStore to
     * @param keyStore         KeyStore to export
     * @param keystorePassword the password for the KeyStore
     */
    @Override
    public void saveKeyStore(File file, KeyStore keyStore, String keystorePassword) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            keyStore.store(fos, keystorePassword.toCharArray());
        } catch (CertificateException | NoSuchAlgorithmException | IOException | KeyStoreException e) {
            throw new KeyStoreAccessException("Unable to save KeyStore to file: " + file.getName(), e);
        }
    }

    @Override
    public KeyManager[] getKeyManagers(KeyStore keyStore, String keyStorePassword) {
        return KeyStoreUtil.getKeyManagers(keyStore, keyStorePassword, null, null);
    }
}
