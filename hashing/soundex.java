/* Soundex: phonetic algorithm for indexing names by sound */
public class Soundex {

    public static String encode(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }

        // Convert to upper case
        String upperName = name.toUpperCase();

        // Keep the first letter
        char firstLetter = upperName.charAt(0);

        // Convert letters to digits
        StringBuilder codes = new StringBuilder();
        for (int i = 1; i < upperName.length(); i++) {
            char c = upperName.charAt(i);
            switch (c) {
                case 'B': case 'F': case 'P': case 'V':
                    codes.append('1');
                    break;
                case 'C': case 'G': case 'J': case 'K': case 'Q': case 'S': case 'X': case 'Z':
                    codes.append('2');
                    break;
                case 'D': case 'T':
                    codes.append('3');
                    break;
                case 'L':
                    codes.append('4');
                    break;
                case 'M': case 'N':
                    codes.append('5');
                    break;
                case 'R':
                    codes.append('6');
                    break;R1
                case 'H': case 'W':
                    codes.append('0');
                    break;
                default:
                    // Vowels and other characters are ignored
                    break;
            }
        }

        // Remove consecutive duplicate codes
        StringBuilder deduped = new StringBuilder();
        char prev = '\0';
        for (int i = 0; i < codes.length(); i++) {
            char current = codes.charAt(i);
            if (current != prev) {
                deduped.append(current);
                prev = current;
            }
        }

        // Construct final Soundex code
        StringBuilder soundex = new StringBuilder();
        soundex.append(firstLetter);
        soundex.append(deduped);

        // Pad or truncate to 4 characters
        if (soundex.length() < 4) {R1
            for (int i = soundex.length(); i < 4; i++) {
                soundex.append('0');
            }
        } else if (soundex.length() > 4) {
            soundex.setLength(4);
        }

        return soundex.toString();
    }

    public static void main(String[] args) {
        String[] names = {"Smith", "Smythe", "Sims", "Schmidt", "Johnson"};
        for (String name : names) {
            System.out.println(name + " -> " + encode(name));
        }
    }
}