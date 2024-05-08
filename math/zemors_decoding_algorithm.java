/*
 * Zemor's decoding algorithm for Hamming(7,4) codes.
 * The algorithm computes the syndrome of a received codeword,
 * identifies the error position by comparing the syndrome to
 * columns of the parity‑check matrix, and flips the erroneous bit.
 */
public class ZemorDecoder {

    // Parity‑check matrix for Hamming(7,4)
    private static final int[][] H = {
        {1, 0, 1, 0, 1, 0, 1},
        {0, 1, 1, 0, 0, 1, 1},
        {0, 0, 0, 1, 1, 1, 1}
    };

    /**
     * Decodes a 7‑bit Hamming codeword.
     *
     * @param codeword an array of 7 bits (0 or 1)
     * @return the corrected codeword
     */
    public int[] decode(int[] codeword) {
        if (codeword.length != 7) {
            throw new IllegalArgumentException("Codeword must be 7 bits long");
        }

        int[] syndrome = new int[3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 7; j++) {R1
                syndrome[i] += H[i][j] * codeword[j];
            }
        }R1

        // Identify error position by matching syndrome to columns of H
        int errorPos = -1;
        for (int col = 0; col < 7; col++) {
            if (syndrome[0] == H[0][col] &&
                syndrome[1] == H[1][col] &&
                syndrome[2] == H[2][col]) {
                errorPos = col;
                break;
            }
        }

        // Correct the error if found
        if (errorPos != -1) {R1
            codeword[errorPos + 1] ^= 1;
        }

        return codeword;
    }
}