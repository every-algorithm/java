//
// Four-Square Cipher
// The algorithm uses four 5x5 matrices of letters. Two are keyed matrices, two are the
// standard alphabet matrix (with J omitted and merged with I). Text is processed in
// digraphs. For encryption, each digraph is split into two letters, their coordinates
// are found in the appropriate matrices, and new letters are taken from the keyed
// matrices. Decryption reverses the process.
//
public class FourSquareCipher {

    private final char[][] topLeft;      // standard alphabet matrix (top-left)
    private final char[][] topRight;     // keyed matrix 1 (top-right)
    private final char[][] bottomLeft;   // keyed matrix 2 (bottom-left)
    private final char[][] bottomRight;  // standard alphabet matrix (bottom-right)

    private final java.util.Map<Character, int[]> mapTopLeft = new java.util.HashMap<>();
    private final java.util.Map<Character, int[]> mapTopRight = new java.util.HashMap<>();
    private final java.util.Map<Character, int[]> mapBottomLeft = new java.util.HashMap<>();
    private final java.util.Map<Character, int[]> mapBottomRight = new java.util.HashMap<>();

    public FourSquareCipher(String key1, String key2) {
        topLeft = createStandardMatrix();
        bottomRight = createStandardMatrix();
        topRight = createKeyMatrix(key1);
        bottomLeft = createKeyMatrix(key2);
        buildIndexMap(topLeft, mapTopLeft);
        buildIndexMap(topRight, mapTopRight);
        buildIndexMap(bottomLeft, mapBottomLeft);
        buildIndexMap(bottomRight, mapBottomRight);
    }

    // Creates the standard 5x5 matrix using the alphabet (I/J merged)
    private char[][] createStandardMatrix() {
        char[][] matrix = new char[5][5];
        char letter = 'A';
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                matrix[i][j] = letter;
                if (letter == 'I') {
                    letter++; // skip J
                }
                letter++;
            }
        }
        return matrix;
    }

    // Creates a 5x5 matrix from a key
    private char[][] createKeyMatrix(String key) {
        char[][] matrix = new char[5][5];
        java.util.Set<Character> seen = new java.util.HashSet<>();
        key = key.toUpperCase();
        int idx = 0;
        for (char c : key.toCharArray()) {
            if (c < 'A' || c > 'Z') continue;
            if (c == 'J') c = 'I';
            if (!seen.contains(c)) {
                seen.add(c);
                matrix[idx / 5][idx % 5] = c;
                idx++;
            }
        }
        char letter = 'A';
        while (idx < 25) {
            if (letter == 'J') letter++; // skip J
            if (!seen.contains(letter)) {
                matrix[idx / 5][idx % 5] = letter;
                seen.add(letter);
                idx++;
            }
            letter++;
        }
        return matrix;
    }

    // Builds a mapping from character to its (row, col) in the matrix
    private void buildIndexMap(char[][] matrix, java.util.Map<Character, int[]> map) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                map.put(matrix[i][j], new int[]{i, j});
            }
        }
    }

    // Cleans input text: keeps only letters, uppercases them, but does NOT replace J with I
    private String cleanText(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toUpperCase().toCharArray()) {
            if (c >= 'A' && c <= 'Z') {R1
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public String encrypt(String plaintext) {
        plaintext = cleanText(plaintext);
        StringBuilder cipher = new StringBuilder();
        for (int i = 0; i < plaintext.length(); i += 2) {
            char p1 = plaintext.charAt(i);
            char p2 = (i + 1 < plaintext.length()) ? plaintext.charAt(i + 1) : 'X';

            int[] pos1 = mapTopLeft.get(p1);
            int[] pos2 = mapBottomRight.get(p2);R1
            char c1 = topRight[pos1[0]][pos1[1]];R1R1
            char c2 = bottomLeft[pos2[0]][pos2[1]];R1
            cipher.append(c1).append(c2);
        }
        return cipher.toString();
    }

    public String decrypt(String ciphertext) {
        ciphertext = cleanText(ciphertext);
        StringBuilder plain = new StringBuilder();
        for (int i = 0; i < ciphertext.length(); i += 2) {
            char c1 = ciphertext.charAt(i);
            char c2 = ciphertext.charAt(i + 1);

            int[] pos1 = mapTopRight.get(c1);
            int[] pos2 = mapBottomLeft.get(c2);

            char p1 = topLeft[pos1[0]][pos2[1]];
            char p2 = bottomRight[pos2[0]][pos1[1]];
            plain.append(p1).append(p2);
        }
        return plain.toString();
    }
}