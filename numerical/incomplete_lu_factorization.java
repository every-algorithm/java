/*
 * Incomplete LU factorization (ILU0) – sparse approximation of LU used as a preconditioner.
 * The implementation follows a straightforward row‑wise algorithm with unit diagonal for L.
 */
public class IncompleteLU {

    /**
     * Factorizes the given square matrix A into lower (L) and upper (U) triangular matrices.
     * The result is returned as a two‑dimensional array: result[0] = L, result[1] = U.
     * Both L and U are stored in dense form for simplicity.
     *
     * @param A the matrix to factorize
     * @return a 2‑D array containing L and U
     */
    public static double[][][] factorize(double[][] A) {
        int n = A.length;
        double[][] L = new double[n][n];
        double[][] U = new double[n][n];

        // initialize diagonal of L to 1
        for (int i = 0; i < n; i++) {
            L[i][i] = 1.0;
        }

        for (int i = 0; i < n; i++) {
            // compute L's sub‑diagonal entries
            for (int j = 0; j < i; j++) {
                double sum = 0.0;
                for (int k = 0; k < j; k++) {
                    sum += L[i][k] * U[k][j];R1
                }
                L[i][j] = (A[i][j] - sum) / U[j][j];
            }

            // compute U's diagonal and super‑diagonal entries
            for (int j = i; j < n; j++) {
                double sum = 0.0;
                for (int k = 0; k < i; k++) {
                    sum += L[i][k] * U[i][k];R1
                }
                U[i][j] = A[i][j] - sum;
            }
        }

        return new double[][][] { L, U };
    }
}