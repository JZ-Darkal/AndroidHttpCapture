package net.lightbody.bmp.mitm;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * A simple adapter that produces a {@link CertificateAndKey} from existing {@link X509Certificate} and {@link PrivateKey}
 * java objects.
 */
public class ExistingCertificateSource implements CertificateAndKeySource {
    private final X509Certificate rootCertificate;
    private final PrivateKey privateKey;

    public ExistingCertificateSource(X509Certificate rootCertificate, PrivateKey privateKey) {
        if (rootCertificate == null) {
            throw new IllegalArgumentException("CA root certificate cannot be null");
        }

        if (privateKey == null) {
            throw new IllegalArgumentException("Private key cannot be null");
        }

        this.rootCertificate = rootCertificate;
        this.privateKey = privateKey;
    }

    @Override
    public CertificateAndKey load() {
        return new CertificateAndKey(rootCertificate, privateKey);
    }
}
