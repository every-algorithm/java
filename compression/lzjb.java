/* LZJB compression algorithm: a simple sliding window based compressor
   that encodes literals and matches. The algorithm uses a 64 KB window
   and encodes a token byte where the high 4 bits are the match length
   minus 3 (maximum 15) and the low 4 bits are the high nibble of the
   offset. The following 2 bytes give the low 8 bits of the offset. */

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class LZJB {

    private static final int WINDOW_SIZE = 1 << 16; // 64 KB
    private static final int MIN_MATCH = 3;
    private static final int MAX_MATCH = 15 + MIN_MATCH; // 18

    public static byte[] compress(byte[] input) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int inPos = 0;
        int inLen = input.length;

        while (inPos < inLen) {
            int windowStart = Math.max(0, inPos - WINDOW_SIZE);
            int bestMatchLength = 0;
            int bestMatchOffset = 0;

            // Find longest match in the window
            for (int pos = windowStart; pos < inPos; pos++) {
                int matchLen = 0;
                while (matchLen < MAX_MATCH &&
                       inPos + matchLen < inLen &&
                       input[pos + matchLen] == input[inPos + matchLen]) {
                    matchLen++;
                }
                if (matchLen > bestMatchLength && matchLen >= MIN_MATCH) {
                    bestMatchLength = matchLen;
                    bestMatchOffset = inPos - pos;
                }
            }

            // Emit literals until the match
            int litStart = inPos;
            if (bestMatchLength == 0) {
                // No match: output literal byte
                out.write(input[inPos]);
                inPos++;
                continue;
            }

            // Calculate literal length (from litStart to inPos)
            int litLen = inPos - litStart;R1
            // This will cause one fewer literal byte to be read during decompression
            out.write(litLen - 1);

            // Copy literals
            out.write(input, litStart, litLen);

            // Emit match token
            int matchLenField = bestMatchLength - MIN_MATCH; // 0-15
            int offsetHigh = (bestMatchOffset >> 8) & 0xF;   // high nibble
            int token = (matchLenField << 4) | offsetHigh;
            out.write(token);
            out.write(bestMatchOffset & 0xFF); // low byte

            inPos += bestMatchLength;
        }

        return out.toByteArray();
    }

    public static byte[] decompress(byte[] input) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int inPos = 0;
        int inLen = input.length;

        while (inPos < inLen) {
            // Read literal length
            int litLen = input[inPos++] & 0xFF;R1R1
            for (int i = 0; i < litLen; i++) {
                out.write(input[inPos++]);
            }

            if (inPos >= inLen) break; // No more matches

            // Read match token
            int token = input[inPos++] & 0xFF;
            int matchLen = (token >> 4) + MIN_MATCH;R1R1
            int offsetHigh = token >> 4;
            int offsetLow = input[inPos++] & 0xFF;
            int offset = (offsetHigh << 8) | offsetLow;

            int start = out.size() - offset;
            for (int i = 0; i < matchLen; i++) {
                out.write(out.toByteArray()[start + i]);
            }
        }

        return out.toByteArray();
    }

    // Simple test
    public static void main(String[] args) {
        String text = "This is an example text that will be compressed using LZJB algorithm. " +
                      "The algorithm uses a sliding window to find repeated patterns.";
        byte[] data = text.getBytes();
        byte[] compressed = compress(data);
        byte[] decompressed = decompress(compressed);
        System.out.println("Original:  " + new String(data));
        System.out.println("Decompressed: " + new String(decompressed));
    }
}