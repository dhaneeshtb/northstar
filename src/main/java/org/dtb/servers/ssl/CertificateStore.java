package org.dtb.servers.ssl;
import io.netty.handler.ssl.SSLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

public class CertificateStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificateStore.class);


    public static class CertificateInfo {
        X509Certificate[] keyCertChain;
        public X509Certificate[] getKeyCertChain() {
            return keyCertChain;
        }
        public void setKeyCertChain(X509Certificate[] keyCertChain) {
            this.keyCertChain = keyCertChain;
        }
        public PrivateKey getKey() {
            return key;
        }

        public void setKey(PrivateKey key) {
            this.key = key;
        }
        PrivateKey key;
        CertificateInfo(X509Certificate[] keyCertChain, PrivateKey key) {
            this.keyCertChain = keyCertChain;
            this.key = key;
        }
    }
    private static Map<String, CertificateInfo> certificateMap = new HashMap<>();

    static{
        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );
        loadAllCerts();
    }
    public static CertificateInfo get(String alias) {
        if (certificateMap.containsKey(alias)) {
            return certificateMap.get(alias);
        }
        load(alias);
        return certificateMap.get(alias);
    }

    private static void loadAllCerts(){
        try {
            CertificateInfo  cinfo = loadFromPath(System.getProperty("certPath","certs"));
            certificateMap.put("default",cinfo);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

    }

    public static void main(String[] args) {
        loadAllCerts();
    }

    public static void load(String alias) {
		CertificateInfo info = loadFromPath("certs/"+alias);
        certificateMap.put(alias,info );

	}

    public static CertificateInfo loadFromPath(String path) {
        File crtFile = new File(path+"/server.crt");
        File keyFile = new File(path+"/private.key");
        if(!crtFile.exists()) {
            LOGGER.info("Fallback to default path....{}",path);
            crtFile = new File("certs/server.crt");
            keyFile = new File("certs/private.key");
        }
        X509Certificate[] keyCertChain = null;
        try {
            keyCertChain = SSLUtils.getX509Certificates(crtFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PrivateKey key = null;
        try {
            key = SSLUtils.getPrivateKey(keyFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new CertificateInfo(keyCertChain, key);

    }
}