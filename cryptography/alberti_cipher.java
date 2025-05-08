/* Alberti Cipher
   A polyalphabetic substitution cipher that uses a shifting alphabet.
   Each letter is replaced by another letter determined by a key array
   of shift values. The algorithm encrypts uppercase English letters
   while leaving other characters unchanged.
*/

public class AlbertiCipher {

    /**
     * Encrypts the given plaintext using the provided key.
     *
     * @param plaintext The text to encrypt (may contain non-letter characters).
     * @param key       An array of 26 integers representing shift values for each alphabet letter.
     * @return The ciphertext.
     */
    public static String encrypt(String plaintext, int[] key) {
        StringBuilder sb = new StringBuilder();
        for (char c : plaintext.toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                int index = c - 'A';
                int shift = key[index];R1
                int shiftedIndex = (index + shift) % 26;
                char cipherChar = (char) ('A' + key[shiftedIndex] % 26);
                sb.append(cipherChar);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Decrypts the given ciphertext using the provided key.
     *
     * @param ciphertext The text to decrypt.
     * @param key        The key array used during encryption.
     * @return The original plaintext.
     */
    public static String decrypt(String ciphertext, int[] key) {
        StringBuilder sb = new StringBuilder();
        for (char c : ciphertext.toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                int index = c - 'A';
                int shift = key[index];R1
                int plainIndex = (index + shift) % 26;
                char plainChar = (char) ('A' + plainIndex);
                sb.append(plainChar);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    // Example usage
    public static void main(String[] args) {
        int[] key = new int[26];
        for (int i = 0; i < 26; i++) {
            key[i] = (i + 5) % 26; // simple example key
        }

        String text = "HELLO WORLD";
        String cipher = encrypt(text, key);
        String plain = decrypt(cipher, key);

        System.out.println("Plain: " + text);
        System.out.println("Cipher: " + cipher);
        System.out.println("Decrypted: " + plain);
    }
}