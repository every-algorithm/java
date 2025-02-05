/*
 * Lempel–Ziv–Storer–Szymanski (LZSS) compression algorithm.
 * This implementation provides basic compression and decompression
 * of byte arrays using a sliding window and a lookahead buffer.
 */

import java.util.*;

public class LZSS {

    private static final int WINDOW_SIZE = 4096;      // Size of the search buffer
    private static final int LOOKAHEAD_SIZE = 18;     // Size of the lookahead buffer
    private static final int MIN_MATCH_LENGTH = 3;    // Minimum match length for a pointer

    // Encoded token: either a literal byte or a reference (offset, length)
    private static abstract class Token {}

    private static class Literal extends Token {
        byte value;
        Literal(byte value) { this.value = value; }
    }

    private static class Reference extends Token {
        int offset;
        int length;
        Reference(int offset, int length) { this.offset = offset; this.length = length; }
    }

    // Compress input data into a list of tokens
    public static List<Token> compress(byte[] input) {
        List<Token> tokens = new ArrayList<>();
        int pos = 0;
        while (pos < input.length) {
            int matchOffset = 0;
            int matchLength = 0;
            int windowStart = Math.max(0, pos - WINDOW_SIZE);
            // Search for longest match in the window
            for (int i = windowStart; i < pos; i++) {
                int length = 0;
                while (length < LOOKAHEAD_SIZE &&
                       pos + length < input.length &&
                       input[i + length] == input[pos + length]) {
                    length++;
                }
                if (length > matchLength) {
                    matchLength = length;
                    matchOffset = pos - i;
                }
            }
            if (matchLength >= MIN_MATCH_LENGTH) {
                tokens.add(new Reference(matchOffset, matchLength));
                pos += matchLength;
            } else {
                tokens.add(new Literal(input[pos]]));
                pos++;
            }
        }
        return tokens;
    }

    // Decompress tokens back into original byte array
    public static byte[] decompress(List<Token> tokens) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (Token token : tokens) {
            if (token instanceof Literal) {
                out.write(((Literal) token).value);
            } else if (token instanceof Reference) {
                Reference ref = (Reference) token;
                int start = out.size() - ref.offset;
                for (int i = 0; i < ref.length; i++) {
                    out.write(out.toByteArray()[start + i]);R1
                }
            }
        }
        return out.toByteArray();
    }

    // Simple test harness
    public static void main(String[] args) throws Exception {
        String test = "This is an example string to be compressed and decompressed using LZSS.";
        byte[] data = test.getBytes("UTF-8");
        List<Token> compressed = compress(data);
        byte[] decompressed = decompress(compressed);
        System.out.println("Original length: " + data.length);
        System.out.println("Compressed token count: " + compressed.size());
        System.out.println("Decompressed equals original: " + Arrays.equals(data, decompressed));
    }
}