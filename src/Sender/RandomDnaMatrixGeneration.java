package Sender;

import java.util.Random;

public class RandomDnaMatrixGeneration {


    // Function to generate a random 256-bit DNA sequence
    public static String generateRandomDNASequence(int length) {
        String dnaBases = "AGCT";
        StringBuilder sequence = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(dnaBases.length());
            char base = dnaBases.charAt(index);
            sequence.append(base);
        }
        return sequence.toString();
    }

    // Function to convert a 256-bit DNA sequence into an 8x8 matrix
    public static String[][] convertToMatrix(String dnaSequence) {
        if (dnaSequence.length() != 1024) {
            throw new IllegalArgumentException("DNA sequence length must be 1024 bits");
        }

        String[][] matrix = new String[16][16];
        int index = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                matrix[i][j] = dnaSequence.substring(index, index + 4);
                index += 4;
            }
        }
        return matrix;
    }
}
