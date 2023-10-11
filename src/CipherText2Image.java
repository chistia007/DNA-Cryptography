import Sender.RandomDnaMatrixGeneration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class CipherText2Image {
   public static String binaryString;
    private static String key="";
     private static String cipherText;
    public static void main(String[] args) {
        try {
            // Read the final XOR cipher text from XOR_cipher.txt
            String xorCipherText = readXORCipherText();

            //seprate the key from the cipher text
            String matrixDNASequence  = xorCipherText.substring(0, 256);

             cipherText = xorCipherText.substring(256);

            //generate key matrix
            String [][] keyMatrix = RandomDnaMatrixGeneration.convertToMatrix(matrixDNASequence);

            // generate the key by observing the oth index of cipherText
            String firstBit= String.valueOf(cipherText.charAt(0));
            int idx1= 0;
            int idx2=0;
            switch (firstBit){
                case "A" :
                    idx1=0;
                    idx2=4;
                    break;
                case "C" :
                    idx1=1;
                    idx2=5;
                    break;
                case "T" :
                    idx1=2;
                    idx2=6;
                    break;
                case "G" :
                    idx1=3;
                    idx2=7;
                    break;
            }


            //selecting column wise , but generating key row wise
            for (int i=0; i<8; i++){
                for (int j=0; j<8; j++){
                    if (j==idx1 || j==idx2){
                        key=key+keyMatrix[i][j];
                    }
                }

            }

            System.out.println("key:   "+ key);

            // Convert the XOR cipher text back to the original DNA sequence
            // but before that remove all paddings
            cipherText= cipherText.replace("X","");
            // now do XOR operation, if you find Y and Z then ignore that [do not run XOR operation on that]. also ignore the first 64 bits
            String dnaSequence = XORtoDNA(cipherText);

            //do dna compliment rule 4 on the dna sequence except ""Y" and "Z"
            String regularDnaSeq= DNAComplimentRule4(dnaSequence);
            System.out.println("DNA sequence: "+ regularDnaSeq);

            // Convert the DNA sequence back to binary
            // do not forget the conversion of Y=0 and Z=1
            binaryString = DNAtoBinary(regularDnaSeq);
            System.out.println(binaryString);

        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            // Convert the binary string to a byte array
            byte[] byteArray = convertBinaryStringToByteArray(binaryString);

            // Check if the byte array is null or empty
            if (byteArray == null || byteArray.length == 0) {
                System.out.println("The byte array is null or empty.");
                return;
            }

            // Create a ByteArrayInputStream from the byte array
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);

            // Read the byte array into a BufferedImage
            BufferedImage image = ImageIO.read(byteArrayInputStream);

            // Check if the image is null
            if (image == null) {
                System.out.println("The BufferedImage is null.");
                return;
            }

            // Save the BufferedImage to a file (optional)
            File outputFile = new File("output.jpg");
            ImageIO.write(image, "jpg", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }


        }

    private static String readXORCipherText() throws IOException {
        StringBuilder xorCipherText = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader("Final_XOR_cipher.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                xorCipherText.append(line);
            }
        }
        return xorCipherText.toString();
    }

    private static String XORtoDNA(String dnaCipherText) {
        // Define the XOR table
        char[][] xorTable = {
                {'C', 'T', 'A', 'G'},
                {'T', 'C', 'G', 'A'},
                {'A', 'G', 'C', 'T'},
                {'G', 'A', 'T', 'C'}
        };

        StringBuilder result = new StringBuilder();
        int j=0;
        // Iterate over the dnaCipherText
        for (int i = 0; i < dnaCipherText.length(); i += 1) {
            if (j==key.length()){
                j=0;
            }

            if(i>=0 && i<=63){
                result.append(dnaCipherText.charAt(i));
                continue;
            }

            // Get the current pair of DNA bases
            char base1 = dnaCipherText.charAt(i);
            char base2 = key.charAt(j);

            String check= String.valueOf(base1);

            if (check.equals("Y") || check.equals("Z") ){
                result.append(base1);
            }
            else{
                // Perform XOR using the table
                try{
                    char xorResult = xorTable[getDNAIndex(base1)][getDNAIndex(base2)];
                    // Append the XOR result to the result string
                    result.append(xorResult);}
                catch (Exception e){
                    System.out.println("base1: "+ base1+check);
                }
            }
        }

        return result.toString();
    }

    // Helper method to get the index of a DNA base in the table
    private static int getDNAIndex(char base) {
        switch (base) {
            case 'C':
                return 0;
            case 'T':
                return 1;
            case 'A':
                return 2;
            case 'G':
                return 3;
            default:
                throw new IllegalArgumentException("Invalid DNA base: " + base);
        }
    }

    private static String DNAtoBinary(String dnaSequence) {
        StringBuilder binaryString = new StringBuilder();

        for (int i = 0; i < dnaSequence.length(); i++) {
            char base = dnaSequence.charAt(i);
            switch (base) {
                case 'C':
                    binaryString.append("11");
                    break;
                case 'G':
                    binaryString.append("00");
                    break;
                case 'T':
                    binaryString.append("01");
                    break;
                case 'A':
                    binaryString.append("10");
                    break;
                case 'Y':
                    binaryString.append("0");
                    break;
                case 'Z':
                    binaryString.append("1");
                    break;
            }
        }

        return binaryString.toString();
    }

    private static String DNAComplimentRule4(String dnaCipherText) {
        StringBuilder dnaComplement = new StringBuilder();

        for (char c : dnaCipherText.toCharArray()) {
            switch (c) {
                case 'A':
                    dnaComplement.append('T');
                    break;
                case 'T':
                    dnaComplement.append('A');
                    break;
                case 'G':
                    dnaComplement.append('C');
                    break;
                case 'C':
                    dnaComplement.append('G');
                    break;
                default:
                    dnaComplement.append(c);
                    break;
            }
        }

        return dnaComplement.toString();
    }



    public static String readBinaryStringFromFile(String filePath) {
        StringBuilder binaryString = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                binaryString.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return binaryString.toString();
    }

    public static byte[] convertBinaryStringToByteArray(String binaryString) {
        int len = binaryString.length();
        int paddedLen = (len + 7) / 8 * 8; // Round up to the nearest multiple of 8
        byte[] byteArray = new byte[paddedLen / 8];

        // Pad the binary string with zeros
        while (binaryString.length() < paddedLen) {
            binaryString = "0" + binaryString;
        }

        for (int i = 0; i < paddedLen; i += 8) {
            String byteString = binaryString.substring(i, i + 8);
            byte b = (byte) Integer.parseInt(byteString, 2);
            byteArray[i / 8] = b;
        }

        return byteArray;
    }

}