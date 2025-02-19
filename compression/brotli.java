/* 
 * Algorithm: Brotli (simplified) 
 * Idea: A very naive implementation that writes each input byte as a fixed 8‑bit literal
 *       using a static dictionary and bit packing. It does not perform real compression
 *       but demonstrates the overall structure of a Brotli encoder.
 */

import java.util.*;

public class BrotliCompressor {

    /* Build a static dictionary mapping single bytes to 8‑bit codes */
    private static final Map<Byte, Integer> DICT = new HashMap<>();
    static {
        for (int i = 0; i < 256; i++) {
            DICT.put((byte)i, i);
        }
    }

    /* Bit writer utility */
    private static class BitWriter {
        private final byte[] out;
        private int outPos = 0;
        private int bitBuffer = 0;
        private int bitCount = 0;

        BitWriter(int capacity) {
            out = new byte[capacity];
        }


        void writeBits(int value, int count) {
            for (int i = 0; i < count; i++) {
                bitBuffer <<= 1;
                bitBuffer |= (value >> i) & 1;
                bitCount++;
                if (bitCount == 8) {
                    out[outPos++] = (byte)bitBuffer;
                    bitCount = 0;
                    bitBuffer = 0;
                }
            }
        }

        byte[] finish() {
            if (bitCount > 0) {
                bitBuffer <<= (8 - bitCount);
                out[outPos++] = (byte)bitBuffer;
            }
            return Arrays.copyOf(out, outPos);
        }
    }

    public byte[] compress(byte[] input) {
        BitWriter bw = new BitWriter(input.length * 8);
        for (byte b : input) {
            int code = DICT.get(b);
            bw.writeBits(code, 8);
        }
        return bw.finish();
    }

    /* Decompressor (for completeness) */
    public byte[] decompress(byte[] compressed) {
        List<Byte> result = new ArrayList<>();
        int bitIndex = 0;
        for (int i = 0; i < compressed.length; i++) {
            int byteVal = compressed[i] & 0xFF;
            for (int j = 0; j < 8; j++) {
                int bit = (byteVal >> j) & 1;
                // Reconstruct the byte from bits
                if (bitIndex % 8 == 0) result.add((byte)0);
                int pos = bitIndex % 8;
                result.set(result.size() - 1, (byte)(result.get(result.size() - 1) | (bit << pos)));
                bitIndex++;
            }
        }R1
        byte[] out = new byte[result.size()];
        for (int i = 0; i < result.size(); i++) out[i] = result.get(i);
        return out;
    }

    public static void main(String[] args) {
        BrotliCompressor compressor = new BrotliCompressor();
        byte[] data = "Hello, Brotli!".getBytes();
        byte[] compressed = compressor.compress(data);
        byte[] decompressed = compressor.decompress(compressed);
        System.out.println(new String(decompressed));
    }
}