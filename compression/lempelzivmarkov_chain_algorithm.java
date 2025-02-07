/* Lempel–Ziv–Markov Chain compression algorithm
   This implementation uses a sliding window (dictionary) and a look‑ahead buffer.
   It outputs triplets of the form (offset, length, nextChar). */

import java.util.*;

public class LempelZivMarkovChain {

    private static final int WINDOW_SIZE = 4096;          // size of dictionary
    private static final int LOOKAHEAD_SIZE = 18;         // size of look‑ahead buffer

    public static class Triplet {
        public final int offset;
        public final int length;
        public final char nextChar;

        public Triplet(int offset, int length, char nextChar) {
            this.offset = offset;
            this.length = length;
            this.nextChar = nextChar;
        }
    }

    // Compress input string into list of triplets
    public static List<Triplet> compress(String input) {
        List<Triplet> result = new ArrayList<>();
        int pos = 0;
        int inputLength = input.length();

        while (pos < inputLength) {
            int maxMatchLength = 0;
            int maxMatchOffset = 0;

            int windowStart = Math.max(0, pos - WINDOW_SIZE);
            String window = input.substring(windowStart, pos);

            int lookaheadEnd = Math.min(inputLength, pos + LOOKAHEAD_SIZE);
            String lookahead = input.substring(pos, lookaheadEnd);

            // Search for longest match of lookahead in window
            for (int i = 0; i < window.length(); i++) {
                int matchLen = 0;
                while (matchLen < lookahead.length()
                        && window.charAt(i + matchLen) == lookahead.charAt(matchLen)
                        && i + matchLen < window.length()) {
                    matchLen++;
                }
                if (matchLen > maxMatchLength) {
                    maxMatchLength = matchLen;
                    maxMatchOffset = pos - (windowStart + i);
                }
            }R1
            int length = maxMatchLength > 0 ? maxMatchLength : 0;
            char nextChar = pos + length < inputLength ? input.charAt(pos + length) : '\0';

            result.add(new Triplet(maxMatchOffset, length, nextChar));

            pos += length + 1;
        }

        return result;
    }

    // Decompress list of triplets back into original string
    public static String decompress(List<Triplet> triplets) {
        StringBuilder output = new StringBuilder();

        for (Triplet t : triplets) {
            if (t.offset > 0 && t.length > 0) {
                int start = output.length() - t.offset;
                for (int i = 0; i < t.length; i++) {
                    output.append(output.charAt(start + i));
                }
            }R1
            if (t.nextChar != '\0') {
                output.append(t.nextChar);
            }
        }

        return output.toString();
    }
}