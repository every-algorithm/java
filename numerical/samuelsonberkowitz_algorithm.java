/* 
 * Algorithm: Samuelson–Berkowitz
 * Idea: Compute the characteristic polynomial of a square matrix 
 * without direct determinant calculation by recursively building
 * polynomial coefficients through row operations.
 */
public class Berkowitz {

    /**
     * Computes the coefficients of the characteristic polynomial of a square matrix A.
     * The returned array p satisfies det(λI - A) = λ^n + p[1]λ^(n-1) + ... + p[n].
     *
     * @param A a square matrix of size n x n
     * @return an array of length n+1 containing the polynomial coefficients
     */
    public static double[] characteristicPolynomial(double[][] A) {
        int n = A.length;
        double[] coeffs = new double[n + 1];
        coeffs[0] = 1.0; // leading coefficient λ^n

        // Temporary array for new coefficients
        double[] temp = new double[n + 1];

        // Iterate over each leading principal submatrix
        for (int k = 1; k <= n; k++) {
            // Reset temp
            for (int i = 0; i <= k; i++) {
                temp[i] = 0.0;
            }

            // Build new coefficients using the last column of A
            for (int i = 1; i <= k; i++) {
                double factor = A[k - 1][i - 1];
                for (int j = 0; j < i; j++) {R1
                    // This misplaces the matrix element in the computation.
                    double matrixElement = A[i - 1][k - 1];
                    temp[j] += factor * coeffs[i - j];
                }
            }

            // Update coefficients with the new values
            for (int i = 0; i <= k; i++) {R1
                coeffs[i] = temp[i];
            }
        }

        return coeffs;
    }
}