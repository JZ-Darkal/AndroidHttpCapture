package net.lightbody.bmp.mitm.tools;

import com.google.common.net.InetAddresses;
import net.lightbody.bmp.mitm.CertificateAndKey;
import net.lightbody.bmp.mitm.CertificateInfo;
import net.lightbody.bmp.mitm.exception.CertificateCreationException;
import net.lightbody.bmp.mitm.exception.ExportException;
import net.lightbody.bmp.mitm.exception.ImportException;
import net.lightbody.bmp.mitm.util.EncryptionUtil;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.bc.BcX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMEncryptor;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.openssl.jcajce.JcePEMEncryptorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import javax.net.ssl.KeyManager;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class BouncyCastleSecurityProviderTool implements SecurityProviderTool {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * The size of certificate serial numbers, in bits.
     */
    private static final int CERTIFICATE_SERIAL_NUMBER_SIZE = 160;

    @Override
    public CertificateAndKey createServerCertificate(CertificateInfo certificateInfo,
                                                     X509Certificate caRootCertificate,
                                                     PrivateKey caPrivateKey,
                                                     KeyPair serverKeyPair,
                                                     String messageDigest) {
        // make sure certificateInfo contains all fields necessary to generate the certificate
        if (certificateInfo.getCommonName() == null) {
            throw new IllegalArgumentException("Must specify CN for server certificate");
        }

        if (certificateInfo.getNotBefore() == null) {
            throw new IllegalArgumentException("Must specify Not Before for server certificate");
        }

        if (certificateInfo.getNotAfter() == null) {
            throw new IllegalArgumentException("Must specify Not After for server certificate");
        }

        // create the subject for the new server certificate. when impersonating an upstream server, this should contain
        // the hostname of the server we are trying to impersonate in the CN field
        X500Name serverCertificateSubject = createX500NameForCertificate(certificateInfo);

        // get the algorithm that will be used to sign the new certificate, which is a combination of the message digest
        // and the digital signature from the CA's private key
        String signatureAlgorithm = EncryptionUtil.getSignatureAlgorithm(messageDigest, caPrivateKey);

        // get a ContentSigner with our CA private key that will be used to sign the new server certificate
        ContentSigner signer = getCertificateSigner(caPrivateKey, signatureAlgorithm);

        // generate a serial number for the new certificate. serial numbers only need to be unique within our
        // certification authority; a large random integer will satisfy that requirement.
        BigInteger serialNumber = EncryptionUtil.getRandomBigInteger(CERTIFICATE_SERIAL_NUMBER_SIZE);

        // create the X509Certificate using Bouncy Castle. the BC X509CertificateHolder can be converted to a JCA X509Certificate.
        X509CertificateHolder certificateHolder;
        try {
            certificateHolder = new JcaX509v3CertificateBuilder(caRootCertificate,
                    serialNumber,
                    certificateInfo.getNotBefore(),
                    certificateInfo.getNotAfter(),
                    serverCertificateSubject,
                    serverKeyPair.getPublic())
                    .addExtension(Extension.subjectAlternativeName, false, getDomainNameSANsAsASN1Encodable(certificateInfo.getSubjectAlternativeNames()))
                    .addExtension(Extension.subjectKeyIdentifier, false, createSubjectKeyIdentifier(serverKeyPair.getPublic()))
                    .addExtension(Extension.basicConstraints, false, new BasicConstraints(false))
                    .build(signer);
        } catch (CertIOException e) {
            throw new CertificateCreationException("Error creating new server certificate", e);
        }

        // convert the Bouncy Castle certificate holder into a JCA X509Certificate
        X509Certificate serverCertificate = convertToJcaCertificate(certificateHolder);

        return new CertificateAndKey(serverCertificate, serverKeyPair.getPrivate());
    }

    @Override
    public KeyStore createServerKeyStore(String keyStoreType, CertificateAndKey serverCertificateAndKey, X509Certificate rootCertificate, String privateKeyAlias, String password) {
        throw new UnsupportedOperationException("BouncyCastle implementation does not implement this method");
    }

    @Override
    public KeyStore createRootCertificateKeyStore(String keyStoreType, CertificateAndKey rootCertificateAndKey, String privateKeyAlias, String password) {
        throw new UnsupportedOperationException("BouncyCastle implementation does not implement this method");
    }

    @Override
    public CertificateAndKey createCARootCertificate(CertificateInfo certificateInfo,
                                                     KeyPair keyPair,
                                                     String messageDigest) {
        if (certificateInfo.getNotBefore() == null) {
            throw new IllegalArgumentException("Must specify Not Before for server certificate");
        }

        if (certificateInfo.getNotAfter() == null) {
            throw new IllegalArgumentException("Must specify Not After for server certificate");
        }

        // create the X500Name that will be both the issuer and the subject of the new root certificate
        X500Name issuer = createX500NameForCertificate(certificateInfo);

        BigInteger serial = EncryptionUtil.getRandomBigInteger(CERTIFICATE_SERIAL_NUMBER_SIZE);

        PublicKey rootCertificatePublicKey = keyPair.getPublic();

        String signatureAlgorithm = EncryptionUtil.getSignatureAlgorithm(messageDigest, keyPair.getPrivate());

        // this is a CA root certificate, so it is self-signed
        ContentSigner selfSigner = getCertificateSigner(keyPair.getPrivate(), signatureAlgorithm);

        ASN1EncodableVector extendedKeyUsages = new ASN1EncodableVector();
        extendedKeyUsages.add(KeyPurposeId.id_kp_serverAuth);
        extendedKeyUsages.add(KeyPurposeId.id_kp_clientAuth);
        extendedKeyUsages.add(KeyPurposeId.anyExtendedKeyUsage);

        X509CertificateHolder certificateHolder;
        try {
            certificateHolder = new JcaX509v3CertificateBuilder(
                    issuer,
                    serial,
                    certificateInfo.getNotBefore(),
                    certificateInfo.getNotAfter(),
                    issuer,
                    rootCertificatePublicKey)
                    .addExtension(Extension.subjectKeyIdentifier, false, createSubjectKeyIdentifier(rootCertificatePublicKey))
                    .addExtension(Extension.basicConstraints, true, new BasicConstraints(true))
                    .addExtension(Extension.keyUsage, false, new KeyUsage(
                            KeyUsage.keyCertSign
                                    | KeyUsage.digitalSignature
                                    | KeyUsage.keyEncipherment
                                    | KeyUsage.dataEncipherment
                                    | KeyUsage.cRLSign))
                    .addExtension(Extension.extendedKeyUsage, false, new DERSequence(extendedKeyUsages))
                    .build(selfSigner);
        } catch (CertIOException e) {
            throw new CertificateCreationException("Error creating root certificate", e);
        }

        // convert the Bouncy Castle X590CertificateHolder to a JCA cert
        X509Certificate cert = convertToJcaCertificate(certificateHolder);

        return new CertificateAndKey(cert, keyPair.getPrivate());
    }

    @Override
    public String encodePrivateKeyAsPem(PrivateKey privateKey, String passwordForPrivateKey, String encryptionAlgorithm) {
        if (passwordForPrivateKey == null) {
            throw new IllegalArgumentException("You must specify a password when serializing a private key");
        }

        PEMEncryptor encryptor = new JcePEMEncryptorBuilder(encryptionAlgorithm)
                .build(passwordForPrivateKey.toCharArray());

        return encodeObjectAsPemString(privateKey, encryptor);
    }

    @Override
    public String encodeCertificateAsPem(Certificate certificate) {
        return encodeObjectAsPemString(certificate, null);
    }

    @Override
    public PrivateKey decodePemEncodedPrivateKey(Reader privateKeyReader, String password) {
        try (PEMParser pemParser = new PEMParser(privateKeyReader)) {
            Object keyPair = pemParser.readObject();

            // retrieve the PrivateKeyInfo from the returned keyPair object. if the key is encrypted, it needs to be
            // decrypted using the specified password first.
            PrivateKeyInfo keyInfo;
            if (keyPair instanceof PEMEncryptedKeyPair) {
                if (password == null) {
                    throw new ImportException("Unable to import private key. Key is encrypted, but no password was provided.");
                }

                PEMDecryptorProvider decryptor = new JcePEMDecryptorProviderBuilder().build(password.toCharArray());

                PEMKeyPair decryptedKeyPair = ((PEMEncryptedKeyPair) keyPair).decryptKeyPair(decryptor);

                keyInfo = decryptedKeyPair.getPrivateKeyInfo();
            } else {
                keyInfo = ((PEMKeyPair) keyPair).getPrivateKeyInfo();
            }

            return new JcaPEMKeyConverter().getPrivateKey(keyInfo);
        } catch (IOException e) {
            throw new ImportException("Unable to read PEM-encoded PrivateKey", e);
        }
    }

    @Override
    public X509Certificate decodePemEncodedCertificate(Reader certificateReader) {
        // JCA provides this functionality already, but it can be easily implemented using BC as well
        throw new UnsupportedOperationException("BouncyCastle implementation does not implement this method");
    }

    @Override
    public KeyStore loadKeyStore(File file, String keyStoreType, String password) {
        throw new UnsupportedOperationException("BouncyCastle implementation does not implement this method");
    }

    @Override
    public void saveKeyStore(File file, KeyStore keyStore, String keystorePassword) {
        throw new UnsupportedOperationException("BouncyCastle implementation does not implement this method");
    }

    @Override
    public KeyManager[] getKeyManagers(KeyStore keyStore, String keyStorePassword) {
        return new KeyManager[0];
    }


    /**
     * Creates an X500Name based on the specified certificateInfo.
     *
     * @param certificateInfo information to populate the X500Name with
     * @return a new X500Name object for use as a subject or issuer
     */
    private static X500Name createX500NameForCertificate(CertificateInfo certificateInfo) {
        X500NameBuilder x500NameBuilder = new X500NameBuilder(BCStyle.INSTANCE);

        if (certificateInfo.getCommonName() != null) {
            x500NameBuilder.addRDN(BCStyle.CN, certificateInfo.getCommonName());
        }

        if (certificateInfo.getOrganization() != null) {
            x500NameBuilder.addRDN(BCStyle.O, certificateInfo.getOrganization());
        }

        if (certificateInfo.getOrganizationalUnit() != null) {
            x500NameBuilder.addRDN(BCStyle.OU, certificateInfo.getOrganizationalUnit());
        }

        if (certificateInfo.getEmail() != null) {
            x500NameBuilder.addRDN(BCStyle.E, certificateInfo.getEmail());
        }

        if (certificateInfo.getLocality() != null) {
            x500NameBuilder.addRDN(BCStyle.L, certificateInfo.getLocality());
        }

        if (certificateInfo.getState() != null) {
            x500NameBuilder.addRDN(BCStyle.ST, certificateInfo.getState());
        }

        if (certificateInfo.getCountryCode() != null) {
            x500NameBuilder.addRDN(BCStyle.C, certificateInfo.getCountryCode());
        }

        // TODO: Add more X.509 certificate fields as needed

        return x500NameBuilder.build();
    }

    /**
     * Converts a list of domain name Subject Alternative Names into ASN1Encodable GeneralNames objects, for use with
     * the Bouncy Castle certificate builder.
     *
     * @param subjectAlternativeNames domain name SANs to convert
     * @return a GeneralNames instance that includes the specifie dsubjectAlternativeNames as DNS name fields
     */
    private static GeneralNames getDomainNameSANsAsASN1Encodable(List<String> subjectAlternativeNames) {
        List<GeneralName> encodedSANs = new ArrayList<>(subjectAlternativeNames.size());
        for (String subjectAlternativeName : subjectAlternativeNames) {
            // IP addresses use the IP Address tag instead of the DNS Name tag in the SAN list
            boolean isIpAddress = InetAddresses.isInetAddress(subjectAlternativeName);
            GeneralName generalName = new GeneralName(isIpAddress ? GeneralName.iPAddress : GeneralName.dNSName, subjectAlternativeName);
            encodedSANs.add(generalName);
        }

        return new GeneralNames(encodedSANs.toArray(new GeneralName[encodedSANs.size()]));
    }

    /**
     * Creates a ContentSigner that can be used to sign certificates with the given private key and signature algorithm.
     *
     * @param certAuthorityPrivateKey the private key to use to sign certificates
     * @param signatureAlgorithm      the algorithm to use to sign certificates
     * @return a ContentSigner
     */
    private static ContentSigner getCertificateSigner(PrivateKey certAuthorityPrivateKey, String signatureAlgorithm) {
        try {
            return new JcaContentSignerBuilder(signatureAlgorithm)
                    .build(certAuthorityPrivateKey);
        } catch (OperatorCreationException e) {
            throw new CertificateCreationException("Unable to create ContentSigner using signature algorithm: " + signatureAlgorithm, e);
        }
    }

    /**
     * Converts a Bouncy Castle X509CertificateHolder into a JCA X590Certificate.
     *
     * @param bouncyCastleCertificate BC X509CertificateHolder
     * @return JCA X509Certificate
     */
    private static X509Certificate convertToJcaCertificate(X509CertificateHolder bouncyCastleCertificate) {
        try {
            return new JcaX509CertificateConverter()
                    .getCertificate(bouncyCastleCertificate);
        } catch (CertificateException e) {
            throw new CertificateCreationException("Unable to convert X590CertificateHolder to JCA X590Certificate", e);
        }
    }

    /**
     * Creates the SubjectKeyIdentifier for a Bouncy Castle X590CertificateHolder.
     *
     * @param key public key to identify
     * @return SubjectKeyIdentifier for the specified key
     */
    private static SubjectKeyIdentifier createSubjectKeyIdentifier(Key key) {
        SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(key.getEncoded());

        return new BcX509ExtensionUtils().createSubjectKeyIdentifier(publicKeyInfo);
    }

    /**
     * Encodes the specified security object in PEM format, using the specified encryptor. If the encryptor is null,
     * the object will not be encrypted in the generated String.
     *
     * @param object    object to encrypt (certificate, private key, etc.)
     * @param encryptor engine to encrypt the resulting PEM String, or null if no encryption should be used
     * @return a PEM-encoded String
     */
    private static String encodeObjectAsPemString(Object object, PEMEncryptor encryptor) {
        StringWriter stringWriter = new StringWriter();

        try (JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter)) {
            pemWriter.writeObject(object, encryptor);
            pemWriter.flush();
        } catch (IOException e) {
            throw new ExportException("Unable to generate PEM string representing object", e);
        }

        return stringWriter.toString();
    }
}
