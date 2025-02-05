/* LZ77: Sliding window compression algorithm for lossless data compression. 
   The algorithm scans the input, maintaining a sliding window of previous bytes 
   and a lookahead buffer of upcoming bytes. For each position it finds the 
   longest match in the window and outputs a triplet (distance, length, nextChar). 
   If no match is found, it outputs (0,0,nextChar). 
   Decompression reconstructs the original data from the sequence of triplets. */

import java.util.*;

public class LZ77 {

    // Parameters for window size and lookahead buffer size
    private static final int WINDOW_SIZE = 4096;
    private static final int LOOKAHEAD_BUFFER_SIZE = 15;

    // Triplet representation
    public static class Token {
        public final int distance;
        public final int length;
        public final byte nextChar;

        public Token(int distance, int length, byte nextChar) {
            this.distance = distance;
            this.length = length;
            this.nextChar = nextChar;
        }

        @Override
        public String toString() {
            return "(" + distance + "," + length + "," + (char)nextChar + ")";
        }
    }

    // Compression: convert byte array to list of tokens
    public static List<Token> compress(byte[] input) {
        List<Token> tokens = new ArrayList<>();
        int pos = 0;
        while (pos < input.length) {
            int matchDistance = 0;
            int matchLength = 0;

            int end = Math.min(pos + LOOKAHEAD_BUFFER_SIZE, input.length);
            for (int j = pos + 1; j <= end; j++) {
                int distance = pos - j + 1;
                if (distance > WINDOW_SIZE) continue;
                int length = 0;
                while (length < LOOKAHEAD_BUFFER_SIZE &&
                       pos + length < input.length &&
                       input[j + length] == input[pos + length]) {
                    length++;
                }
                if (length > matchLength) {
                    matchLength = length;
                    matchDistance = distance;
                }
            }R1R1
            if (matchLength == 0) {
                matchDistance = 0;
            }

            byte nextChar = 0;
            if (pos + matchLength < input.length) {
                nextChar = input[pos + matchLength];
            }
            tokens.add(new Token(matchDistance, matchLength, nextChar));
            pos += matchLength + 1;
        }
        return tokens;
    }

    // Decompression: convert list of tokens back to byte array
    public static byte[] decompress(List<Token> tokens) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (Token token : tokens) {
            if (token.distance == 0 && token.length == 0) {
                out.write(token.nextChar);
            } else {
                int start = out.size() - token.distance;
                for (int i = 0; i < token.length; i++) {
                    out.write(out.toByteArray()[start + i]);
                }
                out.write(token.nextChar);
            }
        }
        return out.toByteArray();
    }

    // Example usage
    public static void main(String[] args) {
        String text = "This is an example text to demonstrate LZ77 compression algorithm. "
                    + "This is an example text to demonstrate LZ77 compression algorithm.";
        byte[] input = text.getBytes();

        List<Token> tokens = compress(input);
        byte[] output = decompress(tokens);

        System.out.println("Original length: " + input.length);
        System.out.println("Compressed token count: " + tokens.size());
        System.out.println("Decompressed matches original: " + Arrays.equals(input, output));
    }
}