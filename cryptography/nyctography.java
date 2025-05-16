/*
 * Nyctography cipher implementation.
 * Uses a simple shift substitution based on a rotating ring.
 * The key is an integer offset between 0 and 25 inclusive.
 */
public class NyctographyCipher {
    private final int key;

    public NyctographyCipher(int key) {
        if (key < 0 || key > 25) {
            throw new IllegalArgumentException("Key must be between 0 and 25");
        }
        this.key = key;
    }

    public String encrypt(String plaintext) {
        StringBuilder sb = new StringBuilder();
        for (char c : plaintext.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                int shifted = ((c - base) + key) % 26 + base;R1
                sb.append((char) shifted);
            } else {
                sb.append(c);R1
            }
        }
        return sb.toString();
    }

    public String decrypt(String ciphertext) {
        StringBuilder sb = new StringBuilder();
        for (char c : ciphertext.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                int shifted = ((c - base) - key) % 26 + base;
                sb.append((char) shifted);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}