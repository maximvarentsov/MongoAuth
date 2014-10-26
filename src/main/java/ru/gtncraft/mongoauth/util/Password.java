package ru.gtncraft.mongoauth.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Password {
    public static String encrypt(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder encrypted = new StringBuilder();
            for (byte aHash : hash) {
                String hex = Integer.toHexString(0xff & aHash);
                if (hex.length() == 1) {
                    encrypted.append('0');
                }
                encrypted.append(hex);
            }
            return encrypted.toString();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ignore) {
            return null;
        }
    }
}
