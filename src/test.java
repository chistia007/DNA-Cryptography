
import java.nio.ByteBuffer;
import java.util.Arrays;
public class test {

    public static void main(String[] args) {
        // Base64-encoded text
        String base64Text = "ujnzu7BmT3RNLyqedlk5o66H";

        // Decode base64 to binary data
        byte[] binaryData = java.util.Base64.getDecoder().decode(base64Text);

        // Unpack binary data into a list of signed 8-bit integers
        ByteBuffer byteBuffer = ByteBuffer.wrap(binaryData);
        byte[] pixelArray = new byte[binaryData.length];
        byteBuffer.get(pixelArray);

        System.out.println(Arrays.toString(pixelArray));
    }
}
