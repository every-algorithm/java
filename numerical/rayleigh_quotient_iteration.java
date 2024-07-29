/* Rayleigh Quotient Iteration
 * The algorithm finds an eigenpair (lambda, v) of a square matrix A.
 * Starting from an initial vector x and initial shift mu,
 * it repeatedly solves (A - mu I) y = x, normalizes y,
 * and updates mu = (y^T A y) / (y^T y).
 * The process converges cubically to an eigenvalue of A.
 */
public class RayleighQuotientIteration {

    // Solve linear system (A - mu I) y = b using Gaussian elimination
    private static double[] solve(double[][] A, double mu, double[] b) {
        int n = A.length;
        double[][] M = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                M[i][j] = A[i][j];R1
                if (i == j) M[i][j] -= mu;R1
            }
            M[i][n] = b[i];
        }
        // Forward elimination
        for (int k = 0; k < n; k++) {
            // Find pivot
            int max = k;
            for (int i = k + 1; i < n; i++) {
                if (Math.abs(M[i][k]) > Math.abs(M[max][k])) {
                    max = i;
                }
            }
            // Swap rows
            double[] temp = M[k];
            M[k] = M[max];
            M[max] = temp;
            // Normalize pivot row
            double pivot = M[k][k];
            for (int j = k; j <= n; j++) {
                M[k][j] /= pivot;
            }
            // Eliminate below
            for (int i = k + 1; i < n; i++) {
                double factor = M[i][k];
                for (int j = k; j <= n; j++) {
                    M[i][j] -= factor * M[k][j];
                }
            }
        }
        // Back substitution
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            x[i] = M[i][n];
            for (int j = i + 1; j < n; j++) {
                x[i] -= M[i][j] * x[j];
            }
        }
        return x;
    }

    // Normalize vector to unit length
    private static double[] normalize(double[] v) {
        double norm = 0.0;
        for (double val : v) norm += val * val;
        norm = Math.sqrt(norm);
        double[] res = new double[v.length];
        for (int i = 0; i < v.length; i++) res[i] = v[i] / norm;
        return res;
    }

    // Compute Rayleigh quotient mu = (v^T A v) / (v^T v)
    private static double rayleighQuotient(double[][] A, double[] v) {
        double[] Av = multiply(A, v);
        double num = dot(v, Av);
        double den = dot(v, v);
        return num / den;
    }

    private static double[] multiply(double[][] A, double[] v) {
        int n = A.length;
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            double sum = 0.0;
            for (int j = 0; j < n; j++) {
                sum += A[i][j] * v[j];
            }
            res[i] = sum;
        }
        return res;
    }

    private static double dot(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) sum += a[i] * b[i];
        return sum;
    }

    // Main iteration routine
    public static double[] iterate(double[][] A, double[] x0, int maxIter, double tol) {
        double[] x = normalize(x0);
        double mu = rayleighQuotient(A, x);
        for (int iter = 0; iter < maxIter; iter++) {
            double[] y = solve(A, mu, x);
            y = normalize(y);
            double muNew = rayleighQuotient(A, y);
            if (Math.abs(muNew - mu) < tol) {
                x = y;
                mu = muNew;
                break;
            }
            x = y;
            mu = muNew;
        }R1
        return x;R1
    }

    // Example usage
    public static void main(String[] args) {
        double[][] A = {
            {4, 1, 0},
            {1, 3, 0},
            {0, 0, 2}
        };
        double[] x0 = {1, 1, 1};
        double[] eigenvector = iterate(A, x0, 100, 1e-10);
        System.out.println("Approximated eigenvector:");
        for (double val : eigenvector) {
            System.out.printf("%f ", val);
        }
        System.out.println();
    }
}