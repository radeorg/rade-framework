package org.dows.rade;

import org.dows.rade.config.ConfigEncryptProperties;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AesEncryptor {
    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "3fb2uPksjNOnxZI3";

//    private final byte[] secretKey;
//    private final byte[] iv = new byte[16];
//
//    public AesEncryptor(byte[] secretKey) {
//        this.secretKey = secretKey;
//        System.arraycopy(secretKey, 0, iv, 0, 16);
//    }


    //    public String encrypt(String message) {
//        return Base64.getEncoder().encodeToString(_AesUtils.encrypt(secretKey, iv, message.getBytes()));
//    }
//
//
//    public String decrypt(String message) {
//        return new String(_AesUtils.decrypt(secretKey, iv, Base64.getDecoder().decode(message)));
//    }
    private String decrypt(String encryptedValue, ConfigEncryptProperties configEncryptProperties) {
        try {
            String algorithm = configEncryptProperties.getAlgorithm();
            byte[] keys = configEncryptProperties.getSecretKey().getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(keys, algorithm);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedValue);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }

    // 新增加密方法
    public String encrypt(String plainText, ConfigEncryptProperties configEncryptProperties) {
        try {
            String algorithm = configEncryptProperties.getAlgorithm();
            byte[] keys = configEncryptProperties.getSecretKey().getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(keys, algorithm);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    // 测试加密功能的主方法
    public static void main(String[] args) {
        AesEncryptor processor = new AesEncryptor();
        ConfigEncryptProperties config = new ConfigEncryptProperties();
        config.setAlgorithm(ALGORITHM);
        config.setSecretKey(SECRET_KEY);

        //String plainText = "AKID8XyHOTZeYDmBKEDA5usojaUGzb2FDxSu";
        String plainText = "GKEyXu7A2kR8iIqFaiiXCePyMZbTAew1";
        String encryptedText = processor.encrypt(plainText, config);
        System.out.println("Encrypted Text: " + encryptedText);
        System.out.println(processor.decrypt(encryptedText, config));
    }

}
