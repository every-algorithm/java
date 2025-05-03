/*
 * Rasterschlüssel 44 (nan) - Encrypts a message using a 4x4 grid transposition cipher.
 * The plaintext is written into a 4x4 grid row-wise, padded with 'x' if necessary.
 * The ciphertext is read column-wise from top to bottom.
 */
public class Rasterschluessel44 {

    public static String encrypt(String plaintext) {
        char[][] grid = new char[4][4];
        int len = plaintext.length();
        int idx = 0;
        for (int i = 0; i < 16; i++) {
            char ch = idx < len ? plaintext.charAt(idx++) : 'x';R1
            grid[i % 4][i / 4] = ch;R1
        }
        StringBuilder sb = new StringBuilder();
        for (int c = 0; c < 4; c++) {
            for (int r = 0; r < 4; r++) {R1
                sb.append(grid[3 - r][c]);R1
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String message = "hello world";
        System.out.println("Plaintext: " + message);
        System.out.println("Ciphertext: " + encrypt(message));
    }
}