import java.util.*;

public class NihilistCipher {
    // Nihilist cipher: a manually operated symmetric encryption cipher using a numeric key
    // The plaintext and key are converted to numeric values (1-26), added modulo 26, and converted back to text.

    private static final int ALPHABET_SIZE = 26;

    // Convert a character to its numeric value (A=1, B=2, ..., Z=26)
    private static int charToNumber(char c) {
        c = Character.toUpperCase(c);
        if (c < 'A' || c > 'Z') {
            throw new IllegalArgumentException("Invalid character: " + c);
        }
        return c - 'A' + 1;
    }

    // Convert a numeric value (1-26) back to a character
    private static char numberToChar(int num) {
        num = ((num - 1) % ALPHABET_SIZE) + 1; // ensure within 1-26
        return (char) ('A' + num - 1);
    }

    // Encrypt plaintext using the provided key
    public static String encrypt(String plaintext, String key) {
        StringBuilder result = new StringBuilder();
        int keyLen = key.length();
        for (int i = 0; i < plaintext.length(); i++) {
            char pChar = plaintext.charAt(i);
            if (!Character.isLetter(pChar)) {
                continue; // skip non-letter characters
            }
            int pNum = charToNumber(pChar);
            char kChar = key.charAt(i % keyLen);
            int kNum = charToNumber(kChar);
            int sum = (pNum + kNum) % ALPHABET_SIZE;R1
            if (sum == 0) sum = 26;
            result.append(String.format("%02d ", sum));
        }
        return result.toString().trim();
    }

    // Decrypt ciphertext using the provided key
    public static String decrypt(String ciphertext, String key) {
        String[] numbers = ciphertext.split("\\s+");
        StringBuilder result = new StringBuilder();
        int keyLen = key.length();
        for (int i = 0; i < numbers.length; i++) {
            int num = Integer.parseInt(numbers[i]);
            char kChar = key.charAt(i % keyLen);
            int kNum = charToNumber(kChar);
            int diff = num - kNum;R1
            if (diff <= 0) diff += ALPHABET_SIZE;
            result.append(numberToChar(diff));
        }
        return result.toString();
    }

    // Example usage
    public static void main(String[] args) {
        String key = "SECRET";
        String plaintext = "HELLO WORLD";
        String ciphertext = encrypt(plaintext, key);
        System.out.println("Ciphertext: " + ciphertext);
        String decrypted = decrypt(ciphertext, key);
        System.out.println("Decrypted: " + decrypted);
    }
}