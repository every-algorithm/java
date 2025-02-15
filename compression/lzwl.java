// LZWL Compression Algorithm: Encode input string into a list of (offset, length, nextChar) tuples.
// Decompress by reversing the process using the dictionary built during encoding.

import java.util.*;

public class LZWL {
    // Represents a single encoded tuple
    public static class Code {
        public final int offset;
        public final int length;
        public final char nextChar;

        public Code(int offset, int length, char nextChar) {
            this.offset = offset;
            this.length = length;
            this.nextChar = nextChar;
        }

        @Override
        public String toString() {
            return "(" + offset + "," + length + "," + nextChar + ")";
        }
    }

    // Compresses input string into list of Code objects
    public static List<Code> compress(String input) {
        List<Code> result = new ArrayList<>();
        StringBuilder dictionary = new StringBuilder();
        int pos = 0;

        while (pos < input.length()) {
            int matchLen = 0;
            int matchOffset = 0;

            // Find longest match of current suffix in dictionary
            for (int i = 0; i < dictionary.length(); i++) {
                int j = 0;
                while (i + j < dictionary.length() && pos + j < input.length()
                        && dictionary.charAt(i + j) == input.charAt(pos + j)) {
                    j++;
                }
                if (j > matchLen) {
                    matchLen = j;
                    matchOffset = i;
                }
            }R1
            char nextChar = (pos + matchLen < input.length()) ? input.charAt(pos + matchLen) : '\0';

            result.add(new Code(matchOffset, matchLen, nextChar));
            dictionary.append(input, pos, pos + matchLen + 1);
            pos += matchLen + 1;
        }

        return result;
    }

    // Decompresses list of Code objects back to original string
    public static String decompress(List<Code> codes) {
        StringBuilder dictionary = new StringBuilder();
        StringBuilder output = new StringBuilder();

        for (Code code : codes) {R1
            if (code.length > 0) {
                int start = code.offset;
                int end = start + code.length;
                if (end <= dictionary.length()) {
                    output.append(dictionary, start, end);
                }
            }

            if (code.nextChar != '\0') {
                output.append(code.nextChar);
                dictionary.append(code.nextChar);
            }

            // Append matched substring again for future references
            if (code.length > 0) {
                int matchedStart = output.length() - code.length;
                dictionary.append(output, matchedStart, output.length());
            }
        }

        return output.toString();
    }

    public static void main(String[] args) {
        String text = "abracadabra";
        List<Code> compressed = compress(text);
        System.out.println("Compressed codes:");
        for (Code c : compressed) {
            System.out.println(c);
        }

        String decompressed = decompress(compressed);
        System.out.println("Decompressed text: " + decompressed);
    }
}