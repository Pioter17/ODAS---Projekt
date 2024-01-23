package com.example.demo.other;

import com.google.common.io.BaseEncoding;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Random;

public class SecretEncryption {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5PADDING";
    private static final String SECRET_KEY="43tlkhHGadsgTtAGYy546aea2452GRHA09aaf2FvbbHNGFXa2144";

    public static String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, getKey(SECRET_KEY));
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, getKey(SECRET_KEY));
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes);
    }

    private static SecretKey getKey(String password) throws Exception{
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), "1234".getBytes(), 65536, 256);
        return new SecretKeySpec(keyFactory.generateSecret(keySpec).getEncoded(), ALGORITHM);
    }
    public static String buildSecret(String userName, String password) {
        String usernameSubstring = generateRandomSubstring(userName);
        String passwordSubstring = generateRandomSubstring(password);
        String salt = generateRandomSalt();

        return BaseEncoding.base32().encode((usernameSubstring+passwordSubstring+salt).getBytes());
    }

    private static String generateRandomSubstring(String input) {
        Random random = new SecureRandom();
        int length = input.length();
        int startIndex = random.nextInt(length);
        int endIndex = random.nextInt(length - startIndex) + startIndex + 1;
        return input.substring(startIndex, endIndex);
    }

    private static String generateRandomSalt() {
        int saltLength = 64;
        StringBuilder salt = new StringBuilder();
        Random random = new SecureRandom();

        for (int i = 0; i < saltLength; i++) {
            char randomChar = (char) (random.nextInt(26) + 'a');
            salt.append(randomChar);
        }

        return salt.toString();
    }
}
