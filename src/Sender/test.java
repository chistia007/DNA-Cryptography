package Sender;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class test {
    public static void main(String[] args) throws Exception {
        //ECB
            // Generate an AES key
//            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//            keyGenerator.init(128); // You can use 128, 192, or 256 bits
//            SecretKey secretKey = keyGenerator.generateKey();
//
//          System.out.println("key : "+secretKey);
//
//            // Create a cipher instance and set it to encrypt mode
//            Cipher cipher = Cipher.getInstance("AES");
//            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
//
//            String plaintext = "This is a secret message";
//            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());
//
//            // Convert the encrypted bytes to a Base64-encoded string for easy transmission
//            String encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);
//
//            System.out.println("Encrypted Text: " + encryptedText);
//
//            // Decrypt the message
//            cipher.init(Cipher.DECRYPT_MODE, secretKey);
//            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
//            String decryptedText = new String(decryptedBytes);
//
//            System.out.println("Decrypted Text: " + decryptedText);




        //CBC
        // Generate an AES key
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128); // You can use 128, 192, or 256 bits
        SecretKey secretKey = keyGenerator.generateKey();

        // Create an initialization vector (IV)
        SecureRandom secureRandom = new SecureRandom();
        byte[] ivBytes = new byte[16]; // IV size is 16 bytes for AES
        secureRandom.nextBytes(ivBytes);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        // Create a cipher instance and set it to encrypt mode with CBC
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

        String plaintext = "This is a secret message";
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());

        // Convert the IV and encrypted bytes to Base64-encoded strings for easy transmission
        String ivBase64 = Base64.getEncoder().encodeToString(ivBytes);
        String encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);

        System.out.println("IV: " + ivBase64);
        System.out.println("Encrypted Text: " + encryptedText);

        // Decrypt the message
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        String decryptedText = new String(decryptedBytes);

        System.out.println("Decrypted Text: " + decryptedText);
        }
}
