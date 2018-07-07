package net.lightbody.bmp.mitm;

import com.google.common.collect.ObjectArrays;
import com.google.common.io.Files;
import net.lightbody.bmp.mitm.exception.UncheckedIOException;
import net.lightbody.bmp.mitm.util.TrustUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * A source of trusted root certificate authorities. Provides static methods to obtain default trust sources:
 * <ul>
 *     <li>{@link #defaultTrustSource()}- both the built-in and JVM-trusted CAs</li>
 *     <li>{@link #javaTrustSource()} - only default CAs trusted by the JVM</li>
 *     <li>{@link #builtinTrustSource()} - only built-in trusted CAs (ultimately derived from Firefox's trust list)</li>
 * </ul>
 *
 * Custom TrustSources can be built by starting with {@link #empty()}, then calling the various add() methods to add
 * PEM-encoded files and Strings, KeyStores, and X509Certificates to the TrustSource. For example:
 * <p/>
 * <code>
 *      TrustSource customTrustSource = TrustSource.empty()
 *              .add(myX509Certificate)
 *              .add(pemFileContainingMyCA)
 *              .add(javaKeyStore);
 * </code>
 * <p/>
 * <b>Note:</b> This class is immutable, so calls to add() will return a new instance, rather than modifying the existing instance.
 */
public class TrustSource {
    /**
     * The default TrustSource. To obtain this TrustSource, use {@link #defaultTrustSource()}.
     */
    private static final TrustSource DEFAULT_TRUST_SOURCE = TrustSource.javaTrustSource().add(TrustSource.builtinTrustSource());

    /**
     * The root CAs this TrustSource trusts.
     */
    private final X509Certificate[] trustedCAs;

    /**
     * Creates a TrustSource that contains no trusted certificates. For public use, see {@link #empty()}.
     */
    protected TrustSource() {
        this(TrustUtil.EMPTY_CERTIFICATE_ARRAY);
    }

    /**
     * Creates a TrustSource that considers only the specified certificates as "trusted". For public use,
     * use {@link #empty()} followed by {@link #add(X509Certificate...)}.
     *
     * @param trustedCAs root CAs to trust
     */
    protected TrustSource(X509Certificate... trustedCAs) {
        if (trustedCAs == null) {
            this.trustedCAs = TrustUtil.EMPTY_CERTIFICATE_ARRAY;
        } else {
            this.trustedCAs = trustedCAs;
        }
    }

    /**
     * Returns the X509 certificates considered "trusted" by this TrustSource. This method will not return null, but
     * may return an empty array.
     */
    public X509Certificate[] getTrustedCAs() {
        return trustedCAs;
    }

    /**
     * Returns a TrustSource that contains no trusted CAs. Can be used in conjunction with the add() methods to build
     * a TrustSource containing custom CAs from a variety of sources (PEM files, KeyStores, etc.).
     */
    public static TrustSource empty() {
        return new TrustSource();
    }

    /**
     * Returns a TrustSource containing the default trusted CAs. By default, contains both the JVM's trusted CAs and the
     * built-in trusted CAs (Firefox's trusted CAs).
     */
    public static TrustSource defaultTrustSource() {
        return DEFAULT_TRUST_SOURCE;
    }

    /**
     * Returns a TrustSource containing only the builtin trusted CAs and does not include the JVM's trusted CAs.
     * See {@link TrustUtil#getBuiltinTrustedCAs()}.
     */
    public static TrustSource builtinTrustSource() {
        return new TrustSource(TrustUtil.getBuiltinTrustedCAs());
    }

    /**
     * Returns a TrustSource containing the default CAs trusted by this JVM. See {@link TrustUtil#getJavaTrustedCAs()}.
     */
    public static TrustSource javaTrustSource() {
        return new TrustSource(TrustUtil.getJavaTrustedCAs());
    }

    /**
     * Returns a new TrustSource containing the same trusted CAs as this TrustSource, plus zero or more CAs contained in
     * the PEM-encoded String. The String may contain multiple certificates and may contain comments or other non-PEM-encoded
     * text, as long as the PEM-encoded certificates are delimited by appropriate BEGIN_CERTIFICATE and END_CERTIFICATE
     * text blocks.
     *
     * @param trustedPemEncodedCAs String containing PEM-encoded certificates to trust
     * @return a new TrustSource containing this TrustSource's trusted CAs plus the CAs in the specified String
     */
    public TrustSource add(String trustedPemEncodedCAs) {
        if (trustedPemEncodedCAs == null) {
            throw new IllegalArgumentException("PEM-encoded trusted CA String cannot be null");
        }

        X509Certificate[] trustedCertificates = TrustUtil.readX509CertificatesFromPem(trustedPemEncodedCAs);

        return add(trustedCertificates);
    }

    /**
     * Returns a new TrustSource containing the same trusted CAs as this TrustSource, plus zero or more additional
     * trusted X509Certificates. If trustedCertificates is null or empty, returns this same TrustSource.
     *
     * @param trustedCertificates X509Certificates of CAs to trust
     * @return a new TrustSource containing this TrustSource's trusted CAs plus the specified CAs
     */
    public TrustSource add(X509Certificate... trustedCertificates) {
        if (trustedCertificates == null || trustedCertificates.length == 0) {
            return this;
        }

        X509Certificate[] newTrustedCAs = ObjectArrays.concat(trustedCAs, trustedCertificates, X509Certificate.class);

        return new TrustSource(newTrustedCAs);
    }

    /**
     * Returns a new TrustSource containing the same trusted CAs as this TrustSource, plus all trusted certificate
     * entries from the specified trustStore. This method will only add trusted certificate entries from the specified
     * KeyStore (i.e. entries of type {@link java.security.KeyStore.TrustedCertificateEntry}; private keys will be
     * ignored. The trustStore may be in JKS or PKCS12 format.
     *
     * @param trustStore keystore containing trusted certificate entries
     * @return a new TrustSource containing this TrustSource's trusted CAs plus trusted certificate entries from the keystore
     */
    public TrustSource add(KeyStore trustStore) {
        if (trustStore == null) {
            throw new IllegalArgumentException("Trust store cannot be null");
        }

        List<X509Certificate> trustedCertificates = TrustUtil.extractTrustedCertificateEntries(trustStore);

        return add(trustedCertificates.toArray(new X509Certificate[0]));
    }

    /**
     * Returns a new TrustSource containing the same trusted CAs as this TrustSource, plus zero or more CAs contained in
     * the PEM-encoded File. The File may contain multiple certificates and may contain comments or other non-PEM-encoded
     * text, as long as the PEM-encoded certificates are delimited by appropriate BEGIN_CERTIFICATE and END_CERTIFICATE
     * text blocks. The file may contain UTF-8 characters, but the PEM-encoded certificate data itself must be US-ASCII.
     *
     * @param trustedCAPemFile File containing PEM-encoded certificates
     * @return a new TrustSource containing this TrustSource's trusted CAs plus the CAs in the specified String
     */
    public TrustSource add(File trustedCAPemFile) {
        if (trustedCAPemFile == null) {
            throw new IllegalArgumentException("Trusted CA file cannot be null");
        }

        String pemFileContents;
        try {
            pemFileContents = Files.asCharSource(trustedCAPemFile, StandardCharsets.UTF_8).read();
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to read file containing PEM-encoded trusted CAs: " + trustedCAPemFile.getAbsolutePath(), e);
        }

        return add(pemFileContents);
    }

    /**
     * Returns a new TrustSource containing the same trusted CAs as this TrustSource, plus the trusted CAs in the specified
     * TrustSource.
     *
     * @param trustSource TrustSource to combine with this TrustSource
     * @return a new TrustSource containing both TrustSources' trusted CAs
     */
    public TrustSource add(TrustSource trustSource) {
        if (trustSource == null) {
            throw new IllegalArgumentException("TrustSource cannot be null");
        }

        return add(trustSource.getTrustedCAs());
    }
}
