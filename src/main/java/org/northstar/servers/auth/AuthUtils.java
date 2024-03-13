package org.northstar.servers.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthUtils {
    private AuthUtils(){}

    public static String onewayHash(String plainText) throws NoSuchAlgorithmException {
        StringBuilder salt=new StringBuilder();
        salt.append("MySuperSecretSalt");
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        salt.append(plainText);
        md.update(salt.toString().getBytes(StandardCharsets.UTF_8)); // Change this to "UTF-16" if needed
        byte[] digest = md.digest();
        return hex(digest);
    }
    public static String hex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
