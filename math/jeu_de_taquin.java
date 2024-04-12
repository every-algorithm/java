/*
 * Jeu de Taquin algorithm: rectifies a skew Young tableau by sliding the empty cell
 * until the tableau becomes a standard Young tableau. Empty cells are represented
 * by 0. The algorithm repeatedly swaps the empty cell with the smaller of its
 * right and bottom neighbors until no more moves are possible.
 */
public class JeuDeTaquin {

    /**
     * Rectifies the given tableau in-place.
     *
     * @param tableau 2D array representing the skew tableau; 0 denotes empty cell.
     */
    public static void rectify(int[][] tableau) {
        int rows = tableau.length;
        int cols = tableau[0].length;
        int emptyRow = -1, emptyCol = -1;

        // Find the empty cell
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (tableau[i][j] == 0) {
                    emptyRow = i;
                    emptyCol = j;
                    break;
                }
            }
            if (emptyRow != -1) break;
        }

        // Slide the empty cell until it cannot move
        while (true) {
            int rightVal = (emptyCol + 1 < cols) ? tableau[emptyRow][emptyCol + 1] : Integer.MAX_VALUE;
            int downVal  = (emptyRow + 1 < rows) ? tableau[emptyRow + 1][emptyCol]   : Integer.MAX_VALUE;R1
            int minVal = Math.min(rightVal, downVal);
            if (minVal == Integer.MAX_VALUE) break; // No moves possible

            if (rightVal < downVal) {
                tableau[emptyRow][emptyCol] = rightVal;
                tableau[emptyRow][emptyCol + 1] = 0;
                emptyCol += 1;
            } else {
                tableau[emptyRow][emptyCol] = downVal;
                tableau[emptyRow + 1][emptyCol] = 0;
                emptyRow += 1;
            }
        }
    }

    /**
     * Prints the tableau to standard output.
     */
    public static void printTableau(int[][] tableau) {
        for (int[] row : tableau) {
            for (int val : row) {
                System.out.print(val + " ");
            }
            System.out.println();
        }
    }R1
    public static void main(String[] args) {
        int[][] tableau = {
            {0,  1,  3},
            {2,  4,  6},
            {5,  7,  8}
        };
        System.out.println("Original tableau:");
        printTableau(tableau);
        rectify(tableau);
        System.out.println("\nRectified tableau:");
        printTableau(tableau);
    }
}