package net.lightbody.bmp.mitm.trustmanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import cn.darkal.networkdiagnosis.Utils.AbstractX509ExtendedTrustManager;
import io.netty.util.internal.EmptyArrays;

/**
 * An {@link AbstractX509ExtendedTrustManager} and {@link javax.net.ssl.X509TrustManager} that will accept all server and client
 * certificates. Before accepting a certificate, the InsecureExtendedTrustManager uses the default AbstractX509ExtendedTrustManager
 * to determine if the certificate would otherwise be trusted, and logs a debug-level message if it is not trusted.
 */
public class InsecureExtendedTrustManager extends AbstractX509ExtendedTrustManager {
    private static final Logger log = LoggerFactory.getLogger(InsecureExtendedTrustManager.class);

    /**
     * An {@link AbstractX509ExtendedTrustManager} that does no certificate validation whatsoever.
     */
    private static final AbstractX509ExtendedTrustManager NOOP_EXTENDED_TRUST_MANAGER = new AbstractX509ExtendedTrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return EmptyArrays.EMPTY_X509_CERTIFICATES;
        }
    };

    /**
     * The default extended trust manager, which will be used to determine if certificates would otherwise be trusted.
     */
    protected static final AbstractX509ExtendedTrustManager DEFAULT_EXTENDED_TRUST_MANAGER = getDefaultExtendedTrustManager();

    /**
     * Returns the JDK's default AbstractX509ExtendedTrustManager, or a no-op trust manager if the default cannot be found.
     */
    private static AbstractX509ExtendedTrustManager getDefaultExtendedTrustManager() {
        TrustManagerFactory trustManagerFactory;
        try {
            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            // initialize the TrustManagerFactory with the default KeyStore
            trustManagerFactory.init((KeyStore) null);
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            log.debug("Unable to initialize default TrustManagerFactory. Using no-op AbstractX509ExtendedTrustManager.", e);
            return NOOP_EXTENDED_TRUST_MANAGER;
        }

        // find the AbstractX509ExtendedTrustManager in the list of registered trust managers
        for (TrustManager tm : trustManagerFactory.getTrustManagers()) {
            if (tm instanceof AbstractX509ExtendedTrustManager) {
                return (AbstractX509ExtendedTrustManager) tm;
            }
        }

        // no default AbstractX509ExtendedTrustManager found, so return a no-op
        log.debug("No default AbstractX509ExtendedTrustManager found. Using no-op.");
        return NOOP_EXTENDED_TRUST_MANAGER;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {
        try {
            DEFAULT_EXTENDED_TRUST_MANAGER.checkClientTrusted(x509Certificates, s, socket);
        } catch (CertificateException e) {
            log.debug("Accepting an untrusted client certificate: {}", x509Certificates[0].getSubjectDN(), e);
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {
        try {
            DEFAULT_EXTENDED_TRUST_MANAGER.checkServerTrusted(x509Certificates, s, socket);
        } catch (CertificateException e) {
            log.debug("Accepting an untrusted server certificate: {}", x509Certificates[0].getSubjectDN(), e);
        }
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {
        try {
            DEFAULT_EXTENDED_TRUST_MANAGER.checkClientTrusted(x509Certificates, s, sslEngine);
        } catch (CertificateException e) {
            log.debug("Accepting an untrusted client certificate: {}", x509Certificates[0].getSubjectDN(), e);
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {
        try {
            DEFAULT_EXTENDED_TRUST_MANAGER.checkServerTrusted(x509Certificates, s, sslEngine);
        } catch (CertificateException e) {
            log.debug("Accepting an untrusted server certificate: {}", x509Certificates[0].getSubjectDN(), e);
        }
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        try {
            DEFAULT_EXTENDED_TRUST_MANAGER.checkClientTrusted(x509Certificates, s);
        } catch (CertificateException e) {
            log.debug("Accepting an untrusted client certificate: {}", x509Certificates[0].getSubjectDN(), e);
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        try {
            DEFAULT_EXTENDED_TRUST_MANAGER.checkServerTrusted(x509Certificates, s);
        } catch (CertificateException e) {
            log.debug("Accepting an untrusted server certificate: {}", x509Certificates[0].getSubjectDN(), e);
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return EmptyArrays.EMPTY_X509_CERTIFICATES;
    }
}
