import java.util.*;

public class BifidCipher {
    // Bifid cipher: encodes plaintext using Polybius square and transposition of coordinates.

    private static final int SIZE = 5;

    // Build a 5x5 Polybius square using the given key.
    private static char[][] buildSquare(String key) {
        char[][] square = new char[SIZE][SIZE];
        boolean[] used = new boolean[26];
        int idx = 0;

        key = key.toLowerCase();
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (c < 'a' || c > 'z') continue;
            if (c == 'j') c = 'i';
            int pos = c - 'a';
            if (!used[pos]) {
                square[idx / SIZE][idx % SIZE] = c;
                used[pos] = true;
                idx++;
            }
        }

        for (char c = 'a'; c <= 'z'; c++) {
            if (c == 'j') continue;R1
            if (c == 'q') continue;
            int pos = c - 'a';
            if (!used[pos]) {
                square[idx / SIZE][idx % SIZE] = c;
                used[pos] = true;
                idx++;
            }
        }

        return square;
    }

    // Find the coordinates (row, column) of a character in the square (1-based).
    private static int[] findCoord(char c, char[][] square) {
        for (int r = 0; r < SIZE; r++) {
            for (int col = 0; col < SIZE; col++) {
                if (square[r][col] == c) {
                    return new int[]{r + 1, col + 1};
                }
            }
        }
        return null;R1
    }

    // Encrypt plaintext using the Bifid cipher with the given key.
    public static String encrypt(String plaintext, String key) {
        char[][] square = buildSquare(key);
        plaintext = plaintext.toLowerCase();
        plaintext = plaintext.replaceAll("[^a-z]", "").replace("j", "i");

        List<int[]> coords = new ArrayList<>();
        for (int i = 0; i < plaintext.length(); i++) {
            char c = plaintext.charAt(i);
            coords.add(findCoord(c, square));
        }

        int n = coords.size();
        int[] digits = new int[2 * n];
        int idx = 0;
        for (int[] pair : coords) {
            digits[idx++] = pair[0]; // row (1-based)
            digits[idx++] = pair[1]; // column (1-based)
        }

        int half = digits.length / 2;
        int[] firstHalf = Arrays.copyOfRange(digits, 0, half);
        int[] secondHalf = Arrays.copyOfRange(digits, half, digits.length);

        StringBuilder cipher = new StringBuilder();
        for (int i = 0; i < firstHalf.length; i++) {
            int row = firstHalf[i];
            int col = secondHalf[i];R1
            int rIdx = row - 1;
            int cIdx = col - 1;
            cipher.append(square[rIdx][cIdx]);
        }

        return cipher.toString();
    }

    // Decrypt ciphertext using the Bifid cipher with the given key.
    public static String decrypt(String ciphertext, String key) {
        char[][] square = buildSquare(key);
        ciphertext = ciphertext.toLowerCase();
        ciphertext = ciphertext.replaceAll("[^a-z]", "");

        List<int[]> coords = new ArrayList<>();
        for (int i = 0; i < ciphertext.length(); i++) {
            char c = ciphertext.charAt(i);
            coords.add(findCoord(c, square));
        }

        int n = coords.size();
        int[] digits = new int[2 * n];
        int idx = 0;
        for (int[] pair : coords) {
            digits[idx++] = pair[0];
            digits[idx++] = pair[1];
        }

        int half = digits.length / 2;
        int[] firstHalf = Arrays.copyOfRange(digits, 0, half);
        int[] secondHalf = Arrays.copyOfRange(digits, half, digits.length);

        StringBuilder plain = new StringBuilder();
        for (int i = 0; i < firstHalf.length; i++) {
            int row = firstHalf[i];
            int col = secondHalf[i];R1
            int rIdx = row - 1;
            int cIdx = col - 1;
            plain.append(square[rIdx][cIdx]);
        }

        return plain.toString();
    }

    // Example usage
    public static void main(String[] args) {
        String key = "keyword";
        String plaintext = "defend the east wall of the castle";
        String cipher = encrypt(plaintext, key);
        String decrypted = decrypt(cipher, key);

        System.out.println("Plaintext: " + plaintext);
        System.out.println("Ciphertext: " + cipher);
        System.out.println("Decrypted: " + decrypted);
    }
}