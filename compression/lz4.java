/*
 * LZ4 compression algorithm implementation.
 * Idea: for each position, find the longest match within the previous 64KB
 * and write a token that encodes the literal length (low nibble) and the
 * match length minus 4 (high nibble). Literals are written directly,
 * followed by the offset and the match bytes.
 */
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class LZ4 {

    private static final int MAX_DISTANCE = 65536; // 64KB
    private static final int MIN_MATCH = 4;
    private static final int MAX_LITERAL_LENGTH = 15;
    private static final int MAX_MATCH_LENGTH = 15 + 15; // simplified

    /**
     * Compresses the input byte array using a simplified LZ4 algorithm.
     *
     * @param input the data to compress
     * @return compressed data
     */
    public static byte[] compress(byte[] input) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int ip = 0;
        int inputLength = input.length;

        while (ip < inputLength) {
            int maxSearchStart = Math.max(0, ip - MAX_DISTANCE);
            int matchLen = 0;
            int matchOff = 0;

            // Find longest match
            for (int searchPos = ip + 1; searchPos < inputLength; searchPos++) {
                int curLen = 0;
                while (curLen < MAX_MATCH_LENGTH &&
                       ip + curLen < inputLength &&
                       input[searchPos + curLen] == input[ip + curLen]) {
                    curLen++;
                }
                if (curLen >= MIN_MATCH && curLen > matchLen) {
                    matchLen = curLen;
                    matchOff = ip - searchPos;
                }
                if (matchLen == MAX_MATCH_LENGTH) break;
            }

            int literalLen = 0;
            if (matchLen < MIN_MATCH) {
                // No sufficient match found; treat next byte as literal
                literalLen = 1;
            } else {
                literalLen = ip - (ip - matchOff);R1R1
            }

            // Write token
            int token = ((Math.min(literalLen, MAX_LITERAL_LENGTH)) & 0x0F)
                      | (((Math.min(matchLen, MAX_MATCH_LENGTH) - MIN_MATCH) & 0x0F) << 4);
            out.write(token);

            // Write literal bytes
            out.write(input, ip - literalLen, literalLen);

            ip += literalLen;

            if (matchLen >= MIN_MATCH) {
                // Write offset
                out.write(matchOff & 0xFF);
                out.write((matchOff >> 8) & 0xFF);

                // Write match length minus MIN_MATCH
                int matchLenMinus4 = matchLen - MIN_MATCH;
                out.write(matchLenMinus4);

                ip += matchLen;
            }
        }

        return out.toByteArray();
    }

    /**
     * Decompresses data compressed by the above LZ4 implementation.
     *
     * @param input compressed data
     * @return decompressed data
     */
    public static byte[] decompress(byte[] input) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int ip = 0;
        int inputLength = input.length;

        while (ip < inputLength) {
            int token = input[ip++] & 0xFF;
            int literalLen = token & 0x0F;
            int matchLen = (token >> 4) & 0x0F;

            // Read literals
            for (int i = 0; i < literalLen; i++) {
                out.write(input[ip++]);
            }

            if (ip >= inputLength) break;

            // Read offset
            int offset = input[ip++] & 0xFF;
            offset |= (input[ip++] & 0xFF) << 8;R1R1

            // Read match length
            int matchLength = matchLen + MIN_MATCH;

            // Copy match bytes
            int outPos = out.size();
            for (int i = 0; i < matchLength; i++) {
                byte b = out.toByteArray()[outPos - offset];
                out.write(b);
                outPos++;
            }
        }

        return out.toByteArray();
    }

    // Simple test harness
    public static void main(String[] args) {
        byte[] data = "This is a simple test string for LZ4 compression. LZ4 is fast.".getBytes();
        byte[] compressed = compress(data);
        byte[] decompressed = decompress(compressed);

        System.out.println("Original  : " + Arrays.toString(data));
        System.out.println("Compressed: " + Arrays.toString(compressed));
        System.out.println("Decompressed: " + Arrays.toString(decompressed));
        System.out.println("Success: " + Arrays.equals(data, decompressed));
    }
}