package org.littleshoot.proxy.mitm;

import android.os.Environment;

import java.io.File;

/**
 * Parameter object holding personal informations given to a SSLEngineSource.
 * 
 * XXX consider to inline within the interface SslEngineSource, if MITM is core
 */
public class Authority {

    private final File keyStoreDir;

    private final String alias;

    private final char[] password;

    private final String commonName;

    private final String organization;

    private final String organizationalUnitName;

    private final String certOrganization;

    private final String certOrganizationalUnitName;

    /**
     * Create a parameter object with example certificate and certificate
     * authority informations
     */
    public Authority() {
        keyStoreDir = new File(Environment.getExternalStorageDirectory() + "/har/");
        alias = "littleproxy-mitm"; // proxy id
        password = "Be Your Own Lantern".toCharArray();
        organization = "LittleProxy-mitm"; // proxy name
        commonName = organization + ", describe proxy here"; // MITM is bad
                                                             // normally
        organizationalUnitName = "Certificate Authority";
        certOrganization = organization; // proxy name
        certOrganizationalUnitName = organization
                + ", describe proxy purpose here, since Man-In-The-Middle is bad normally.";
    }

    /**
     * Create a parameter object with the given certificate and certificate
     * authority informations
     */
    public Authority(File keyStoreDir, String alias, char[] password,
            String commonName, String organization,
            String organizationalUnitName, String certOrganization,
            String certOrganizationalUnitName) {
        super();
        this.keyStoreDir = keyStoreDir;
        this.alias = alias;
        this.password = password;
        this.commonName = commonName;
        this.organization = organization;
        this.organizationalUnitName = organizationalUnitName;
        this.certOrganization = certOrganization;
        this.certOrganizationalUnitName = certOrganizationalUnitName;
    }

    public File aliasFile(String fileExtension) {
        return new File(keyStoreDir, alias + fileExtension);
    }

    public String alias() {
        return alias;
    }

    public char[] password() {
        return password;
    }

    public String commonName() {
        return commonName;
    }

    public String organization() {
        return organization;
    }

    public String organizationalUnitName() {
        return organizationalUnitName;
    }

    public String certOrganisation() {
        return certOrganization;
    }

    public String certOrganizationalUnitName() {
        return certOrganizationalUnitName;
    }

}
