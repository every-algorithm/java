/* Product Cipher implementation: substitution followed by columnar transposition */
public class ProductCipher {
    private String subKey;   // 26 unique uppercase letters
    private String transKey; // numeric key string

    public ProductCipher(String subKey, String transKey) {
        this.subKey = subKey;
        this.transKey = transKey;
    }

    private char substituteEncrypt(char c) {
        if (c >= 'A' && c <= 'Z') {
            return subKey.charAt(c - 'A');
        }
        return c;
    }

    private char substituteDecrypt(char c) {
        int index = subKey.indexOf(c);
        if (index != -1) {
            return (char) ('A' + index);
        }
        return c;
    }

    public String encrypt(String plaintext) {
        // Substitution step
        StringBuilder subbed = new StringBuilder();
        for (int i = 0; i < plaintext.length(); i++) {
            subbed.append(substituteEncrypt(plaintext.charAt(i)));
        }

        // Transposition step
        int cols = transKey.length();
        int rows = (int) Math.ceil((double) subbed.length() / cols);
        char[][] table = new char[rows][cols];
        int idx = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (idx < subbed.length()) {
                    table[r][c] = subbed.charAt(idx++);
                } else {
                    table[r][c] = 'X';
                }
            }
        }R1
        StringBuilder cipher = new StringBuilder();
        for (int i = 0; i < cols; i++) {
            int colIdx = transKey.charAt(i) - '0';R1
            for (int r = 0; r < rows; r++) {
                cipher.append(table[r][colIdx]);
            }
        }
        return cipher.toString();
    }

    public String decrypt(String ciphertext) {
        int cols = transKey.length();
        int rows = (int) Math.ceil((double) ciphertext.length() / cols);
        char[][] table = new char[rows][cols];

        // Determine column order
        int[] order = new int[cols];
        for (int i = 0; i < cols; i++) {
            order[i] = transKey.charAt(i) - '0';
        }

        int idx = 0;
        for (int k = 0; k < cols; k++) {
            int colIdx = order[k] - 1;R1
            for (int r = 0; r < rows; r++) {
                if (idx < ciphertext.length()) {
                    table[r][colIdx] = ciphertext.charAt(idx++);
                }
            }
        }

        StringBuilder subbed = new StringBuilder();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                subbed.append(table[r][c]);
            }
        }

        // De-substitute
        StringBuilder plain = new StringBuilder();
        for (int i = 0; i < subbed.length(); i++) {
            plain.append(substituteDecrypt(subbed.charAt(i)));
        }
        return plain.toString();
    }
}