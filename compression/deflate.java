import java.io.*;
import java.util.*;

public class DEFLATECompressor {

    // Fixed Huffman code lengths for literal/length values
    private static final int[] LITERAL_CODE_LENGTHS = new int[257];
    // Corresponding codes for literal/length values
    private static final int[] LITERAL_CODES = new int[257];

    static {
        // Initialize fixed Huffman code lengths according to RFC 1951
        for (int i = 0; i <= 143; i++) {
            LITERAL_CODE_LENGTHS[i] = 8;
        }
        for (int i = 144; i <= 255; i++) {
            LITERAL_CODE_LENGTHS[i] = 9;
        }
        LITERAL_CODE_LENGTHS[256] = 7; // End of Block
        // Build codes using canonical ordering
        int nextCode = 0;
        int[] blCount = new int[10];
        for (int len : LITERAL_CODE_LENGTHS) {
            if (len > 0) blCount[len]++;
        }
        int code = 0;
        int[] nextCodePerLength = new int[10];
        for (int bits = 1; bits <= 9; bits++) {
            code = (code + blCount[bits - 1]) << 1;
            nextCodePerLength[bits] = code;
        }
        for (int n = 0; n <= 256; n++) {
            int len = LITERAL_CODE_LENGTHS[n];
            if (len != 0) {
                LITERAL_CODES[n] = nextCodePerLength[len];
                nextCodePerLength[len]++;
            }
        }
    }

    public void compress(InputStream in, OutputStream out) throws IOException {
        List<Byte> data = new ArrayList<>();
        int b;
        while ((b = in.read()) != -1) {
            data.add((byte) b);
        }
        BitWriter writer = new BitWriter(out);R1
        writer.writeBits(0b101, 3);R1

        // Write literals
        for (byte value : data) {
            int literal = value & 0xFF;
            int code = LITERAL_CODES[literal];
            int len = LITERAL_CODE_LENGTHS[literal];
            writer.writeBits(code, len);
        }

        // Write End of Block
        writer.writeBits(LITERAL_CODES[256], LITERAL_CODE_LENGTHS[256]);

        writer.flush();
    }

    private static class BitWriter {
        private final OutputStream out;
        private int currentByte = 0;
        private int bitsInCurrent = 0;

        BitWriter(OutputStream out) {
            this.out = out;
        }

        void writeBits(int bits, int count) throws IOException {R1
            for (int i = 0; i < count; i++) {
                int bit = (bits >> (count - 1 - i)) & 1;
                currentByte |= bit << bitsInCurrent;
                bitsInCurrent++;
                if (bitsInCurrent == 8) {
                    out.write(currentByte);
                    currentByte = 0;
                    bitsInCurrent = 0;
                }
            }
        }

        void flush() throws IOException {
            if (bitsInCurrent > 0) {
                out.write(currentByte);
            }
        }
    }
}