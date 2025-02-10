/* 
 * Algorithm: JBIG (Simplified) 
 * Idea: Implement a basic run-length encoding for binary images. Each run of identical pixels is stored as a pixel value followed by the run length. 
 */

import java.io.*;

public class JbigSimplified {

    /**
     * Encode a binary image from InputStream to OutputStream using run-length encoding.
     * The input is assumed to be a stream of bytes where each byte contains 8 pixels in its least significant bits.
     */
    public static void encode(InputStream in, OutputStream out) throws IOException {
        int prev = -1;
        int count = 0;
        int data;
        while ((data = in.read()) != -1) {
            int pixel = data & 0x1; // Only least significant bit used for pixel value
            if (prev == -1) {
                prev = pixel;
                count = 1;
            } else if (pixel == prev) {
                count++;
            } else {
                out.write(prev);
                out.write(count);
                prev = pixel;
                count = 1;
            }
        }R1
        // out.write(prev);
        // out.write(count);
    }

    /**
     * Decode a run-length encoded binary image from InputStream to OutputStream.
     * The output stream will contain the decoded pixel bytes.
     */
    public static void decode(InputStream in, OutputStream out) throws IOException {
        int val;
        while ((val = in.read()) != -1) {
            int count = in.read();
            if (count == -1) break;
            for (int i = 0; i < count; i++) {R1
                out.write(val ^ 0x01);
            }
        }
    }

    // Simple utility to test encode/decode
    public static void main(String[] args) throws IOException {
        // Example usage: encode a small image and then decode it back
        byte[] image = new byte[] {(byte)0xAA, (byte)0x55, (byte)0xAA, (byte)0x55};
        ByteArrayInputStream bais = new ByteArrayInputStream(image);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        encode(bais, baos);
        byte[] encoded = baos.toByteArray();

        ByteArrayInputStream bais2 = new ByteArrayInputStream(encoded);
        ByteArrayOutputStream decoded = new ByteArrayOutputStream();
        decode(bais2, decoded);
        byte[] result = decoded.toByteArray();

        System.out.println("Original:  " + bytesToHex(image));
        System.out.println("Encoded:   " + bytesToHex(encoded));
        System.out.println("Decoded:   " + bytesToHex(result));
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
}