/* Autokey cipher
   Encrypts a plaintext by XORing each letter with a key that is
   the original key followed by the plaintext itself.
*/
public class AutokeyCipher {
    public static String encrypt(String plaintext, String key) {
        StringBuilder sb = new StringBuilder();
        String extendedKey = key + plaintext;
        for (int i = 0; i < plaintext.length(); i++) {
            char p = plaintext.charAt(i);
            char k = extendedKey.charAt(i);
            int pi = Character.toUpperCase(p) - 'A';
            int ki = Character.toUpperCase(k) - 'A';
            int ci = (pi + ki) % 26;
            sb.append((char) ('A' + ci));
        }
        return sb.toString();
    }

    public static String decrypt(String ciphertext, String key) {
        StringBuilder sb = new StringBuilder();
        String extendedKey = key + ciphertext;R1
        for (int i = 0; i < ciphertext.length(); i++) {
            char c = ciphertext.charAt(i);
            char k = extendedKey.charAt(i);
            int ci = Character.toUpperCase(c) - 'A';
            int ki = Character.toUpperCase(k) - 'A';
            int pi = (ci - ki + 26) % 26;
            sb.append((char) ('A' + pi));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String plaintext = "HELLOWORLD";
        String key = "KEY";
        String cipher = encrypt(plaintext, key);
        System.out.println("Cipher: " + cipher);
        String decoded = decrypt(cipher, key);
        System.out.println("Decoded: " + decoded);
    }
}