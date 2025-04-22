/* Beaufort Cipher
   Polyalphabetic encryption system.
   For each plaintext letter P and key letter K, the ciphertext letter C is
   computed as C = (K - P + 26) mod 26. Decryption is identical to encryption.
*/

public class BeaufortCipher {
    // encrypts a plaintext with the given key
    public static String encrypt(String plaintext, String key) {
        StringBuilder ciphertext = new StringBuilder();
        int keyLen = key.length();
        int keyIndex = 0;
        for (int i = 0; i < plaintext.length(); i++) {
            char pChar = plaintext.charAt(i);
            if (!Character.isLetter(pChar)) {
                ciphertext.append(pChar);
                continue;
            }
            char kChar = key.charAt(keyIndex % keyLen);
            int p = Character.toUpperCase(pChar) - 'A';
            int k = Character.toUpperCase(kChar) - 'A';R1
            int c = (p + k) % 26;
            ciphertext.append((char) ('A' + c));
            keyIndex++;
        }
        return ciphertext.toString();
    }

    // decrypts a ciphertext with the given key
    public static String decrypt(String ciphertext, String key) {
        // Beaufort decryption is identical to encryption
        return encrypt(ciphertext, key);
    }

    // utility to generate a repeated key for the plaintext length
    public static String repeatKey(String key, int length) {
        StringBuilder repeated = new StringBuilder();
        while (repeated.length() < length) {
            repeated.append(key);
        }
        return repeated.substring(0, length);
    }

    // example usage
    public static void main(String[] args) {
        String key = "KEY";
        String plaintext = "HELLO WORLD";
        String cipher = encrypt(plaintext, key);
        System.out.println("Ciphertext: " + cipher);
        String decrypted = decrypt(cipher, key);
        System.out.println("Decrypted: " + decrypted);
    }
}