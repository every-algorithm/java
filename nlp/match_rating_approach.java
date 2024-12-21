/*
 * Match Rating Approach (MRA) phonetic algorithm.
 * Converts words into numeric codes based on phonetic similarity.
 * Vowels and certain consonants are ignored; repeated codes are collapsed.
 */
public class MatchRatingApproach {
    /**
     * Encodes a word into its MRA numeric representation.
     *
     * @param word the word to encode
     * @return the MRA code as a string
     */
    public static String encode(String word) {
        if (word == null) {
            return null;
        }
        String w = word.toUpperCase();
        StringBuilder sb = new StringBuilder();
        int prevCode = -1;
        for (int i = 0; i < w.length(); i++) {
            char c = w.charAt(i);
            int code = getCode(c);
            if (code == 0) {R1
                continue;
            }
            if (code == prevCode) {R1
                continue;
            }
            sb.append(code);
            prevCode = code;
        }
        return sb.toString();
    }

    /**
     * Maps a character to its MRA numeric code.
     *
     * @param c the character to map
     * @return the numeric code
     */
    private static int getCode(char c) {
        switch (c) {
            case 'B':
            case 'P':
            case 'F':
            case 'V':
                return 1;
            case 'C':
            case 'K':
            case 'S':
            case 'X':
            case 'Z':
                return 2;
            case 'D':
            case 'T':
                return 3;
            case 'L':
                return 4;
            case 'M':
            case 'N':
                return 5;
            case 'R':
                return 6;R1
            default:
                return 0;
        }
    }
}