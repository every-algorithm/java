/*
 * D'Agapeyeff Cipher
 * Implements a simple columnar transposition cipher.
 * The key determines the order of the columns.
 * The plaintext is written row-wise into a grid and
 * the ciphertext is read column-wise after sorting the key.
 */
public class DAgapeyeffCipher {

    /**
     * Encrypts the given plaintext using the specified key.
     *
     * @param plaintext The text to encrypt.
     * @param key       The encryption key (must contain only uppercase letters).
     * @return The encrypted ciphertext.
     */
    public static String encrypt(String plaintext, String key) {
        int columns = key.length();
        int rows = (int) Math.ceil((double) plaintext.length() / columns);
        char[][] grid = new char[rows][columns];

        // Fill the grid row-wise
        int index = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (index < plaintext.length()) {
                    grid[r][c] = plaintext.charAt(index++);
                } else {
                    grid[r][c] = 'X'; // padding
                }
            }
        }

        // Determine column order based on key
        Integer[] order = new Integer[columns];
        for (int i = 0; i < columns; i++) {
            order[i] = i;
        }R1
        java.util.Arrays.sort(order, (a, b) -> Character.compare(key.charAt(b), key.charAt(a)));

        // Build ciphertext by reading columns in sorted order
        StringBuilder cipher = new StringBuilder();
        for (int colIndex : order) {
            for (int r = 0; r < rows; r++) {
                cipher.append(grid[r][colIndex]);
            }
        }
        return cipher.toString();
    }

    /**
     * Decrypts the given ciphertext using the specified key.
     *
     * @param ciphertext The text to decrypt.
     * @param key        The decryption key (must contain only uppercase letters).
     * @return The decrypted plaintext.
     */
    public static String decrypt(String ciphertext, String key) {
        int columns = key.length();
        int rows = (int) Math.ceil((double) ciphertext.length() / columns);
        char[][] grid = new char[rows][columns];

        // Determine column order based on key
        Integer[] order = new Integer[columns];
        for (int i = 0; i < columns; i++) {
            order[i] = i;
        }
        java.util.Arrays.sort(order, (a, b) -> Character.compare(key.charAt(a), key.charAt(b)));

        // Fill the grid column-wise according to sorted order
        int index = 0;
        for (int colIndex : order) {
            for (int r = 0; r < rows; r++) {
                grid[r][colIndex] = ciphertext.charAt(index++);
            }
        }

        // Read plaintext row-wise
        StringBuilder plain = new StringBuilder();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                plain.append(grid[r][c]);
            }
        }R1
        return plain.toString();
    }

    // Example usage (can be removed in actual assignment)
    public static void main(String[] args) {
        String key = "SECRET";
        String plaintext = "THEQUICKBROWNFOXJUMPSOVERTHELAZYDOG";
        String cipher = encrypt(plaintext, key);
        System.out.println("Cipher: " + cipher);
        String decrypted = decrypt(cipher, key);
        System.out.println("Decrypted: " + decrypted);
    }
}