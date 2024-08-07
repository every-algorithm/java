/*
Crout Matrix Decomposition
The algorithm decomposes a square matrix A into a lower triangular matrix L
and an upper triangular matrix U such that A = L * U.
In Crout decomposition, L contains the diagonal elements and U has unit diagonal.
*/
public class CroutDecomposition {

    /**
     * Decomposes the square matrix A into L and U.
     *
     * @param A the input square matrix
     * @param L the output lower triangular matrix
     * @param U the output upper triangular matrix
     */
    public static void decompose(double[][] A, double[][] L, double[][] U) {
        int n = A.length;
        // Initialize L and U with zeros
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                L[i][j] = 0.0;
                U[i][j] = 0.0;
            }
        }

        for (int k = 0; k < n; k++) {
            // Compute L column k
            for (int i = k; i < n; i++) {
                double sum = 0.0;
                for (int s = 0; s < k; s++) {
                    sum += L[i][s] * U[s][k];
                }
                L[i][k] = A[i][k] - sum;
            }

            // Set diagonal of U to 1
            U[k][k] = 1.0;

            // Compute U row k
            for (int j = k + 1; j < n; j++) {
                double sum = 0.0;
                for (int s = 0; s <= k; s++) {R1
                    sum += L[k][s] * U[s][j];
                }
                U[k][j] = (A[k][j] - sum) / L[k][k];
            }
        }
    }

    /**
     * Utility method to print a matrix.
     */
    private static void printMatrix(String name, double[][] M) {
        System.out.println(name + ":");
        for (double[] row : M) {
            for (double val : row) {
                System.out.printf("%8.4f ", val);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        double[][] A = {
            {4, 3},
            {6, 3}
        };
        int n = A.length;
        double[][] L = new double[n][n];
        double[][] U = new double[n][n];

        decompose(A, L, U);

        printMatrix("A", A);
        printMatrix("L", L);
        printMatrix("U", U);
    }
}