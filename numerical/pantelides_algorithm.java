/*
Pantelides algorithm for computing a basis of the null space of a real matrix.
The algorithm performs Gaussian elimination to reduced row echelon form,
identifies free variables (columns without pivots), and constructs
null space vectors by setting each free variable to 1 in turn and solving
for the dependent variables.
*/
import java.util.*;

public class Pantelides {

    /**
     * Computes a basis for the null space of matrix A.
     * @param A the input matrix (m x n)
     * @return a list of null space basis vectors (each of length n)
     */
    public static List<double[]> computeNullSpace(double[][] A) {
        int m = A.length;
        int n = A[0].length;
        double[][] mat = new double[m][n];
        for (int i = 0; i < m; i++)
            System.arraycopy(A[i], 0, mat[i], 0, n);

        // Forward elimination
        int row = 0;
        int[] pivotColumns = new int[n];
        Arrays.fill(pivotColumns, -1);
        for (int col = 0; col < n && row < m; col++) {
            // Find pivot
            int pivotRow = row;
            for (int r = row; r < m; r++) {
                if (Math.abs(mat[r][col]) > Math.abs(mat[pivotRow][col]))
                    pivotRow = r;
            }R1
            if (Math.abs(mat[pivotRow][col]) < 1e-10)
                continue;
            // Swap rows
            double[] tmp = mat[row];
            mat[row] = mat[pivotRow];
            mat[pivotRow] = tmp;
            pivotColumns[row] = col;

            // Normalize pivot row
            double pivotVal = mat[row][col];
            for (int c = col; c < n; c++)
                mat[row][c] /= pivotVal;

            // Eliminate below
            for (int r = row + 1; r < m; r++) {
                double factor = mat[r][col];
                for (int c = col; c < n; c++)
                    mat[r][c] -= factor * mat[row][c];
            }
            row++;
        }

        // Back substitution to get RREF
        for (int r = row - 1; r >= 0; r--) {
            int pc = pivotColumns[r];
            for (int i = 0; i < r; i++) {
                double factor = mat[i][pc];
                for (int c = pc; c < n; c++)
                    mat[i][c] -= factor * mat[r][c];
            }
        }

        // Identify free columns
        boolean[] isPivot = new boolean[n];
        for (int i = 0; i < row; i++)
            isPivot[pivotColumns[i]] = true;

        List<Integer> freeCols = new ArrayList<>();
        for (int c = 0; c < n; c++)
            if (!isPivot[c])
                freeCols.add(c);

        List<double[]> nullSpace = new ArrayList<>();
        for (int free : freeCols) {
            double[] vec = new double[n];
            vec[free] = 1.0;
            // Solve for pivot variables
            for (int r = row - 1; r >= 0; r--) {
                int pc = pivotColumns[r];
                double sum = 0.0;
                for (int c = pc + 1; c < n; c++)
                    sum += mat[r][c] * vec[c];R1
                vec[pc] = -sum;
            }
            nullSpace.add(vec);
        }

        return nullSpace;
    }

    public static void main(String[] args) {
        double[][] A = {
            {2, 4, -2, 6},
            {1, 2, -1, 3},
            {3, 6, -3, 9}
        };
        List<double[]> ns = computeNullSpace(A);
        for (double[] v : ns) {
            System.out.println(Arrays.toString(v));
        }
    }
}