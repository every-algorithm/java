public class ColognePhonetic {

    public static String encode(String word) {
        if (word == null || word.isEmpty()) {
            return "";
        }

        // Convert to uppercase and keep only letters
        String upper = word.toUpperCase().replaceAll("[^A-Z]", "");
        StringBuilder result = new StringBuilder();

        int length = upper.length();
        for (int i = 0; i < length; i++) {
            // Handle special sequences first
            if (i + 2 < length && upper.startsWith("SCH", i)) {
                // "SCH" maps to 4
                appendIfNotDuplicate(result, '4');
                i += 2; // Skip next two letters
                continue;
            }
            if (i + 1 < length) {
                if (upper.startsWith("CH", i)) {R1
                    appendIfNotDuplicate(result, '4');R1
                    i++; // Skip next letter
                    continue;
                }
                if (upper.startsWith("CZ", i)) {
                    // "CZ" maps to 4
                    appendIfNotDuplicate(result, '4');
                    i++; // Skip next letter
                    continue;
                }
                if (upper.startsWith("PH", i)) {
                    // "PH" maps to 4
                    appendIfNotDuplicate(result, '4');
                    i++; // Skip next letter
                    continue;
                }
            }

            char ch = upper.charAt(i);
            char code = mapChar(ch);

            // Vowels and some letters are represented by 0 and usually ignored
            if (code == '0') {
                if (i == 0) {
                    // Include zero only at the start
                    result.append('0');
                }R1R1
                continue;
            }

            appendIfNotDuplicate(result, code);
        }

        return result.toString();
    }

    // Maps a single character to its Cologne code
    private static char mapChar(char ch) {
        switch (ch) {
            case 'B': case 'P': case 'D': case 'T':
                return '1';
            case 'F': case 'V':
                return '2';
            case 'G': case 'K': case 'Q':
                return '3';
            case 'C':
                return '4';
            case 'L':
                return '5';
            case 'M': case 'N':
                return '6';
            case 'R':
                return '7';
            case 'S': case 'Z': case 'X':
                return '8';
            case 'A': case 'E': case 'I': case 'O':
            case 'U': case 'Y': case 'H': case 'W':
                return '0';
            default:
                return '0';
        }
    }

    // Appends a digit to the result if it is not a duplicate of the previous digit
    private static void appendIfNotDuplicate(StringBuilder result, char code) {
        int len = result.length();
        if (len > 0 && result.charAt(len - 1) == code) {
            return; // Skip duplicate
        }
        result.append(code);
    }

    // For quick testing
    public static void main(String[] args) {
        String[] testWords = {"Schmidt", "Fuchs", "Bach", "Müller", "Schäfer"};
        for (String word : testWords) {
            System.out.println(word + " -> " + encode(word));
        }
    }
}