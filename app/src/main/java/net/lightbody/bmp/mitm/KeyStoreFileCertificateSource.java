package net.lightbody.bmp.mitm;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import net.lightbody.bmp.mitm.exception.CertificateSourceException;
import net.lightbody.bmp.mitm.tools.DefaultSecurityProviderTool;
import net.lightbody.bmp.mitm.tools.SecurityProviderTool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.KeyStore;

/**
 * Loads a KeyStore from a file or classpath resource. If configured with a File object, attempts to load the KeyStore
 * from the specified file. Otherwise, attempts to load the KeyStore from a classpath resource.
 */
public class KeyStoreFileCertificateSource implements CertificateAndKeySource {
    private static final Logger log = LoggerFactory.getLogger(KeyStoreFileCertificateSource.class);

    private final String keyStoreClasspathResource;
    private final File keyStoreFile;

    private final String keyStoreType;

    private final String keyStorePassword;
    private final String privateKeyAlias;

    private SecurityProviderTool securityProviderTool = new DefaultSecurityProviderTool();

    private final Supplier<CertificateAndKey> certificateAndKey = Suppliers.memoize(new Supplier<CertificateAndKey>() {
        @Override
        public CertificateAndKey get() {
            return loadKeyStore();
        }
    });

    /**
     * Creates a {@link CertificateAndKeySource} that loads an existing {@link KeyStore} from a classpath resource.
     *
     * @param keyStoreType              the KeyStore type, such as PKCS12 or JKS
     * @param keyStoreClasspathResource classpath resource to load (for example, "/keystore.jks")
     * @param privateKeyAlias           the alias of the private key in the KeyStore
     * @param keyStorePassword          te KeyStore password
     */
    public KeyStoreFileCertificateSource(String keyStoreType, String keyStoreClasspathResource, String privateKeyAlias, String keyStorePassword) {
        if (keyStoreClasspathResource == null) {
            throw new IllegalArgumentException("The classpath location of the KeyStore cannot be null");
        }

        if (keyStoreType == null) {
            throw new IllegalArgumentException("KeyStore type cannot be null");
        }

        if (privateKeyAlias == null) {
            throw new IllegalArgumentException("Alias of the private key in the KeyStore cannot be null");
        }

        this.keyStoreClasspathResource = keyStoreClasspathResource;
        this.keyStoreFile = null;

        this.keyStoreType = keyStoreType;
        this.keyStorePassword = keyStorePassword;
        this.privateKeyAlias = privateKeyAlias;
    }

    /**
     * Creates a {@link CertificateAndKeySource} that loads an existing {@link KeyStore} from a classpath resource.
     *
     * @param keyStoreType     the KeyStore type, such as PKCS12 or JKS
     * @param keyStoreFile     KeyStore file to load
     * @param privateKeyAlias  the alias of the private key in the KeyStore
     * @param keyStorePassword te KeyStore password
     */
    public KeyStoreFileCertificateSource(String keyStoreType, File keyStoreFile, String privateKeyAlias, String keyStorePassword) {
        if (keyStoreFile == null) {
            throw new IllegalArgumentException("The KeyStore file cannot be null");
        }

        if (keyStoreType == null) {
            throw new IllegalArgumentException("KeyStore type cannot be null");
        }

        if (privateKeyAlias == null) {
            throw new IllegalArgumentException("Alias of the private key in the KeyStore cannot be null");
        }

        this.keyStoreFile = keyStoreFile;
        this.keyStoreClasspathResource = null;

        this.keyStoreType = keyStoreType;
        this.keyStorePassword = keyStorePassword;
        this.privateKeyAlias = privateKeyAlias;
    }

    /**
     * Override the default {@link SecurityProviderTool} used to load the KeyStore.
     */
    public KeyStoreFileCertificateSource certificateTool(SecurityProviderTool securityProviderTool) {
        this.securityProviderTool = securityProviderTool;
        return this;
    }

    @Override
    public CertificateAndKey load() {
        return certificateAndKey.get();

    }

    /**
     * Loads the {@link CertificateAndKey} from the KeyStore using the {@link SecurityProviderTool}.
     */
    private CertificateAndKey loadKeyStore() {
        // load the KeyStore from the file or classpath resource, then delegate to a KeyStoreCertificateSource
        KeyStore keyStore;
        if (keyStoreFile != null) {
            keyStore = securityProviderTool.loadKeyStore(keyStoreFile, keyStoreType, keyStorePassword);
        } else {
            // copy the classpath resource to a temporary file and load the keystore from that temp file
            Path tempKeyStoreFile = null;
            try (InputStream keystoreAsStream = KeyStoreFileCertificateSource.class.getResourceAsStream(keyStoreClasspathResource)) {
                tempKeyStoreFile = Files.createTempFile("keystore", "temp");
                Files.copy(keystoreAsStream, tempKeyStoreFile, StandardCopyOption.REPLACE_EXISTING);

                keyStore = securityProviderTool.loadKeyStore(tempKeyStoreFile.toFile(), keyStoreType, keyStorePassword);
            } catch (IOException e) {
                throw new CertificateSourceException("Unable to open KeyStore classpath resource: " + keyStoreClasspathResource, e);
            } finally {
                if (tempKeyStoreFile != null) {
                    try {
                        Files.deleteIfExists(tempKeyStoreFile);
                    } catch (IOException e) {
                        log.warn("Unable to delete temporary KeyStore file: {}.", tempKeyStoreFile.toAbsolutePath());
                    }
                }
            }
        }

        KeyStoreCertificateSource keyStoreCertificateSource = new KeyStoreCertificateSource(keyStore, privateKeyAlias, keyStorePassword);

        return keyStoreCertificateSource.load();
    }
}
