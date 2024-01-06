package Sender;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

public class AES {
    private SecretKey secretKey;
    private IvParameterSpec ivParameterSpec;


    public String CBCEncryption(String plainText) throws Exception {
        // Generate an AES key
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        secretKey = keyGenerator.generateKey();

        // Generate an initialization vector (IV)
        SecureRandom secureRandom = new SecureRandom();
        byte[] ivBytes = new byte[16];
        secureRandom.nextBytes(ivBytes);
        ivParameterSpec = new IvParameterSpec(ivBytes);
        // Create a cipher instance and set it to encrypt mode with AES-CBC
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        String encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);
        saveSecretKey("secretKey.txt");
        saveIV("IVParameterSpec.txt", ivBytes);
        return encryptedText;
    }
    private void saveSecretKey(String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            writer.write(encodedKey);
            System.out.println("Saved Secret key: " + encodedKey);
        }
    }
    private void saveIV(String filename, byte[] ivBytes) throws IOException {
        if (ivBytes.length != 16) {
            throw new IllegalArgumentException("Invalid IV length: " + ivBytes.length + " bytes. Expected 16 bytes.");
        }
        try (FileWriter writer = new FileWriter(filename)) {
            String ivBase64 = Base64.getEncoder().encodeToString(ivBytes);
            writer.write("IV: " + ivBase64 + "\n");
        }
    }
    public SecretKey loadSecretKey(String filename) throws IOException, NoSuchAlgorithmException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String encodedKey = reader.readLine();
            byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
            secretKey = new SecretKeySpec(decodedKey, "AES");
        }
        return secretKey;
    }
    public IvParameterSpec loadIV(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            StringBuilder ivBase64 = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("IV: ")) {
                    ivBase64.append(line.substring(4).trim());
                }
            }
            byte[] ivBytes = Base64.getDecoder().decode(ivBase64.toString());
            // Ensure the loaded IV has the correct length
            if (ivBytes.length != 16) {
                throw new IllegalArgumentException("Invalid IV length: " + ivBytes.length + " bytes. Expected 16 bytes.");
            }
            ivParameterSpec = new IvParameterSpec(ivBytes);
        }
        return ivParameterSpec;
    }
    public String CBCDecryption(String encryptedText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes);
    }
}
