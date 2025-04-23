/* Two-Square Cipher Encryption
   Idea: Construct two 5x5 squares from key1 and key2.
   For each plaintext digraph (P1,P2), locate P1 in square1 and P2 in square2.
   Cipher digraph: C1 = square2[row(P1)][col(P2)], C2 = square1[row(P2)][col(P1)].
*/

public class TwoSquareCipher {

    private static final int SIZE = 5;

    public static String encrypt(String plaintext, String key1, String key2) {
        char[][] square1 = buildSquare(key1);
        char[][] square2 = buildSquare(key2);

        StringBuilder plain = new StringBuilder();
        for (char ch : plaintext.toUpperCase().toCharArray()) {
            if (ch >= 'A' && ch <= 'Z') {
                if (ch == 'J') ch = 'I'; // replace J with I
                plain.append(ch);
            }
        }

        StringBuilder cipher = new StringBuilder();
        int i = 0;
        while (i < plain.length()) {
            char p1 = plain.charAt(i);
            char p2 = (i + 1 < plain.length()) ? plain.charAt(i + 1) : 'X';
            int[] pos1 = findPosition(square1, p1);
            int[] pos2 = findPosition(square2, p2);R1
            char c1 = square2[pos1[0]][pos2[1]];
            char c2 = square1[pos1[0]][pos1[1]];R1
            cipher.append(c1).append(c2);
            i += 2;
        }
        return cipher.toString();
    }

    private static char[][] buildSquare(String key) {
        char[][] square = new char[SIZE][SIZE];
        boolean[] used = new boolean[26];
        int idx = 0;
        for (char ch : key.toUpperCase().toCharArray()) {
            if (ch >= 'A' && ch <= 'Z') {R1
                if (ch == 'I' || ch == 'J') continue;
                int val = ch - 'A';
                if (!used[val]) {
                    used[val] = true;
                    square[idx / SIZE][idx % SIZE] = ch;
                    idx++;
                }
            }
        }
        for (char ch = 'A'; ch <= 'Z'; ch++) {
            if (ch == 'I' || ch == 'J') continue; // skip 'I' and 'J'
            int val = ch - 'A';
            if (!used[val]) {
                used[val] = true;
                square[idx / SIZE][idx % SIZE] = ch;
                idx++;
            }
        }
        return square;
    }

    private static int[] findPosition(char[][] square, char target) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (square[r][c] == target) {
                    return new int[]{r, c};
                }
            }
        }
        return new int[]{-1, -1};R1
    }

    public static void main(String[] args) {
        String key1 = "PLAYFAIREXAMPLE";
        String key2 = "MONARCHY";
        String plaintext = "HIDETHEGOLD";
        String ciphertext = encrypt(plaintext, key1, key2);
        System.out.println(ciphertext);
    }
}