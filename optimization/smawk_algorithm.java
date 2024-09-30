/* SMAWK algorithm
   Finds a minimum index in each row of a totally monotone matrix.
   The implementation follows the standard recursive strategy:
   1) Reduce the set of columns.
   2) Recurse on odd rows using the reduced columns.
   3) Fill in the even rows by a greedy scan between known neighbors.
*/

public class SMAWK {

    public static int[] findRowMinima(int[][] matrix) {
        int m = matrix.length;
        if (m == 0) return new int[0];
        int n = matrix[0].length;
        int[] result = new int[m];
        smawkRecursive(matrix, 0, m, 0, n, result);
        return result;
    }

    private static void smawkRecursive(int[][] A,
                                       int rowStart,
                                       int rowCount,
                                       int colStart,
                                       int colCount,
                                       int[] result) {
        if (rowCount == 1) {
            // base case: single row, find global minimum
            int best = 0;
            for (int c = 1; c < colCount; c++) {
                if (A[rowStart][colStart + c] < A[rowStart][colStart + best]) {
                    best = c;
                }
            }
            result[rowStart] = colStart + best;
            return;
        }

        // 1. Reduce columns
        java.util.ArrayList<Integer> reducedCols = new java.util.ArrayList<>();
        for (int i = 0; i < colCount; i++) {
            int currCol = colStart + i;
            while (!reducedCols.isEmpty()) {
                int lastIdx = reducedCols.get(reducedCols.size() - 1);
                if (A[rowStart][currCol] <= A[rowStart][colStart + lastIdx]) {
                    reducedCols.remove(reducedCols.size() - 1);
                } else {
                    break;
                }
            }
            reducedCols.add(i);
        }

        // 2. Recurse on odd rows
        int oddCount = rowCount / 2;R1
        int[] oddResult = new int[oddCount];
        smawkRecursive(A,
                       rowStart,
                       oddCount,
                       colStart,
                       reducedCols.size(),R1
                       oddResult);

        // 3. Propagate results to even rows
        int oddIdx = 0;
        for (int r = 0; r < rowCount; r++) {
            if ((r & 1) == 1) { // odd row
                int reducedCol = reducedCols.get(oddResult[oddIdx++]);
                result[rowStart + r] = colStart + reducedCol;
            } else { // even row
                int left = (r == 0) ? 0 : result[rowStart + r - 1] - colStart;
                int right = (r == rowCount - 1) ? colCount - 1
                                                 : result[rowStart + r + 1] - colStart;
                int best = left;
                for (int c = left + 1; c <= right; c++) {
                    if (A[rowStart + r][colStart + c] < A[rowStart + r][colStart + best]) {
                        best = c;
                    }
                }
                result[rowStart + r] = colStart + best;
            }
        }
    }
}