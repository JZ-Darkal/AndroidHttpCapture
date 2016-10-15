package net.lightbody.bmp.mitm.util;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import net.lightbody.bmp.mitm.exception.KeyStoreAccessException;
import net.lightbody.bmp.mitm.exception.TrustSourceException;
import net.lightbody.bmp.mitm.exception.UncheckedIOException;
import net.lightbody.bmp.mitm.tools.DefaultSecurityProviderTool;
import net.lightbody.bmp.mitm.tools.SecurityProviderTool;
import net.lightbody.bmp.util.ClasspathResourceUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Utility class for interacting with the default trust stores on this JVM.
 */
public class TrustUtil {
    private static final Logger log = LoggerFactory.getLogger(TrustUtil.class);

    /**
     * Regex that matches a single certificate within a PEM file containing (potentially multiple) certificates.
     */
    private static final Pattern CA_PEM_PATTERN = Pattern.compile("-----BEGIN CERTIFICATE-----.+?-----END CERTIFICATE-----", Pattern.DOTALL);

    /**
     * The file containing the built-in list of trusted CAs.
     */
    private static final String DEFAULT_TRUSTED_CA_RESOURCE = "/cacerts.pem";

    /**
     * Empty X509 certificate array, useful for indicating an empty root CA trust store.
     */
    public static final X509Certificate[] EMPTY_CERTIFICATE_ARRAY = new X509Certificate[0];

    /**
     * Security provider used to transform PEM files into Certificates.
     * TODO: Modify the architecture of TrustUtil and TrustSource so that they do not need a hard-coded SecurityProviderTool.
     */
    private static final SecurityProviderTool securityProviderTool = new DefaultSecurityProviderTool();

    /**
     * Singleton for the list of CAs trusted by Java by default.
     */
    private static final Supplier<X509Certificate[]> javaTrustedCAs = Suppliers.memoize(new Supplier<X509Certificate[]>() {
        @Override
        public X509Certificate[] get() {
            X509TrustManager defaultTrustManager = getDefaultJavaTrustManager();

            X509Certificate[] defaultJavaTrustedCerts = defaultTrustManager.getAcceptedIssuers();

            if (defaultJavaTrustedCerts != null) {
                return defaultJavaTrustedCerts;
            } else {
                return EMPTY_CERTIFICATE_ARRAY;
            }
        }
    });

    /**
     * Singleton for the built-in list of trusted CAs.
     */
    private static final Supplier<X509Certificate[]> builtinTrustedCAs = Suppliers.memoize(new Supplier<X509Certificate[]>() {
        @Override
        public X509Certificate[] get() {
            try {
                // the file may contain UTF-8 characters, but the PEM-encoded certificate data itself must be US-ASCII
                String allCAs = ClasspathResourceUtil.classpathResourceToString(DEFAULT_TRUSTED_CA_RESOURCE, Charset.forName("UTF-8"));

                return readX509CertificatesFromPem(allCAs);
            } catch (UncheckedIOException e) {
                log.warn("Unable to load built-in trusted CAs; no built-in CAs will be trusted", e);
                return new X509Certificate[0];
            }
        }
    });

    /**
     * Returns the built-in list of trusted CAs. This is a copy of cURL's list (https://curl.haxx.se/ca/cacert.pem), which is
     * ultimately derived from Firefox/NSS' list of trusted CAs.
     */
    public static X509Certificate[] getBuiltinTrustedCAs() {
        return builtinTrustedCAs.get();
    }

    /**
     * Returns the list of root CAs trusted by default in this JVM, according to the TrustManager returned by
     * {@link #getDefaultJavaTrustManager()}.
     */
    public static  X509Certificate[] getJavaTrustedCAs() {
        return javaTrustedCAs.get();
    }

    /**
     * Parses a String containing zero or more PEM-encoded X509 certificates into an array of {@link X509Certificate}.
     * Everything outside of BEGIN CERTIFICATE and END CERTIFICATE lines will be ignored.
     *
     * @param pemEncodedCAs a String containing PEM-encoded certficiates
     * @return array containing certificates in the String
     */
    public static X509Certificate[] readX509CertificatesFromPem(String pemEncodedCAs) {
        List<X509Certificate> certificates = new ArrayList<>(500);

        Matcher pemMatcher = CA_PEM_PATTERN.matcher(pemEncodedCAs);

        while (pemMatcher.find()) {
            String singleCAPem = pemMatcher.group();

            X509Certificate certificate = readSingleX509Certificate(singleCAPem);
            certificates.add(certificate);
        }

        return certificates.toArray(new X509Certificate[0]);
    }

    /**
     * Parses a single PEM-encoded X509 certificate into an {@link X509Certificate}.
     *
     * @param x509CertificateAsPem PEM-encoded X509 certificate
     * @return parsed Java X509Certificate
     */
    public static X509Certificate readSingleX509Certificate(String x509CertificateAsPem) {
        return securityProviderTool.decodePemEncodedCertificate(new StringReader(x509CertificateAsPem));
    }

    /**
     * Returns a new instance of the default TrustManager for this JVM. Uses the default JVM trust store, which is
     * generally the cacerts file in JAVA_HOME/jre/lib/security, but this can be overridden using JVM parameters.
     */
    public static X509TrustManager getDefaultJavaTrustManager() {
        TrustManagerFactory tmf;
        try {
            tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            // initializing the trust store with a null KeyStore will load the default JVM trust store
            tmf.init((KeyStore) null);
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            throw new TrustSourceException("Unable to retrieve default TrustManagerFactory", e);
        }

        // Get hold of the default trust manager
        for (TrustManager tm : tmf.getTrustManagers()) {
            if (tm instanceof X509TrustManager) {
                return (X509TrustManager) tm;
            }
        }

        // didn't find an X509TrustManager
        throw new TrustSourceException("No X509TrustManager found");
    }

    /**
     * Extracts the {@link java.security.KeyStore.TrustedCertificateEntry}s from the specified KeyStore. All other entry
     * types, including private keys, will be ignored.
     *
     * @param trustStore keystore containing trusted certificate entries
     * @return the trusted certificate entries in the specified keystore
     */
    public static List<X509Certificate> extractTrustedCertificateEntries(KeyStore trustStore) {
        try {
            Enumeration<String> aliases = trustStore.aliases();
            List<String> keyStoreAliases = Collections.list(aliases);

            List<X509Certificate> trustedCertificates = new ArrayList<>(keyStoreAliases.size());

            for (String alias : keyStoreAliases) {
                if (trustStore.entryInstanceOf(alias, KeyStore.TrustedCertificateEntry.class)) {
                    Certificate certificate = trustStore.getCertificate(alias);
                    if (!(certificate instanceof X509Certificate)) {
                        log.debug("Skipping non-X509Certificate in KeyStore. Certificate type: {}", certificate.getType());
                        continue;
                    }

                    trustedCertificates.add((X509Certificate) certificate);
                }
            }

            return trustedCertificates;
        } catch (KeyStoreException e) {
            throw new KeyStoreAccessException("Error occurred while retrieving trusted CAs from KeyStore", e);
        }
    }
}
