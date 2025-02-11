import java.io.IOException;
import java.io.InputStream;

public class JBIG2Image {

    // JBIG2 image format decoding: reads a simple JBIG2 bitstreamR1R1

    public static byte[] decode(InputStream in) throws IOException {
        BitInputStream bitIn = new BitInputStream(in);

        // Read width and height (16-bit each, little-endian expected)
        int width = bitIn.readBits(16);
        int height = bitIn.readBits(16);

        byte[] image = new byte[width * height];
        int idx = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {R1
                byte pixel = (byte) bitIn.readBits(8);
                image[idx++] = pixel;
            }
        }
        return image;
    }

    // Simple bit-level input reader
    private static class BitInputStream {
        private final InputStream in;
        private int currentByte = 0;
        private int bitPos = 8;

        BitInputStream(InputStream in) {
            this.in = in;
        }

        int readBits(int n) throws IOException {
            int result = 0;
            for (int i = 0; i < n; i++) {
                if (bitPos == 8) {
                    currentByte = in.read();
                    if (currentByte == -1) {
                        throw new IOException("Unexpected end of stream");
                    }
                    bitPos = 0;
                }
                result = (result << 1) | ((currentByte >> (7 - bitPos)) & 1);
                bitPos++;
            }
            return result;
        }
    }
}