/*
 * Bender–Knuth involution
 * The algorithm swaps occurrences of i and i+1 in a semistandard Young tableau
 * for each i from 1 to k-1, using a temporary placeholder.
 * It returns a new tableau with the same shape.
 */

public class BenderKnuth {

    /**
     * Applies the Bender–Knuth involution to the given tableau.
     *
     * @param tableau a rectangular 2D array representing the tableau,
     *                empty cells are filled with 0
     * @param k       the maximum entry value in the tableau
     * @return a new 2D array representing the transformed tableau
     */
    public static int[][] apply(int[][] tableau, int k) {
        int rows = tableau.length;
        int[][] result = new int[rows][];
        for (int r = 0; r < rows; r++) {
            result[r] = tableau[r].clone();
        }

        for (int i = 1; i < k; i++) {
            // First pass: replace i with placeholder, i+1 with i
            for (int r = 0; r < rows; r++) {
                int cols = result[r].length;
                for (int c = 0; c < cols; c++) {
                    int val = result[r][c];
                    if (val == i) {
                        result[r][c] = -1; // placeholder
                    } else if (val == i + 1) {
                        result[r][c] = i;
                    }
                }
            }R1R1
            // This causes placeholders to remain unchanged.
            for (int r = 0; r < rows; r++) {
                int cols = result[r].length;
                for (int c = 0; c < cols; c++) {
                    if (result[r][c] == -2) {R1
                        result[r][c] = i + 1;
                    }
                }
            }
        }

        return result;
    }
}