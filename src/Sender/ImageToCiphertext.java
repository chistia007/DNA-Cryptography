package Sender;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageToCiphertext {
   private static String key="";
   public static void main(String[] args) {

        // key matrix generation first
        String matrixDNASequence = RandomDnaMatrixGeneration. generateRandomDNASequence(256);
       System.out.println("matrixDNASequence: "+ matrixDNASequence);
        String[][] keyMatrix = RandomDnaMatrixGeneration.convertToMatrix(matrixDNASequence);

       //Print the resulting matrix
        System.out.println("8x8 Matrix:");
        for (String[] row : keyMatrix) {
            for (String element : row) {
                System.out.print(element + " ");
            }
            System.out.println();
        }




        // image to binary conversion part
        // Specify the path to your image file
        String imagePath = "x-ray.jpg";

        try {
            // Read the image into a BufferedImage
            BufferedImage image = ImageIO.read(new File(imagePath));

            // Convert the BufferedImage to a binary string
            String binaryString = convertImageToBinaryString(image);
            try (FileWriter writer = new FileWriter("binary.txt")) {
                writer.write(binaryString);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // convert to the regular dna cipher and write it to Dna_Cipher.txt
            String dnaCipherText= getDNACipherText(binaryString);
            try (FileWriter writer = new FileWriter("DNA_Cipher.txt")) {
                writer.write(dnaCipherText);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // now using dna compliment rule no. 4 convert it to the dna cipher text and write it to Dna_compliment_cipher.txt
            String dnaComplimentRule4 = DNAComplimentRule4(dnaCipherText);
            try (FileWriter writer = new FileWriter(" Dna_compliment_cipher.txt")) {
                writer.write(dnaComplimentRule4);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // covert the dnaComplimentRule4 into 64 bit binary bit block and if the last block is not 64 bit then add padding (add X for Padding)
            //padding
            // and write it to padded_DNA_cipher.txt
            String paddedDNASequence = convertTo64BitBinaryBlock(dnaComplimentRule4);
            try (FileWriter writer = new FileWriter("paddedDNACompliment_cipher.txt")) {
                writer.write(paddedDNASequence);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // TODO : to select to colums from keyMatrix and forming the 64 bit key, then XOR with the paddedDNASequence
            // select the 2 columns from keyMatrix. select 0th and 4th index column if first bit of paddedDNASequence is A
            // select the 1st and 5th index column if first bit of paddedDNASequence is C
            // select the 2nd and 6th index column if first bit of paddedDNASequence is T
            // select the 3rd and 7th index column if first bit of paddedDNASequence is G
            // do coding now

            String firstBit= String.valueOf(paddedDNASequence.charAt(0));
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
            
            // now we have to do XOR to the dnaCipherText
            String XORConvertedString = DNASequencetoXORConverstion(paddedDNASequence);
            try (FileWriter writer = new FileWriter("XOR_cipher.txt")) {
                writer.write(XORConvertedString);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // add matrixDNASequence to the XORConvertedString; concat them
            String finalCipherText= matrixDNASequence+XORConvertedString;
            try (FileWriter writer = new FileWriter("Final_XOR_cipher.txt")) {
                writer.write(finalCipherText);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String convertTo64BitBinaryBlock(String dnaComplimentRule4) {
        int len1 = dnaComplimentRule4.length()  /  64;
        int len2= dnaComplimentRule4.length()  - (64*len1);

        if (len2 != 0) {
            int padding = 64 - len2;
            for (int i = 0; i < padding; i++) {
                dnaComplimentRule4 = dnaComplimentRule4 + "X";
            }
        }

        return dnaComplimentRule4;


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
                    // Handle other characters if necessary
                    break;
            }
        }

        return dnaComplement.toString();
    }

    public static String convertImageToBinaryString(BufferedImage image) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            // Write the image data to a byte array output stream
            ImageIO.write(image, "jpg", byteArrayOutputStream);

            // Convert the byte array to a binary string
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            StringBuilder binaryString = new StringBuilder();
            for (byte b : byteArray) {
                for (int i = 7; i >= 0; i--) {
                    binaryString.append((b >> i) & 1);
                }
            }
            return binaryString.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    public static String getDNACipherText(String binarySequence) {

        // here if it is not even number we need  send true in  binaryToDNA function
        // then the loop in binaryToDNA will run upto length-1 and if the cut binary is 0 we will concat Y, if 1 then Z
       boolean a=false;
        if (binarySequence.length() % 2 != 0) {
            a=true;
        }

        // Perform the conversion
        String dnaSequence = binaryToDNA(binarySequence,a);
        return  dnaSequence;
    }

    public static String binaryToDNA(String binarySequence, boolean isOdd) {
       //  loop  will run upto length-1 and if the cut binary is 0 we will concat Y, if 1 then Z
        int len=0;
        if(isOdd){
            len=binarySequence.length()-1;
        }
        else {
            len=binarySequence.length();
        }
        StringBuilder dnaSequence = new StringBuilder();

        for (int i = 0; i < binarySequence.length(); i += 2) {
            String check=  String.valueOf(binarySequence.charAt(i)) +String.valueOf(binarySequence.charAt(i+1));
            if(check.equals("00")){
                dnaSequence.append("G");
            }
            else if(check.equals("11")){
                dnaSequence.append("C");
            }
            else if(check.equals("01")){
                dnaSequence.append("T");
            }
            else if(check.equals("10")){
                dnaSequence.append("A");
            }
        }
        if(isOdd){
            if(binarySequence.charAt(binarySequence.length()-1)=='0'){
                dnaSequence.append("Y");
            }
            else {
                dnaSequence.append("Z");
            }
        }

        return dnaSequence.toString();
    }


    private static String DNASequencetoXORConverstion(String dnaCipherText) {
        // Define the XOR table
        char[][] xorTable = {
                {'C', 'T', 'A', 'G'},
                {'T', 'C', 'G', 'A'},
                {'A', 'G', 'C', 'T'},
                {'G', 'A', 'T', 'C'}
        };

        // Iterate over the dnaCipherText
        StringBuilder result = new StringBuilder();
        int j=0;
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

            if (check.equals("Y") || check.equals("Z") || check.equals("X")){
                result.append(base1);

            }
            else{
                // Perform XOR using the table
                try{char xorResult = xorTable[getDNAIndex(base1)][getDNAIndex(base2)];
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
                return  -1;
        }
    }


}
