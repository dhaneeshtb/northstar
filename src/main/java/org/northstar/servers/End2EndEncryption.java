package org.northstar.servers;

import org.northstar.security.GenerateKeyPair;
import org.northstar.servers.exceptions.GenericServerProcessingException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class End2EndEncryption {

    private GenerateKeyPair.EncodedKeyPair encodedKeyPair;
    private GenerateKeyPair.DecodedKeyPair decodeDKeyPair;

    public GenerateKeyPair.EncodedKeyPair getEncodedKeyPair() {
        return encodedKeyPair;
    }

    public GenerateKeyPair.DecodedKeyPair getDecodeDKeyPair() {
        return decodeDKeyPair;
    }

    public End2EndEncryption(String privateKey, String publicKey) throws NoSuchAlgorithmException {
        this.encodedKeyPair = GenerateKeyPair.generateKeyPair(privateKey,publicKey);
        try {
            this.decodeDKeyPair= encodedKeyPair.getDecodedPair();
        } catch (Exception e) {
            throw new GenericServerProcessingException(e);
        }
    }

    public End2EndEncryption() throws NoSuchAlgorithmException, IOException {
        this.encodedKeyPair = GenerateKeyPair.generateKeyPair();
        try {
            this.decodeDKeyPair= encodedKeyPair.getDecodedPair();
        } catch (Exception e) {
            throw new GenericServerProcessingException(e);
        }
    }
}
