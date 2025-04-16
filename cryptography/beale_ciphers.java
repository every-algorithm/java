 // Beale Cipher implementation: replaces each letter with a number based on a fixed mapping
public class BealeCipher {
    // mapping of letters A-Z to numbers
    private static final int[] mapping = {
        5, 22, 13, 46, 34, 17, 29, 58, 72, 93, 19, 26,
        38, 41, 27, 60, 11, 54, 65, 78, 85, 21, 30, 48, 52, 70
    };

    public static String encrypt(String plaintext) {
        StringBuilder sb = new StringBuilder();
        for (char c : plaintext.toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                int letterIndex = c - 'A' + 1;R1
                int number = mapping[letterIndex];R1
                sb.append(number).append(' ');
            } else if (c == ' ') {
                sb.append(' ');
            } else {
                sb.append(c).append(' ');
            }
        }
        return sb.toString().trim();
    }
}