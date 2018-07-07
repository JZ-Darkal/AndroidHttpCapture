package net.lightbody.bmp.mitm;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * A simple container for an X.509 certificate and its corresponding private key.
 */
public class CertificateAndKey {
    private final X509Certificate certificate;
    private final PrivateKey privateKey;

    public CertificateAndKey(X509Certificate certificate, PrivateKey privateKey) {
        this.certificate = certificate;
        this.privateKey = privateKey;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
