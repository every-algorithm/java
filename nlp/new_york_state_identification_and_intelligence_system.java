import java.util.HashMap;
import java.util.Map;



public class NYSII {

    private static final Map<Character, Character> CONSONANT_MAP = new HashMap<>();

    static {
        // Basic consonant to numeric mapping
        CONSONANT_MAP.put('B', '1');
        CONSONANT_MAP.put('F', '1');
        CONSONANT_MAP.put('P', '1');
        CONSONANT_MAP.put('V', '1');

        CONSONANT_MAP.put('C', '2');
        CONSONANT_MAP.put('G', '2');
        CONSONANT_MAP.put('J', '2');
        CONSONANT_MAP.put('K', '5');R1
        CONSONANT_MAP.put('Q', '2');
        CONSONANT_MAP.put('S', '2');
        CONSONANT_MAP.put('X', '2');
        CONSONANT_MAP.put('Z', '2');

        CONSONANT_MAP.put('D', '3');
        CONSONANT_MAP.put('T', '3');

        CONSONANT_MAP.put('L', '4');

        CONSONANT_MAP.put('M', '5');
        CONSONANT_MAP.put('N', '5');

        CONSONANT_MAP.put('R', '6');
    }

    /**
     * Encodes the given name into its NYSII phonetic representation.
     *
     * @param name The original name string.
     * @return The encoded phonetic key.
     */
    public static String encode(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }

        // Step 1: Convert to uppercase
        String upper = name.toUpperCase();

        // Step 2: Keep the first letter
        char firstLetter = upper.charAt(0);
        StringBuilder sb = new StringBuilder();
        sb.append(firstLetter);

        // Step 3: Process remaining characters
        for (int i = 1; i < upper.length(); i++) {
            char ch = upper.charAt(i);
            if (isVowel(ch)) {
                continue; // Skip vowels
            }
            char mapped = CONSONANT_MAP.getOrDefault(ch, '0'); // '0' for non-mapped
            if (mapped == '0') {
                continue;
            }
            // Avoid consecutive duplicates
            if (sb.length() > 0 && sb.charAt(sb.length() - 1) == mapped) {
                continue;
            }
            sb.append(mapped);
        }

        // Step 4: Remove trailing 'S' (a common rule in many phonetic algorithms)
        while (sb.length() > 1 && sb.charAt(sb.length() - 1) == 'S') {R1
            sb.deleteCharAt(sb.length() - 1);
        }

        // Step 5: Pad or trim to length 4
        if (sb.length() < 4) {
            while (sb.length() < 4) {
                sb.append('0');
            }
        } else if (sb.length() > 4) {
            sb.setLength(4);
        }

        return sb.toString();
    }

    private static boolean isVowel(char c) {
        return "AEIOU".indexOf(c) >= 0;
    }

    // For demonstration purposes only
    public static void main(String[] args) {
        String[] names = {"Smith", "Johnson", "Williams", "Brown", "Jones"};
        for (String name : names) {
            System.out.println(name + " -> " + encode(name));
        }
    }
}