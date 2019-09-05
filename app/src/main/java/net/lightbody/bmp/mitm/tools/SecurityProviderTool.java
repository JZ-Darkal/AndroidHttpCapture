package net.lightbody.bmp.mitm.tools;

import net.lightbody.bmp.mitm.CertificateAndKey;
import net.lightbody.bmp.mitm.CertificateInfo;

import java.io.File;
import java.io.Reader;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;

/**
 * Generic interface for functionality provided by a Security Provider.
 */
public interface SecurityProviderTool {
    /**
     * Creates a new self-signed CA root certificate, suitable for use signing new server certificates.
     *
     * @param certificateInfo certificate info to populate in the new root cert
     * @param keyPair         root certificate's public and private keys
     * @param messageDigest   digest to use when signing the new root certificate, such as SHA512
     * @return a new root certificate and private key
     */
    CertificateAndKey createCARootCertificate(CertificateInfo certificateInfo,
                                              KeyPair keyPair,
                                              String messageDigest);

    /**
     * Creates a new server X.509 certificate using the serverKeyPair. The new certificate will be populated with
     * information from the specified certificateInfo and will be signed using the specified caPrivateKey and messageDigest.
     *
     * @param certificateInfo   basic X.509 certificate info that will be used to create the server certificate
     * @param caRootCertificate root certificate that will be used to populate the issuer field of the server certificate
     * @param serverKeyPair     server's public and private keys
     * @param messageDigest     message digest to use when signing the server certificate, such as SHA512
     * @param caPrivateKey      root certificate private key that will be used to sign the server certificate
     * @return a new server certificate and its private key
     */
    CertificateAndKey createServerCertificate(CertificateInfo certificateInfo,
                                              X509Certificate caRootCertificate,
                                              PrivateKey caPrivateKey,
                                              KeyPair serverKeyPair,
                                              String messageDigest);

    /**
     * Assembles a Java KeyStore containing a server's certificate, private key, and the certificate authority's certificate,
     * which can be used to create an {@link javax.net.ssl.SSLContext}.
     *
     * @param keyStoreType            the KeyStore type, such as JKS or PKCS12
     * @param serverCertificateAndKey certificate and private key for the server, which will be placed in the KeyStore
     * @param rootCertificate         CA root certificate of the private key that signed the server certificate
     * @param privateKeyAlias         alias to assign the private key (with accompanying certificate chain) to in the KeyStore
     * @param password                password for the new KeyStore and private key
     * @return a new KeyStore with the server's certificate and password-protected private key
     */
    KeyStore createServerKeyStore(String keyStoreType,
                                  CertificateAndKey serverCertificateAndKey,
                                  X509Certificate rootCertificate,
                                  String privateKeyAlias,
                                  String password);

    /**
     * Assembles a Java KeyStore containing a CA root certificate and its private key.
     *
     * @param keyStoreType          the KeyStore type, such as JKS or PKCS12
     * @param rootCertificateAndKey certification authority's root certificate and private key, which will be placed in the KeyStore
     * @param privateKeyAlias       alias to assign the private key (with accompanying certificate chain) to in the KeyStore
     * @param password              password for the new KeyStore and private key
     * @return a new KeyStore with the root certificate and password-protected private key
     */
    KeyStore createRootCertificateKeyStore(String keyStoreType,
                                           CertificateAndKey rootCertificateAndKey,
                                           String privateKeyAlias,
                                           String password);

    /**
     * Encodes a private key in PEM format, encrypting it with the specified password. The private key will be encrypted
     * using the specified algorithm.
     *
     * @param privateKey            private key to encode
     * @param passwordForPrivateKey password to protect the private key
     * @param encryptionAlgorithm   algorithm to use to encrypt the private key
     * @return PEM-encoded private key as a String
     */
    String encodePrivateKeyAsPem(PrivateKey privateKey, String passwordForPrivateKey, String encryptionAlgorithm);

    /**
     * Encodes a certificate in PEM format.
     *
     * @param certificate certificate to encode
     * @return PEM-encoded certificate as a String
     */
    String encodeCertificateAsPem(Certificate certificate);

    /**
     * Decodes a PEM-encoded private key into a {@link PrivateKey}. The password may be null if the PEM-encoded private key
     * is not password-encrypted.
     *
     * @param privateKeyReader a reader for a PEM-encoded private key
     * @param password         password protecting the private key  @return the decoded private key
     */
    PrivateKey decodePemEncodedPrivateKey(Reader privateKeyReader, String password);

    /**
     * Decodes a PEM-encoded X.509 Certificate into a {@link X509Certificate}.
     *
     * @param certificateReader a reader for a PEM-encoded certificate
     * @return the decoded X.509 certificate
     */
    X509Certificate decodePemEncodedCertificate(Reader certificateReader);

    /**
     * Loads a Java KeyStore object from a file.
     *
     * @param file         KeyStore file to load
     * @param keyStoreType KeyStore type (PKCS12, JKS, etc.)
     * @param password     the KeyStore password
     * @return an initialized Java KeyStore object
     */
    KeyStore loadKeyStore(File file, String keyStoreType, String password);

    /**
     * Saves a Java KeyStore to a file, protecting it with the specified password.
     *
     * @param file             file to save the KeyStore to
     * @param keyStore         KeyStore to save
     * @param keystorePassword password for the KeyStore
     */
    void saveKeyStore(File file, KeyStore keyStore, String keystorePassword);

    /**
     * Retrieve the KeyManagers for the specified KeyStore.
     *
     * @param keyStore         the KeyStore to retrieve KeyManagers from
     * @param keyStorePassword the KeyStore password
     * @return KeyManagers for the specified KeyStore
     */
    KeyManager[] getKeyManagers(KeyStore keyStore, String keyStorePassword);
}
