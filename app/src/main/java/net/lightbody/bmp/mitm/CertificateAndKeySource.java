package net.lightbody.bmp.mitm;

/**
 * A CertificateAndKeySource generates {@link CertificateAndKey}s, i.e. the root certificate and private key used
 * to sign impersonated certificates of upstream servers. Implementations of this interface load impersonation materials
 * from various sources, including Java KeyStores, JKS files, etc., or generate them on-the-fly.
 */
public interface CertificateAndKeySource {
    /**
     * Loads a certificate and its corresponding private key. Every time this method is called, it should return the same
     * certificate and private key (although it may be a different {@link CertificateAndKey} instance).
     *
     * @return certificate and its corresponding private key
     */
    CertificateAndKey load();
}
