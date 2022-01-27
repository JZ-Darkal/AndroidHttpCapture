package org.littleshoot.proxy.mitm;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyManagementException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Random;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.bc.BcX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CertificateHelper {

    private static final Logger log = LoggerFactory.getLogger(CertificateHelper.class);

    public static final String PROVIDER_NAME = BouncyCastleProvider.PROVIDER_NAME;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final String KEYGEN_ALGORITHM = "RSA";

    private static final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";

    /**
     * The signature algorithm starting with the message digest to use when
     * signing certificates. On 64-bit systems this should be set to SHA512, on
     * 32-bit systems this is SHA256. On 64-bit systems, SHA512 generally
     * performs better than SHA256; see this question for details:
     * http://crypto.stackexchange.com/questions/26336/sha512-faster-than-sha256
     */
    private static final String SIGNATURE_ALGORITHM = (is32BitJvm() ? "SHA256" : "SHA512") + "WithRSAEncryption";

    private static final int ROOT_KEYSIZE = 2048;

    private static final int FAKE_KEYSIZE = 1024;

    /** The milliseconds of a day */
    private static final long ONE_DAY = 86400000L;

    /**
     * Current time minus 1 year, just in case software clock goes back due to
     * time synchronization
     */
    private static final Date NOT_BEFORE = new Date(System.currentTimeMillis() - ONE_DAY * 365);

    /**
     * The maximum possible value in X.509 specification: 9999-12-31 23:59:59,
     * new Date(253402300799000L), but Apple iOS 8 fails with a certificate
     * expiration date grater than Mon, 24 Jan 6084 02:07:59 GMT (issue #6).
     * 
     * Hundred years in the future from starting the proxy should be enough.
     */
    private static final Date NOT_AFTER = new Date(System.currentTimeMillis() + ONE_DAY * 365 * 100);

    /**
     * Enforce TLS 1.2 if available, since it's not default up to Java 8.
     * <p>
     * Java 7 disables TLS 1.1 and 1.2 for clients. From <a href=
     * "http://docs.oracle.com/javase/7/docs/technotes/guides/security/SunProviders.html"
     * >Java Cryptography Architecture Oracle Providers Documentation:</a>
     * Although SunJSSE in the Java SE 7 release supports TLS 1.1 and TLS 1.2,
     * neither version is enabled by default for client connections. Some
     * servers do not implement forward compatibility correctly and refuse to
     * talk to TLS 1.1 or TLS 1.2 clients. For interoperability, SunJSSE does
     * not enable TLS 1.1 or TLS 1.2 by default for client connections.
     */
    private static final String SSL_CONTEXT_PROTOCOL = "TLSv1.2";
    /**
     * {@link SSLContext}: Every implementation of the Java platform is required
     * to support the following standard SSLContext protocol: TLSv1
     */
    private static final String SSL_CONTEXT_FALLBACK_PROTOCOL = "TLSv1";

    private CertificateHelper() {}

    public static KeyPair generateKeyPair(int keySize)
            throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator generator = KeyPairGenerator
                .getInstance(KEYGEN_ALGORITHM/* , PROVIDER_NAME */);
        SecureRandom secureRandom = SecureRandom
                .getInstance(SECURE_RANDOM_ALGORITHM/* , PROVIDER_NAME */);
        generator.initialize(keySize, secureRandom);
        return generator.generateKeyPair();
    }

    /**
     * Uses the non-portable system property sun.arch.data.model to help
     * determine if we are running on a 32-bit JVM. Since the majority of modern
     * systems are 64 bits, this method "assumes" 64 bits and only returns true
     * if sun.arch.data.model explicitly indicates a 32-bit JVM.
     *
     * @return true if we can determine definitively that this is a 32-bit JVM,
     *         otherwise false
     */
    private static boolean is32BitJvm() {
        Integer bits = Integer.getInteger("sun.arch.data.model");
        return bits != null && bits == 32;
    }

    public static KeyStore createRootCertificate(Authority authority,
            String keyStoreType) throws NoSuchAlgorithmException,
            NoSuchProviderException, IOException,
            OperatorCreationException, CertificateException, KeyStoreException {

        KeyPair keyPair = generateKeyPair(ROOT_KEYSIZE);

        X500NameBuilder nameBuilder = new X500NameBuilder(BCStyle.INSTANCE);
        nameBuilder.addRDN(BCStyle.CN, authority.commonName());
        nameBuilder.addRDN(BCStyle.O, authority.organization());
        nameBuilder.addRDN(BCStyle.OU, authority.organizationalUnitName());

        X500Name issuer = nameBuilder.build();
        BigInteger serial = BigInteger.valueOf(initRandomSerial());
        X500Name subject = issuer;
        PublicKey pubKey = keyPair.getPublic();

        X509v3CertificateBuilder generator = new JcaX509v3CertificateBuilder(
                issuer, serial, NOT_BEFORE, NOT_AFTER, subject, pubKey);

        generator.addExtension(Extension.subjectKeyIdentifier, false,
                createSubjectKeyIdentifier(pubKey));
        generator.addExtension(Extension.basicConstraints, true,
                new BasicConstraints(true));

        KeyUsage usage = new KeyUsage(KeyUsage.keyCertSign
                | KeyUsage.digitalSignature | KeyUsage.keyEncipherment
                | KeyUsage.dataEncipherment | KeyUsage.cRLSign);
        generator.addExtension(Extension.keyUsage, false, usage);

        ASN1EncodableVector purposes = new ASN1EncodableVector();
        purposes.add(KeyPurposeId.id_kp_serverAuth);
        purposes.add(KeyPurposeId.id_kp_clientAuth);
        purposes.add(KeyPurposeId.anyExtendedKeyUsage);
        generator.addExtension(Extension.extendedKeyUsage, false,
                new DERSequence(purposes));

        X509Certificate cert = signCertificate(generator, keyPair.getPrivate());

        KeyStore result = KeyStore
                .getInstance(keyStoreType/* , PROVIDER_NAME */);
        result.load(null, null);
        result.setKeyEntry(authority.alias(), keyPair.getPrivate(),
                authority.password(), new Certificate[] { cert });
        return result;
    }

    private static SubjectKeyIdentifier createSubjectKeyIdentifier(Key key)
            throws IOException {
        ByteArrayInputStream bIn = new ByteArrayInputStream(key.getEncoded());
        ASN1InputStream is = null;
        try {
            is = new ASN1InputStream(bIn);
            ASN1Sequence seq = (ASN1Sequence) is.readObject();
            SubjectPublicKeyInfo info = new SubjectPublicKeyInfo(seq);
            return new BcX509ExtensionUtils().createSubjectKeyIdentifier(info);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    public static KeyStore createServerCertificate(String commonName,
            SubjectAlternativeNameHolder subjectAlternativeNames,
            Authority authority, Certificate caCert, PrivateKey caPrivKey)
            throws NoSuchAlgorithmException, NoSuchProviderException,
            IOException, OperatorCreationException, CertificateException,
            InvalidKeyException, SignatureException, KeyStoreException {

        KeyPair keyPair = generateKeyPair(FAKE_KEYSIZE);

        X500Name issuer = new X509CertificateHolder(caCert.getEncoded())
                .getSubject();
        BigInteger serial = BigInteger.valueOf(initRandomSerial());

        X500NameBuilder name = new X500NameBuilder(BCStyle.INSTANCE);
        name.addRDN(BCStyle.CN, commonName);
        name.addRDN(BCStyle.O, authority.certOrganisation());
        name.addRDN(BCStyle.OU, authority.certOrganizationalUnitName());
        X500Name subject = name.build();

        X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(issuer, serial, NOT_BEFORE,
                new Date(System.currentTimeMillis() + ONE_DAY), subject, keyPair.getPublic());

        builder.addExtension(Extension.subjectKeyIdentifier, false,
                createSubjectKeyIdentifier(keyPair.getPublic()));
        builder.addExtension(Extension.basicConstraints, false,
                new BasicConstraints(false));

        subjectAlternativeNames.fillInto(builder);

        X509Certificate cert = signCertificate(builder, caPrivKey);

        cert.checkValidity(new Date());
        cert.verify(caCert.getPublicKey());

        KeyStore result = KeyStore.getInstance(KeyStore.getDefaultType()
        /* , PROVIDER_NAME */);
        result.load(null, null);
        Certificate[] chain = { cert, caCert };
        result.setKeyEntry(authority.alias(), keyPair.getPrivate(),
                authority.password(), chain);

        return result;
    }

    private static X509Certificate signCertificate(
            X509v3CertificateBuilder certificateBuilder,
            PrivateKey signedWithPrivateKey) throws OperatorCreationException,
            CertificateException {
        ContentSigner signer = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM).build(signedWithPrivateKey);
        return new JcaX509CertificateConverter().getCertificate(certificateBuilder.build(signer));
    }

    public static TrustManager[] getTrustManagers(KeyStore keyStore)
            throws KeyStoreException, NoSuchAlgorithmException,
            NoSuchProviderException {
        String trustManAlg = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(trustManAlg
        /* , PROVIDER_NAME */);
        tmf.init(keyStore);
        return tmf.getTrustManagers();
    }

    public static KeyManager[] getKeyManagers(KeyStore keyStore,
            Authority authority) throws NoSuchAlgorithmException,
            NoSuchProviderException, UnrecoverableKeyException,
            KeyStoreException {
        String keyManAlg = KeyManagerFactory.getDefaultAlgorithm();
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(keyManAlg
        /* , PROVIDER_NAME */);
        kmf.init(keyStore, authority.password());
        return kmf.getKeyManagers();
    }

    public static SSLContext newClientContext(KeyManager[] keyManagers,
            TrustManager[] trustManagers) throws NoSuchAlgorithmException,
            KeyManagementException, NoSuchProviderException {
        SSLContext result = newSSLContext();
        result.init(keyManagers, trustManagers, null);
        return result;
    }

    public static SSLContext newServerContext(KeyManager[] keyManagers)
            throws NoSuchAlgorithmException, NoSuchProviderException,
            KeyManagementException {
        SSLContext result = newSSLContext();
        SecureRandom random = new SecureRandom();
        random.setSeed(System.currentTimeMillis());
        result.init(keyManagers, null, random);
        return result;
    }

    private static SSLContext newSSLContext() throws NoSuchAlgorithmException {
        try {
            log.debug("Using protocol {}", SSL_CONTEXT_PROTOCOL);
            return SSLContext.getInstance(SSL_CONTEXT_PROTOCOL
            /* , PROVIDER_NAME */);
        } catch (NoSuchAlgorithmException e) {
            log.warn("Protocol {} not available, falling back to {}", SSL_CONTEXT_PROTOCOL,
                    SSL_CONTEXT_FALLBACK_PROTOCOL);
            return SSLContext.getInstance(SSL_CONTEXT_FALLBACK_PROTOCOL
            /* , PROVIDER_NAME */);
        }
    }

    public static long initRandomSerial() {
        final Random rnd = new Random();
        rnd.setSeed(System.currentTimeMillis());
        // prevent browser certificate caches, cause of doubled serial numbers
        // using 48bit random number
        long sl = ((long) rnd.nextInt()) << 32 | (rnd.nextInt() & 0xFFFFFFFFL);
        // let reserve of 16 bit for increasing, serials have to be positive
        sl = sl & 0x0000FFFFFFFFFFFFL;
        return sl;
    }

}
