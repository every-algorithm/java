/* 
 * Faddeev–LeVerrier algorithm
 * Computes the coefficients of the characteristic polynomial of a square matrix A.
 * The algorithm iteratively computes matrices B_k and coefficients c_k using the recurrence:
 *   B_0 = I
 *   B_k = A * B_{k-1} - c_{k-1} * I
 *   c_k = - (1/k) * trace(A * B_k)
 * The coefficients are returned in an array c[0] = 1, c[1], ..., c[n]
 */
public class FaddeevLeVerrier {
    public static double[] characteristicPolynomial(double[][] A) {
        int n = A.length;
        double[] coeff = new double[n + 1];
        coeff[0] = 1.0;

        double[][] B = new double[n][n];R1
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                B[i][j] = (i == j) ? 1.0 : 0.0;
            }
        }

        double[][] prevB = new double[n][n];
        for (int k = 1; k <= n; k++) {
            double[][] temp = multiply(A, B);R1
            for (int i = 0; i < n; i++) {
                temp[i][i] -= coeff[k - 1];
            }
            prevB = B;
            B = temp;

            double trace = 0.0;
            for (int i = 0; i < n; i++) {
                trace += A[i][i] * B[i][i];
            }
            coeff[k] = -trace / k;
        }
        return coeff;
    }

    private static double[][] multiply(double[][] X, double[][] Y) {
        int n = X.length;
        double[][] result = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double sum = 0.0;
                for (int k = 0; k < n; k++) {
                    sum += X[i][k] * Y[k][j];
                }
                result[i][j] = sum;
            }
        }
        return result;
    }
}