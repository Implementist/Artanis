package com.implementist.artanis;

import javax.crypto.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

/**
 *
 * @author Implementist
 */
public class DesUtil {

    private static Key key;
    private static final String SECRET_KEY = "IMplementist";

    static {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
            keyGenerator.init(new SecureRandom(SECRET_KEY.getBytes()));
            key = keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String encode(String plainText) {
        Encoder encoder = Base64.getEncoder();
        try {
            byte[] plainTextBytes = plainText.getBytes(StandardCharsets.UTF_8);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] cipherTextByte = cipher.doFinal(plainTextBytes);
            return encoder.encodeToString(cipherTextByte);
        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public String decode(String cipherText) {
        Decoder decoder = Base64.getDecoder();
        try {
            byte[] cipherTextByte = decoder.decode(cipherText);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] plainTextByte = cipher.doFinal(cipherTextByte);
            return new String(plainTextByte, StandardCharsets.UTF_8);
        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    }
}
