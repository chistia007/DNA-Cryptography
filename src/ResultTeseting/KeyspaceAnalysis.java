package ResultTeseting;

import Receiver.CipherText2Image;

import java.util.Random;

public class KeyspaceAnalysis {
    private static int count=0;
    public static void main(String[] args) {
        // Perform keyspace analysis for 8-bit image
        performKeyspaceAnalysis(256);

        // You can modify the key size according to your requirements
        // For example, for a 128-bit key, use performKeyspaceAnalysis(128);
        System.out.println("count of successful decryption using random key: "+count);
    }

    private static void performKeyspaceAnalysis(int keySize) {
        // Initialize CipherText2Image object
        CipherText2Image cipherText2Image = new CipherText2Image();

        // Read the original XOR cipher text
        String xorCipherText;
        try {
            xorCipherText = cipherText2Image.readXORCipherText();
        } catch (Exception e) {
            System.out.println("Error reading XOR cipher text.");
            return;
        }

        // Iterate through the keyspace
        for (int i = 0; i < Math.pow(4, keySize); i++) {
            // Generate a random key of the specified size
            String randomKey = generateRandomKey(keySize);

            // Pass the random key to the CipherText2Image class for decryption
            cipherText2Image.key = randomKey; // Set the key in CipherText2Image
            System.out.println("Key:1 " + cipherText2Image.key);
            System.out.println("Key:2 " +randomKey);
            try {
                cipherText2Image.decryption();
                System.out.println("Decrypted Text: " + cipherText2Image.getDecryptedBinaryString());
                System.out.println("Encrypted Text: " + xorCipherText);
            } catch (Exception e) {
                // Handle decryption errors if any
                System.out.println("Error during decryption with key: " + e.getMessage());
            }

            // Compare the decrypted result with the original XOR cipher text
            if (xorCipherText.equals(cipherText2Image.getDecryptedBinaryString())) {
                count+=1;
                System.out.println("Successful decryption with key: " + randomKey);
                break; // Exit the loop if successful decryption is found
            }
        }
    }

    private static String generateRandomKey(int keySize) {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        String dnaCharacters = "ACGT"; // DNA bases

        for (int i = 0; i < keySize; i++) {
            int index = random.nextInt(dnaCharacters.length());
            char dnaBase = dnaCharacters.charAt(index);
            key.append(dnaBase);
        }

        return key.toString();
    }
}
