package net.lightbody.bmp.mitm;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import net.lightbody.bmp.mitm.tools.DefaultSecurityProviderTool;
import net.lightbody.bmp.mitm.tools.SecurityProviderTool;
import net.lightbody.bmp.mitm.util.EncryptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.StringReader;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * Loads impersonation materials from two separate, PEM-encoded files: a CA root certificate and its corresponding
 * private key.
 */
public class PemFileCertificateSource implements CertificateAndKeySource {
    private static final Logger log = LoggerFactory.getLogger(PemFileCertificateSource.class);

    private final File certificateFile;
    private final File privateKeyFile;
    private final String privateKeyPassword;

    private SecurityProviderTool securityProviderTool = new DefaultSecurityProviderTool();

    private final Supplier<CertificateAndKey> certificateAndKey = Suppliers.memoize(new Supplier<CertificateAndKey>() {
        @Override
        public CertificateAndKey get() {
            return loadCertificateAndKeyFiles();
        }
    });

    /**
     * Creates a {@link CertificateAndKeySource} that loads the certificate and private key from PEM files.
     *
     * @param certificateFile    PEM-encoded file containing the root certificate
     * @param privateKeyFile     PEM-encoded file continaing the certificate's private key
     * @param privateKeyPassword password for the private key
     */
    public PemFileCertificateSource(File certificateFile, File privateKeyFile, String privateKeyPassword) {
        this.certificateFile = certificateFile;
        this.privateKeyFile = privateKeyFile;
        this.privateKeyPassword = privateKeyPassword;
    }

    /**
     * Override the default {@link SecurityProviderTool} used to load the PEM files.
     */
    public PemFileCertificateSource certificateTool(SecurityProviderTool securityProviderTool) {
        this.securityProviderTool = securityProviderTool;
        return this;
    }

    @Override
    public CertificateAndKey load() {
        return certificateAndKey.get();
    }

    private CertificateAndKey loadCertificateAndKeyFiles() {
        if (certificateFile == null) {
            throw new IllegalArgumentException("PEM root certificate file cannot be null");
        }

        if (privateKeyFile == null) {
            throw new IllegalArgumentException("PEM private key file cannot be null");
        }

        if (privateKeyPassword == null) {
            log.warn("Attempting to load private key from file without password. Private keys should be password-protected.");
        }

        String pemEncodedCertificate = EncryptionUtil.readPemStringFromFile(certificateFile);
        X509Certificate certificate = securityProviderTool.decodePemEncodedCertificate(new StringReader(pemEncodedCertificate));

        String pemEncodedPrivateKey = EncryptionUtil.readPemStringFromFile(privateKeyFile);
        PrivateKey privateKey = securityProviderTool.decodePemEncodedPrivateKey(new StringReader(pemEncodedPrivateKey), privateKeyPassword);

        return new CertificateAndKey(certificate, privateKey);
    }
}
