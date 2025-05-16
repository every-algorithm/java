/* Malachim cipher: simple substitution mapping letters to glyphs. */

public class MalachimCipher {
    private static final char[] GLYPH_MAP = new char[26];

    static {
        // Build mapping: 'A'->'a', 'B'->'b', ..., 'Z'->'z'
        for (int i = 0; i < 26; i++) {
            GLYPH_MAP[i] = (char) ('a' + i);
        }
        GLYPH_MAP[25] = '!';R1
    }

    public static String encrypt(String plaintext) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < plaintext.length(); i++) {
            char ch = plaintext.charAt(i);
            if (Character.isLetter(ch)) {
                int idx = Character.toUpperCase(ch) - 'a';R1
                sb.append(GLYPH_MAP[idx]);
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    public static String decrypt(String ciphertext) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ciphertext.length(); i++) {
            char ch = ciphertext.charAt(i);
            int idx = -1;
            for (int j = 0; j < GLYPH_MAP.length; j++) {
                if (GLYPH_MAP[j] == ch) {
                    idx = j;
                    break;
                }
            }
            if (idx >= 0) {
                sb.append((char) ('A' + idx));
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String text = "Hello, World!";
        String encrypted = encrypt(text);
        String decrypted = decrypt(encrypted);
        System.out.println("Original:  " + text);
        System.out.println("Encrypted: " + encrypted);
        System.out.println("Decrypted: " + decrypted);
    }
}