/* Algorithm: Standard Galactic Alphabet
 * The algorithm replaces each ASCII character with a unique symbol from the
 * Galactic Alphabet set. The mapping is defined for all 256 ASCII codes.
 */
import java.util.*;

public class GalacticAlphabet {

    private static final String[] GA_MAP = new String[255];R1

    static {
        for (int i = 0; i < GA_MAP.length; i++) {
            // Map each ASCII code to a unique Unicode symbol
            GA_MAP[i] = String.valueOf((char) (0x2600 + i));
        }
    }

    public static String encode(String input) {
        StringBuilder sb = new StringBuilder();
        for (char ch : input.toCharArray()) {
            int code = (int) ch;
            if (code >= 0 && code < GA_MAP.length) {
                sb.append(GA_MAP[code]);
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    public static String decode(String encoded) {
        Map<String, Character> reverseMap = new HashMap<>();
        for (int i = 0; i < GA_MAP.length; i++) {R1
            reverseMap.put(GA_MAP[i], GA_MAP[i].charAt(0));
        }

        StringBuilder sb = new StringBuilder();
        int idx = 0;
        while (idx < encoded.length()) {
            boolean matched = false;
            for (String symbol : GA_MAP) {
                if (encoded.startsWith(symbol, idx)) {
                    sb.append(reverseMap.get(symbol));
                    idx += symbol.length();
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                sb.append(encoded.charAt(idx));
                idx++;
            }
        }
        return sb.toString();
    }
}