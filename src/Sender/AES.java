package Sender;

import Receiver.CipherText2Image;
import ResultTeseting.GCM;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

public class AES {
    private GCMParameterSpec gcmParameterSpec;
    private SecretKey secretKey;
    private StringBuilder sb = new StringBuilder();



    public String GCMEncryption(String plainText) throws Exception {
        // Generate an AES key
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        secretKey = keyGenerator.generateKey();

        // Create an initialization vector (IV)
        SecureRandom secureRandom = new SecureRandom();
        byte[] ivBytes = new byte[12];
        secureRandom.nextBytes(ivBytes);
        gcmParameterSpec = new GCMParameterSpec(128, ivBytes);


        // Create a cipher instance and set it to encrypt mode with ResultTeseting.GCM
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        System.out.println("Secret key: " + Base64.getEncoder().encodeToString(secretKey.getEncoded()));
        System.out.println("IV110: " + Arrays.toString(ivBytes));
        System.out.println("IV22: " + Base64.getEncoder().encodeToString(gcmParameterSpec.getIV()));
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);

        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        //print this encryptedBytes
       // System.out.println("Encrypted Text: " + Arrays.toString(encryptedBytes));

        // Convert the IV and encrypted bytes to Base64-encoded strings for easy transmission
        String ivBase64 = Base64.getEncoder().encodeToString(ivBytes);
        String encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);

        System.out.println("IV: " + ivBase64);

        saveSecretKey("secretKey.txt");
        saveGCMParameterSpec("gcmParameterSpec.txt", ivBytes);

        //decrypt
//        Cipher cipher1 = Cipher.getInstance("AES/ResultTeseting.GCM/NoPadding");
//        cipher1.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);
//        byte[] decryptedBytes = cipher1.doFinal(Base64.getDecoder().decode(encryptedText));
//        String decryptedText = new String(decryptedBytes);
//        System.out.println("Decrypted Text: " + decryptedText);
//
//        try (FileWriter writer = new FileWriter("decryptedgcm.txt")) {
//            writer.write(decryptedText);
//        }

        return encryptedText;
    }

    private void saveSecretKey(String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            writer.write(encodedKey);
            System.out.println("Saved Secret key: " + encodedKey);
        }
    }


    private void saveGCMParameterSpec(String filename, byte[] ivBytes) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            String ivBase64 = Base64.getEncoder().encodeToString(ivBytes);
            writer.write("IV: " + ivBase64 + "\n");  // Add a newline character
            System.out.println("Saved IV: " + ivBase64);
        }
    }




    public SecretKey loadSecretKey(String filename) throws IOException, NoSuchAlgorithmException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String encodedKey = reader.readLine();
            // Decode the string directly without using StringBuilder
            byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
            // Rebuild the key using SecretKeySpec
            secretKey = new SecretKeySpec(decodedKey, "AES");
            System.out.println("Loaded Secret key: " + Base64.getEncoder().encodeToString(secretKey.getEncoded()));
        }
        return secretKey;
    }

    public GCMParameterSpec loadGCMParameterSpec(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            StringBuilder ivBase64 = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("IV: ")) {
                    // Skip "IV: " prefix and trim whitespace
                    ivBase64.append(line.substring(4).trim());
                }
            }

            byte[] ivBytes = Base64.getDecoder().decode(ivBase64.toString());
            System.out.println("Loaded IV: " + Base64.getEncoder().encodeToString(ivBytes));
            gcmParameterSpec = new GCMParameterSpec(128, ivBytes);
        }
        return gcmParameterSpec;
    }



}
