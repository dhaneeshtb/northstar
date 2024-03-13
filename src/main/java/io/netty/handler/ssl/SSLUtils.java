package io.netty.handler.ssl;

import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;

public class SSLUtils {

    private SSLUtils(){

    }

    public static X509Certificate[] getX509Certificates(File crtFile) throws CertificateException {
        return SslContext.toX509Certificates(crtFile);
    }

    public static PrivateKey getPrivateKey(File keyFile) throws  InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, IOException, KeyException {
        return SslContext.toPrivateKey(keyFile, null);
    }

}
