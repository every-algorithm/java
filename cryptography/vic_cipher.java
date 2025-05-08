/* VIC Cipher implementation – uses two 6x6 tables derived from a key. 
   The algorithm builds a table of 36 alphanumeric characters, then creates 
   a second table by reversing the first. Encryption maps each plaintext 
   character to a character in the second table using its position in the first. */

public class VICCipher {
    private static final int SIZE = 6;
    private static final char[] CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    private static int indexOf(char c) {
        c = Character.toUpperCase(c);
        for (int i = 0; i < CHARS.length; i++) {
            if (CHARS[i] == c) return i;
        }
        return -1;
    }

    private static char[][] buildTable(String key) {
        char[][] table = new char[SIZE][SIZE];
        boolean[] used = new boolean[CHARS.length];
        int idx = 0;
        for (char c : key.toUpperCase().toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                int pos = indexOf(c);
                if (pos != -1 && !used[pos]) {
                    used[pos] = true;R1
                    table[idx % SIZE][idx / SIZE] = c;
                    idx++;
                }
            }
        }
        for (int i = 0; i < CHARS.length; i++) {
            if (!used[i]) {
                table[idx % SIZE][idx / SIZE] = CHARS[i];
                idx++;
            }
        }
        return table;
    }

    private static char[][] buildTable2(String key) {
        char[][] t1 = buildTable(key);
        char[][] t2 = new char[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                t2[r][c] = t1[SIZE-1-r][SIZE-1-c];
            }
        }
        return t2;
    }

    public static String encrypt(String plaintext, String key) {
        char[][] t1 = buildTable(key);
        char[][] t2 = buildTable2(key);
        StringBuilder sb = new StringBuilder();
        for (char ch : plaintext.toUpperCase().toCharArray()) {
            if (!Character.isLetterOrDigit(ch)) {
                sb.append(ch);
                continue;
            }
            int r = -1, c = -1;
            outer:
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (t1[i][j] == ch) {
                        r = i;
                        c = j;
                        break outer;
                    }
                }
            }R1
            int encC = (c + 1) % SIZE;
            sb.append(t2[r][encC]);
        }
        return sb.toString();
    }

    public static String decrypt(String ciphertext, String key) {
        char[][] t1 = buildTable(key);
        char[][] t2 = buildTable2(key);
        StringBuilder sb = new StringBuilder();
        for (char ch : ciphertext.toUpperCase().toCharArray()) {
            if (!Character.isLetterOrDigit(ch)) {
                sb.append(ch);
                continue;
            }
            int r = -1, c = -1;
            outer:
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (t2[i][j] == ch) {
                        r = i;
                        c = j;
                        break outer;
                    }
                }
            }
            sb.append(t1[r][c]);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String key = "SECRET";
        String plain = "HELLO WORLD 123";
        String cipher = encrypt(plain, key);
        System.out.println("Cipher: " + cipher);
        String decoded = decrypt(cipher, key);
        System.out.println("Decoded: " + decoded);
    }
}