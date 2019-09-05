package net.lightbody.bmp.mitm;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import net.lightbody.bmp.mitm.keys.KeyGenerator;
import net.lightbody.bmp.mitm.keys.RSAKeyGenerator;
import net.lightbody.bmp.mitm.tools.DefaultSecurityProviderTool;
import net.lightbody.bmp.mitm.tools.SecurityProviderTool;
import net.lightbody.bmp.mitm.util.EncryptionUtil;
import net.lightbody.bmp.mitm.util.MitmConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A {@link CertificateAndKeySource} that dynamically generates a CA root certificate and private key. The certificate
 * and key will only be generated once; all subsequent calls to {@link #load()} will return the same materials. To save
 * the generated certificate and/or private key for installation in a browser or other client, use one of the encode
 * or save methods:
 * <ul>
 * <li>{@link #encodeRootCertificateAsPem()}</li>
 * <li>{@link #encodePrivateKeyAsPem(String)}</li>
 * <li>{@link #saveRootCertificateAsPemFile(File)}</li>
 * <li>{@link #savePrivateKeyAsPemFile(File, String)}</li>
 * <li>{@link #saveRootCertificateAndKey(String, File, String, String)}</li>
 * </ul>
 */
public class RootCertificateGenerator implements CertificateAndKeySource {
    private static final Logger log = LoggerFactory.getLogger(RootCertificateGenerator.class);
    /**
     * The default algorithm to use when encrypting objects in PEM files (such as private keys).
     */
    private static final String DEFAULT_PEM_ENCRYPTION_ALGORITHM = "AES-128-CBC";
    private final CertificateInfo rootCertificateInfo;
    private final String messageDigest;
    private final KeyGenerator keyGenerator;
    private final SecurityProviderTool securityProviderTool;
    /**
     * The new root certificate and private key are generated only once, even across multiple calls to {@link #load()}},
     * to allow users to save the new generated root certificate for use in browsers/other HTTP clients.
     */
    private final Supplier<CertificateAndKey> generatedCertificateAndKey = Suppliers.memoize(new Supplier<CertificateAndKey>() {
        @Override
        public CertificateAndKey get() {
            return generateRootCertificate();
        }
    });

    public RootCertificateGenerator(CertificateInfo rootCertificateInfo,
                                    String messageDigest,
                                    KeyGenerator keyGenerator,
                                    SecurityProviderTool securityProviderTool) {
        if (rootCertificateInfo == null) {
            throw new IllegalArgumentException("CA root certificate cannot be null");
        }

        if (messageDigest == null) {
            throw new IllegalArgumentException("Message digest cannot be null");
        }

        if (keyGenerator == null) {
            throw new IllegalArgumentException("Key generator cannot be null");
        }

        if (securityProviderTool == null) {
            throw new IllegalArgumentException("Certificate tool cannot be null");
        }

        this.rootCertificateInfo = rootCertificateInfo;
        this.messageDigest = messageDigest;
        this.keyGenerator = keyGenerator;
        this.securityProviderTool = securityProviderTool;
    }

    /**
     * Convenience method to return a new {@link Builder} instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a default CN field for a certificate, using the hostname of this machine and the current time.
     */
    private static String getDefaultCommonName() {
        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostname = "localhost";
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");

        String currentDateTime = dateFormat.format(new Date());

        String defaultCN = "Generated CA (" + hostname + ") " + currentDateTime;

        // CN fields can only be 64 characters
        return defaultCN.length() <= 64 ? defaultCN : defaultCN.substring(0, 63);
    }

    @Override
    public CertificateAndKey load() {
        // only generate the materials once, so they can can be saved if desired
        return generatedCertificateAndKey.get();
    }

    /**
     * Generates a new CA root certificate and private key.
     *
     * @return new root certificate and private key
     */
    private CertificateAndKey generateRootCertificate() {
        long generationStart = System.currentTimeMillis();

        // create the public and private key pair that will be used to sign the generated certificate
        KeyPair caKeyPair = keyGenerator.generate();

        // delegate the creation and signing of the X.509 certificate to the certificate tool
        CertificateAndKey certificateAndKey = securityProviderTool.createCARootCertificate(
                rootCertificateInfo,
                caKeyPair,
                messageDigest);

        long generationFinished = System.currentTimeMillis();

        log.info("Generated CA root certificate and private key in {}ms. Key generator: {}. Signature algorithm: {}.",
                generationFinished - generationStart, keyGenerator, messageDigest);

        return certificateAndKey;
    }

    /**
     * Returns the generated root certificate as a PEM-encoded String.
     */
    public String encodeRootCertificateAsPem() {
        return securityProviderTool.encodeCertificateAsPem(generatedCertificateAndKey.get().getCertificate());
    }

    /**
     * Returns the generated private key as a PEM-encoded String, encrypted using the specified password and the
     * {@link #DEFAULT_PEM_ENCRYPTION_ALGORITHM}.
     *
     * @param privateKeyPassword password to use to encrypt the private key
     */
    public String encodePrivateKeyAsPem(String privateKeyPassword) {
        return securityProviderTool.encodePrivateKeyAsPem(generatedCertificateAndKey.get().getPrivateKey(), privateKeyPassword, DEFAULT_PEM_ENCRYPTION_ALGORITHM);
    }

    /**
     * Saves the root certificate as PEM-encoded data to the specified file.
     */
    public void saveRootCertificateAsPemFile(File file) {
        String pemEncodedCertificate = securityProviderTool.encodeCertificateAsPem(generatedCertificateAndKey.get().getCertificate());

        EncryptionUtil.writePemStringToFile(file, pemEncodedCertificate);
    }

    /**
     * Saves the private key as PEM-encoded data to a file, using the specified password to encrypt the private key and
     * the {@link #DEFAULT_PEM_ENCRYPTION_ALGORITHM}. If the password is null, the private key will be stored unencrypted.
     * In general, private keys should not be stored unencrypted.
     *
     * @param file                  file to save the private key to
     * @param passwordForPrivateKey password to protect the private key
     */
    public void savePrivateKeyAsPemFile(File file, String passwordForPrivateKey) {
        String pemEncodedPrivateKey = securityProviderTool.encodePrivateKeyAsPem(generatedCertificateAndKey.get().getPrivateKey(), passwordForPrivateKey, DEFAULT_PEM_ENCRYPTION_ALGORITHM);

        EncryptionUtil.writePemStringToFile(file, pemEncodedPrivateKey);
    }

    /**
     * Saves the generated certificate and private key as a file, using the specified password to protect the key store.
     *
     * @param keyStoreType    the KeyStore type, such as PKCS12 or JKS
     * @param file            file to export the root certificate and private key to
     * @param privateKeyAlias alias for the private key in the KeyStore
     * @param password        password for the private key and the KeyStore
     */
    public void saveRootCertificateAndKey(String keyStoreType,
                                          File file,
                                          String privateKeyAlias,
                                          String password) {
        CertificateAndKey certificateAndKey = generatedCertificateAndKey.get();

        KeyStore keyStore = securityProviderTool.createRootCertificateKeyStore(keyStoreType, certificateAndKey, privateKeyAlias, password);

        securityProviderTool.saveKeyStore(file, keyStore, password);
    }

    /**
     * A Builder for {@link RootCertificateGenerator}s. Initialized with suitable default values suitable for most purposes.
     */
    public static class Builder {
        private CertificateInfo certificateInfo = new CertificateInfo()
                .commonName(getDefaultCommonName())
                .organization("CA dynamically generated by LittleProxy")
                .notBefore(new Date(System.currentTimeMillis() - 365L * 24L * 60L * 60L * 1000L))
                .notAfter(new Date(System.currentTimeMillis() + 365L * 24L * 60L * 60L * 1000L));

        private KeyGenerator keyGenerator = new RSAKeyGenerator();

        private String messageDigest = MitmConstants.DEFAULT_MESSAGE_DIGEST;

        private SecurityProviderTool securityProviderTool = new DefaultSecurityProviderTool();

        /**
         * Certificate info to use to generate the root certificate. Reasonable default values will be used if certificate
         * info is not supplied.
         */
        public Builder certificateInfo(CertificateInfo certificateInfo) {
            this.certificateInfo = certificateInfo;
            return this;
        }

        /**
         * The {@link KeyGenerator} that will be used to generate the root certificate's public and private keys.
         */
        public Builder keyGenerator(KeyGenerator keyGenerator) {
            this.keyGenerator = keyGenerator;
            return this;
        }

        /**
         * The message digest that will be used when self-signing the root certificates.
         */
        public Builder messageDigest(String messageDigest) {
            this.messageDigest = messageDigest;
            return this;
        }

        /**
         * The {@link SecurityProviderTool} implementation that will be used to generate certificates.
         */
        public Builder certificateTool(SecurityProviderTool securityProviderTool) {
            this.securityProviderTool = securityProviderTool;
            return this;
        }

        public RootCertificateGenerator build() {
            return new RootCertificateGenerator(certificateInfo, messageDigest, keyGenerator, securityProviderTool);
        }
    }
}
