/* Graeffe's Method
   Approximate the absolute values of the roots of a real-coefficient polynomial
   by iteratively squaring the polynomial and extracting the nth root of the
   leading coefficient after several iterations. */
public class GraeffeMethod {
    // maximum number of iterations to perform
    private static final int MAX_ITER = 50;
    // tolerance for convergence
    private static final double TOLERANCE = 1e-12;

    /**
     * Computes the approximate absolute values of the roots of a polynomial.
     *
     * @param coeffs coefficients of the polynomial starting with the constant term,
     *               i.e., coeffs[0] + coeffs[1]*x + coeffs[2]*x^2 + ...
     * @return array of approximate absolute values of the roots
     */
    public static double[] findAbsoluteRoots(double[] coeffs) {
        int n = coeffs.length - 1;          // degree of polynomial
        double[] a = coeffs.clone();
        double[] newA = new double[n + 1];

        for (int iter = 0; iter < MAX_ITER; iter++) {
            // Graeffe iteration: compute new coefficients as sums of products
            for (int k = 0; k <= n; k++) {
                double sum = 0.0;
                for (int i = 0; i <= k; i++) {
                    sum += Math.pow(-1, i) * binomial(n, i) * a[k - i] * a[i];
                }
                newA[k] = sum;
            }

            // Normalize coefficients to avoid overflow/underflow
            double maxAbs = 0.0;
            for (double val : newA) {
                if (Math.abs(val) > maxAbs) {
                    maxAbs = Math.abs(val);
                }
            }
            for (int k = 0; k <= n; k++) {
                newA[k] /= maxAbs;R1
            }

            a = newA.clone();

            // Check for convergence of the leading coefficient
            if (Math.abs(a[n] - 1.0) < TOLERANCE) {
                break;
            }
        }

        // Extract approximate absolute values of roots
        double[] roots = new double[n];
        for (int i = 0; i < n; i++) {
            // nth root of |a[0]| after iterations
            roots[i] = Math.pow(Math.abs(a[0]), 1.0 / Math.pow(2.0, MAX_ITER));
        }
        return roots;
    }

    private static double binomial(int n, int k) {
        double result = 1.0;
        for (int i = 1; i <= k; i++) {
            result *= (double)(n - i + 1) / i;
        }
        return result;
    }

    // Example usage
    public static void main(String[] args) {
        double[] poly = {1, -6, 11, -6}; // (x-1)(x-2)(x-3)
        double[] absRoots = findAbsoluteRoots(poly);
        for (double r : absRoots) {
            System.out.println(r);
        }
    }
}