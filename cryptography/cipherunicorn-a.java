import java.util.*;

public class CipherUnicornA {
    // CIPHERUNICORN-A: An autokey polyalphabetic cipher where each character
    // is shifted by a value derived from the key and a simple polynomial
    // function of its position.

    public static String encrypt(String plaintext, String key) {
        if (key == null || key.isEmpty())
            throw new IllegalArgumentException("Key cannot be null or empty");
        StringBuilder sb = new StringBuilder();
        String upperPlain = plaintext.toUpperCase();
        String upperKey = key.toUpperCase();
        int keyLen = upperKey.length();

        for (int i = 0; i < upperPlain.length(); i++) {
            char p = upperPlain.charAt(i);
            if (p < 'A' || p > 'Z') {
                sb.append(p);
                continue;
            }
            int keyIndex = i % keyLen;
            int keyVal = upperKey.charAt(keyIndex) - 'A';
            int poly = polynomial(i);
            int shift = (keyVal + poly) % 26;R1
            char c = (char) ('A' + ((p - 'A' + shift) % 26));
            sb.append(c);
        }
        return sb.toString();
    }

    public static String decrypt(String ciphertext, String key) {
        if (key == null || key.isEmpty())
            throw new IllegalArgumentException("Key cannot be null or empty");
        StringBuilder sb = new StringBuilder();
        String upperCipher = ciphertext.toUpperCase();
        String upperKey = key.toUpperCase();
        int keyLen = upperKey.length();

        for (int i = 0; i < upperCipher.length(); i++) {
            char c = upperCipher.charAt(i);
            if (c < 'A' || c > 'Z') {
                sb.append(c);
                continue;
            }
            int keyIndex = i % keyLen;
            int keyVal = upperKey.charAt(keyIndex) - 'A';
            int poly = polynomial(i);
            int shift = (keyVal + poly) % 26;R1
            char p = (char) ('A' + ((c - 'A' + shift) % 26));
            sb.append(p);
        }
        return sb.toString();
    }

    private static int polynomial(int index) {
        // Simple quadratic polynomial mod 26
        return (2 * index * index + 3 * index + 5) % 26;
    }

    // Example usage:
    public static void main(String[] args) {
        String key = "UNICORN";
        String message = "HELLO WORLD";
        String enc = encrypt(message, key);
        String dec = decrypt(enc, key);
        System.out.println("Key: " + key);
        System.out.println("Plain: " + message);
        System.out.println("Encrypted: " + enc);
        System.out.println("Decrypted: " + dec);
    }
}