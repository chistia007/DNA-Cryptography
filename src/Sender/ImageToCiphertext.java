package Sender;

import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class ImageToCiphertext {
   private static String key="";
   private static StringBuilder st;
    private static String dnaComplimentRule4;

    public static SecretKey secretKey;
    public static GCMParameterSpec gcmParameterSpec;
   public static void main(String[] args) {
       long startTime = System.currentTimeMillis();

       encrption();

       long endTime = System.currentTimeMillis();
       long duration = endTime - startTime;

       System.out.println("Time taken: " + duration + " miliseconds");

    }


    public static void encrption(){
        // key matrix generation first
        String matrixDNASequence = RandomDnaMatrixGeneration. generateRandomDNASequence(1024);
        String[][] keyMatrix = RandomDnaMatrixGeneration.convertToMatrix(matrixDNASequence);





        // image to binary conversion part
        // Specify the path to your image file
        String imagePath = "brain-scan.jpg";

        try {
            // Read the image into a BufferedImage
            BufferedImage image = ImageIO.read(new File(imagePath));

            // Convert the BufferedImage to a binary string
            String binaryString = convertImageToBinaryString(image);
            try (FileWriter writer = new FileWriter("binary1.txt")) {
                writer.write(binaryString);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // convert binary to the regular dna cipher and write it to Dna_Cipher.txt
            String dnaCipherText= getDNACipherText(binaryString);
            try (FileWriter writer = new FileWriter("DNA_Cipher.txt")) {
                writer.write(dnaCipherText);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // now using dna compliment rule no. 4 convert it to the dna cipher text and write it to Dna_compliment_cipher.txt
            dnaComplimentRule4 = DNAComplimentRule4(dnaCipherText);
            try (FileWriter writer = new FileWriter(" Dna_compliment_cipher.txt")) {
                writer.write(dnaComplimentRule4);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // covert the dnaComplimentRule4 into 256 bit binary bit block and if the last block is not 256 bit then add padding (add X for Padding)
            //padding
            // and write it to padded_DNA_cipher.txt
            String paddedDNASequence = convertTo256BitBinaryBlock(dnaComplimentRule4);
            try (FileWriter writer = new FileWriter("paddedDNACompliment_cipher.txt")) {
                writer.write(paddedDNASequence);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // TODO : to select to colums from keyMatrix and forming the 64 bit key, then XOR with the paddedDNASequence
            // select the 4 columns from keyMatrix. select 0th and 4th index column if first bit of paddedDNASequence is A
            // select the 1st and 5th index column if first bit of paddedDNASequence is C
            // select the 2nd and 6th index column if first bit of paddedDNASequence is T
            // select the 3rd and 7th index column if first bit of paddedDNASequence is G
            // do coding now

            String firstBit= String.valueOf(paddedDNASequence.charAt(0));
            int idx1= 0;
            int idx2=0;
            int idx3=0;
            int idx4=0;
            switch (firstBit){
                case "A" :
                    idx1=0;
                    idx2=4;
                    idx3=8;
                    idx4=9;
                    break;
                case "C" :
                    idx1=1;
                    idx2=5;
                    idx3=14;
                    idx4=15;
                    break;
                case "T" :
                    idx1=2;
                    idx2=6;
                    idx3=12;
                    idx4=13;
                    break;
                case "G" :
                    idx1=3;
                    idx2=7;
                    idx3=10;
                    idx4=11;
                    break;
            }


            //selecting column wise , but generating key row wise
            for (int i=0; i<16; i++){
                for (int j=0; j<16; j++){
                    if (j==idx1 || j==idx2 || j==idx3 || j==idx4){
                        key=key+keyMatrix[i][j];
                    }
                }

            }


            // now we have to do XOR to the dnaCipherText
            String XORConvertedString = DNASequencetoXORConverstion(paddedDNASequence);
            try (FileWriter writer = new FileWriter("XOR_cipher.txt")) {
                writer.write(XORConvertedString);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // add matrixDNASequence to the XORConvertedString; concat them
            String finalCipherText= matrixDNASequence+XORConvertedString;
            try (FileWriter writer = new FileWriter("decryptedDCM.txt")) {
                writer.write(finalCipherText);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String encryptedText= "";
            AES aes = new AES();
            try {
                 encryptedText = aes.CBCEncryption(finalCipherText);
            } catch (Exception e) {
                e.printStackTrace();
            }
            FileWriter writer = new FileWriter("Final_XOR_cipher.txt");
            writer.write(encryptedText);

            String encryptedBinary=convertTextToBinaryString(encryptedText);
            try (FileWriter writer1 = new FileWriter("encryptedImageBinary.txt")) {
                writer1.write(encryptedBinary);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //
            //
            //
            //encrypted image pixels getting starts from here
            //
            //
            //
            //new code

            // Decode base64 to binary data
            byte[] binaryData = java.util.Base64.getDecoder().decode(encryptedText);

            // Unpack binary data into a list of signed 8-bit integers
            ByteBuffer byteBuffer = ByteBuffer.wrap(binaryData);
            byte[] pixelArray = new byte[binaryData.length];
            byteBuffer.get(pixelArray);

           //write pixel array to a txt file
            try (FileWriter writer1 = new FileWriter("encryptedImagePixels404.txt")) {
                writer1.write(Arrays.toString(pixelArray));
            } catch (IOException e) {
                e.printStackTrace();
            }



            //old code
//            st = new StringBuilder();
//
//            for (int i = 0; i < XORConvertedString.length(); i++) {
//                char base = XORConvertedString.charAt(i);
//                switch (base) {
//                    case 'C':
//                        st.append("11");
//                        break;
//                    case 'G':
//                        st.append("00");
//                        break;
//                    case 'T':
//                        st.append("01");
//                        break;
//                    case 'A':
//                        st.append("10");
//                        break;
//                    default:
//
//                }
//            }
//            try {
//                String rs = st.toString();
//                //System.out.println("resultString: "+ rs);
//                byte[] byteArray1 = convertBinaryStringToByteArray(rs);
//
//                // Check if the byte array is null or empty
//                if (byteArray1 == null || byteArray1.length == 0) {
//                    System.out.println("The byte array is null or empty.");
//                    return;
//                }
//
//                // Create a ByteArrayInputStream from the byte array
//                ByteArrayInputStream byteArrayInputStream1 = new ByteArrayInputStream(byteArray1);
//
//                System.out.println("encrypted image bytestream array: "+ Arrays.toString(byteArrayInputStream1.readAllBytes()));
//
//                // Read the byte array into a BufferedImage
//                BufferedImage image1 = ImageIO.read(byteArrayInputStream1);
//                System.out.println("image1: "+ image1);
//
////                 Check if the image is null
//                if (image1 == null) {
//                    System.out.println("The BufferedImage is null.");
//                    return;
//                }
//
//                // Save the BufferedImage to a file (optional)
//                File outputFile = new File("encryptedImage.jpg");
//                ImageIO.write(image1, "jpg", outputFile);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


            //ends here


        } catch (IOException e) {
            e.printStackTrace();
        }

    }




//
//
//
// All methods start from here
//
//
    private static String convertTo256BitBinaryBlock(String dnaComplimentRule4) {
        int len1 = dnaComplimentRule4.length()  /  256;
        int len2= dnaComplimentRule4.length()  - (256*len1);

        if (len2 != 0) {
            int padding = 256 - len2;
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
           // System.out.println("inputted Images byte array: "+ Arrays.toString(byteArray));
            //write it as inputtedimagepixel.txt
            try (FileWriter writer = new FileWriter("inputtedImagePixel.txt")) {
                writer.write(Arrays.toString(byteArray));
            } catch (IOException e) {
                e.printStackTrace();
            }
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


    public static String convertTextToBinaryString(String text) {
        byte[] byteArray = text.getBytes();
       // System.out.println("inputted Images byte array1: "+ Arrays.toString(byteArray));
        StringBuilder binaryString = new StringBuilder();

        for (byte b : byteArray) {
            for (int i = 7; i >= 0; i--) {
                binaryString.append((b >> i) & 1);
            }
        }

        return binaryString.toString();
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

            if(i>=0 && i<=255){
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



    //for getting encryptrd images
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

    public GCMParameterSpec getGcmParameterSpec() {
        return gcmParameterSpec;
    }
    public SecretKey getSecretKey() {
        return secretKey;
    }


}
