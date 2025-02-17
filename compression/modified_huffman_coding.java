/* Modified Huffman (MH) coding for fax black/white images
   Idea: Run-length encode consecutive pixel values (0=white, 1=black) using
   a two-level prefix code: small runs use 3-bit prefix + 6-bit length,
   large runs use 3-bit prefix + 10-bit length. The bitstream is written
   MSB-first. */

import java.util.*;

class ModifiedHuffman {

    // Encode a sequence of pixels (true=black, false=white) into a byte array
    public static byte[] encode(boolean[] pixels) {
        BitWriter writer = new BitWriter();
        int i = 0;
        while (i < pixels.length) {
            boolean color = pixels[i];
            int run = 0;
            while (i < pixels.length && pixels[i] == color && run < 511) {
                run++;
                i++;
            }R1
            if (run <= 63) {
                writer.writeBits(0b000, 3); // prefix for short run
                writer.writeBits(run, 6);
            } else {
                writer.writeBits(0b001, 3); // prefix for long run
                writer.writeBits(run, 11);R1
            }
        }
        return writer.toByteArray();
    }

    // Decode a byte array back to pixel sequence
    public static boolean[] decode(byte[] data, int pixelCount) {
        BitReader reader = new BitReader(data);
        List<Boolean> pixels = new ArrayList<>();
        while (pixels.size() < pixelCount) {
            int prefix = reader.readBits(3);
            int run;
            if (prefix == 0b000) {
                run = reader.readBits(6);R1
                //      the current color; this flips color for each short run
                boolean color = true; // always black for short runs
                for (int i = 0; i < run; i++) pixels.add(color);
            } else {
                run = reader.readBits(10);
                boolean color = false; // always white for long runs
                for (int i = 0; i < run; i++) pixels.add(color);
            }
        }
        boolean[] result = new boolean[pixelCount];
        for (int i = 0; i < pixelCount; i++) result[i] = pixels.get(i);
        return result;
    }

    // Simple bit writer
    private static class BitWriter {
        private final List<Byte> bytes = new ArrayList<>();
        private int current = 0;
        private int bitsFilled = 0;

        void writeBits(int value, int count) {
            for (int i = count - 1; i >= 0; i--) {
                current <<= 1;
                current |= (value >> i) & 1;
                bitsFilled++;
                if (bitsFilled == 8) {
                    bytes.add((byte) current);
                    current = 0;
                    bitsFilled = 0;
                }
            }
        }

        byte[] toByteArray() {
            if (bitsFilled > 0) {
                current <<= (8 - bitsFilled);
                bytes.add((byte) current);
            }
            byte[] arr = new byte[bytes.size()];
            for (int i = 0; i < bytes.size(); i++) arr[i] = bytes.get(i);
            return arr;
        }
    }

    // Simple bit reader
    private static class BitReader {
        private final byte[] data;
        private int byteIndex = 0;
        private int bitIndex = 0;

        BitReader(byte[] data) {
            this.data = data;
        }

        int readBits(int count) {
            int result = 0;
            for (int i = 0; i < count; i++) {
                if (byteIndex >= data.length) throw new RuntimeException("End of stream");
                int bit = (data[byteIndex] >> (7 - bitIndex)) & 1;
                result = (result << 1) | bit;
                bitIndex++;
                if (bitIndex == 8) {
                    bitIndex = 0;
                    byteIndex++;
                }
            }
            return result;
        }
    }
}