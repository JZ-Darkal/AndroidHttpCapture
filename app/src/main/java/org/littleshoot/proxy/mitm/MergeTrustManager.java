package org.littleshoot.proxy.mitm;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class MergeTrustManager implements X509TrustManager {

    private final X509TrustManager addedTm;
    private final X509TrustManager javaTm;

    public MergeTrustManager(KeyStore trustStore)
            throws NoSuchAlgorithmException, KeyStoreException {
        if (trustStore == null) {
            throw new IllegalArgumentException("Missed trust store");
        }
        this.javaTm = defaultTrustManager(null);
        this.addedTm = defaultTrustManager(trustStore);
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        List<X509Certificate> issuers = new ArrayList<X509Certificate>();
        issuers.addAll(Arrays.asList(addedTm.getAcceptedIssuers()));
        issuers.addAll(Arrays.asList(javaTm.getAcceptedIssuers()));
        return issuers.toArray(new X509Certificate[issuers.size()]);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        try {
            addedTm.checkServerTrusted(chain, authType);
        } catch (CertificateException e) {
            javaTm.checkServerTrusted(chain, authType);
        }
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        try {
            javaTm.checkClientTrusted(chain, authType);
        } catch (CertificateException e) {
            addedTm.checkClientTrusted(chain, authType);
        }
    }

    private X509TrustManager defaultTrustManager(KeyStore trustStore)
            throws NoSuchAlgorithmException, KeyStoreException {
        String tma = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tma);
        tmf.init(trustStore);
        TrustManager[] trustManagers = tmf.getTrustManagers();
        for (TrustManager each : trustManagers) {
            if (each instanceof X509TrustManager) {
                return (X509TrustManager) each;
            }
        }
        throw new IllegalStateException("Missed X509TrustManager in "
                + Arrays.toString(trustManagers));
    }

}
