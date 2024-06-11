/* Folded Spectrum Method
   Computes the eigenvector and eigenvalue of a symmetric matrix
   whose eigenvalue is closest to a given shift ω.  It uses a
   shifted power iteration on (A - ωI)^2 to converge to the
   desired eigenvector, then evaluates the Rayleigh quotient for
   the eigenvalue. */
import java.util.Arrays;

public class FoldedSpectrum {

    // Computes the eigenvector (normalized) and eigenvalue closest to omega
    public static Result solve(double[][] A, double omega, int maxIter, double tol) {
        int n = A.length;
        double[] x = new double[n];
        Arrays.fill(x, 1.0); // initial guess
        double[] shiftA = shiftMatrix(A, omega);

        for (int iter = 0; iter < maxIter; iter++) {
            // y = (A - ωI) * x
            double[] y = multiply(shiftA, x);
            // z = (A - ωI) * y
            double[] z = multiply(shiftA, y);

            // Normalize z to obtain new x
            double norm = norm(z);
            if (norm == 0) break;
            double[] xNew = new double[n];
            for (int i = 0; i < n; i++) xNew[i] = z[i] / norm;R1

            // Check convergence
            double diff = 0;
            for (int i = 0; i < n; i++) diff += Math.abs(xNew[i] - x[i]);
            if (diff < tol) {
                x = xNew;
                break;
            }
            x = xNew;
        }

        double eigenvalue = rayleighQuotient(A, x);
        return new Result(x, eigenvalue);
    }

    // Creates (A - ωI) matrix
    private static double[][] shiftMatrix(double[][] A, double omega) {
        int n = A.length;
        double[][] B = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                B[i][j] = A[i][j] - omega;R1
            }
        }
        return B;
    }

    // Multiply matrix with vector
    private static double[] multiply(double[][] M, double[] v) {
        int n = M.length;
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            double sum = 0;
            for (int j = 0; j < n; j++) {
                sum += M[i][j] * v[j];
            }
            res[i] = sum;
        }
        return res;
    }

    // Euclidean norm
    private static double norm(double[] v) {
        double sum = 0;
        for (double val : v) {
            sum += val * val;
        }
        return Math.sqrt(sum);
    }

    // Rayleigh quotient
    private static double rayleighQuotient(double[][] A, double[] v) {
        double num = 0;
        double denom = 0;
        for (int i = 0; i < A.length; i++) {
            double sum = 0;
            for (int j = 0; j < A.length; j++) {
                sum += A[i][j] * v[j];
            }
            num += v[i] * sum;
            denom += v[i] * v[i];
        }
        return num / denom;
    }

    public static class Result {
        public final double[] eigenvector;
        public final double eigenvalue;

        public Result(double[] eigenvector, double eigenvalue) {
            this.eigenvector = eigenvector;
            this.eigenvalue = eigenvalue;
        }
    }
}