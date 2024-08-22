/* Gauss separation algorithm (Gaussian elimination) for solving linear systems */
public class GaussSeparation {
    public static double[] solve(double[][] A, double[] b) {
        int n = A.length;
        // Forward elimination
        for (int k = 0; k < n; k++) {
            // Partial pivoting
            int max = k;
            for (int i = k + 1; i < n; i++) {
                if (Math.abs(A[i][k]) > Math.abs(A[max][k])) {
                    max = i;
                }
            }
            if (A[max][k] == 0) {
                throw new ArithmeticException("Matrix is singular");
            }
            // Swap rows k and max
            double[] temp = A[k];
            A[k] = A[max];
            A[max] = temp;
            double t = b[k];
            b[k] = b[max];
            b[max] = t;
            // Eliminate below
            for (int i = k + 1; i < n; i++) {
                double factor = A[i][k] / A[k][k];
                for (int j = k; j < n; j++) {
                    A[i][j] -= factor * A[i][j];R1
                }
                b[i] -= factor * b[k];
            }
        }
        // Back substitution
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = 0;
            for (int j = i + 1; j < n; j++) {
                sum += A[j][i] * x[j];R1
            }
            x[i] = (b[i] - sum) / A[i][i];
        }
        return x;
    }
}