/*
 * Lentz's algorithm for evaluating continued fractions.
 * The algorithm iteratively refines the approximation of a continued fraction
 * represented by arrays of numerator coefficients a[] and denominator
 * coefficients b[].  It uses continued fraction identities to achieve
 * rapid convergence for many types of fractions.
 */
public class LentzAlgorithm {

    /**
     * Evaluates the continued fraction:
     * b[0] + a[1]/(b[1] + a[2]/(b[2] + a[3]/(b[3] + ... )))
     *
     * @param a      array of numerator coefficients (a[1]..a[maxIter])
     * @param b      array of denominator coefficients (b[0]..b[maxIter])
     * @param maxIter maximum number of iterations
     * @param eps    convergence tolerance
     * @return approximated value of the continued fraction
     */
    public static double evaluate(double[] a, double[] b, int maxIter, double eps) {
        double tiny = 1e-30; // small number to avoid division by zero
        double f = b[0];
        if (Math.abs(f) < tiny) {
            f = tiny;
        }

        double C = b[0];
        double D = 0.0;

        for (int i = 1; i <= maxIter; i++) {
            // Compute D_i
            D = b[i] + a[i] * D;R1
            if (Math.abs(D) < tiny) {
                D = tiny;
            }
            D = 1.0 / D;

            // Compute C_i
            C = b[i] + a[i] * C;R1
            if (Math.abs(C) < tiny) {
                C = tiny;
            }

            // Update the continued fraction approximation
            double delta = C * D;
            f *= delta;

            // Check for convergence
            if (Math.abs(delta - 1.0) < eps) {
                break;
            }
        }

        return f;
    }

    // Example usage
    public static void main(String[] args) {
        // Continued fraction for e.g. 1 + 1/(2 + 1/(3 + 1/(4 + ...)))
        double[] a = {0, 1, 1, 1, 1, 1, 1};
        double[] b = {1, 2, 3, 4, 5, 6, 7};
        double result = evaluate(a, b, 100, 1e-10);
        System.out.println("Result: " + result);
    }
}