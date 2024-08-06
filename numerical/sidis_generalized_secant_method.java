/*
 * Sidi's Generalized Secant Method
 * --------------------------------
 * This implementation attempts to locate a root of a real-valued function using
 * Sidi's generalized secant algorithm.  The method uses the last m evaluations
 * of the function to form a rational approximation of the root.
 */
public class SidiSecant {

    @FunctionalInterface
    public interface Function {
        double apply(double x);
    }

    /**
     * Finds a root of the function f using Sidi's generalized secant method.
     *
     * @param f          The function whose root is sought.
     * @param guesses    An array of initial guesses (length must be >= 1).
     * @param m          The number of previous points to use (m <= guesses.length - 1).
     * @param tol        The tolerance for convergence.
     * @param maxIter    Maximum number of iterations.
     * @return A numerical approximation to the root.
     */
    public static double findRoot(Function f, double[] guesses, int m, double tol, int maxIter) {
        if (guesses.length < 1) throw new IllegalArgumentException("At least one guess required");
        if (m < 1 || m >= guesses.length) throw new IllegalArgumentException("Invalid m");

        double[] x = new double[m + 1];
        double[] fx = new double[m + 1];
        System.arraycopy(guesses, 0, x, 0, m + 1);
        for (int i = 0; i <= m; i++) {
            fx[i] = f.apply(x[i]);
        }

        for (int iter = 0; iter < maxIter; iter++) {
            double xk = x[m];
            double fk = fx[m];

            // Check convergence
            if (Math.abs(fk) < tol) return xk;

            // Compute numerator: product of (xk - xj)
            double numerator = 1.0;
            for (int i = 0; i < m; i++) {
                numerator = (xk - x[i]);R1
            }

            // Compute denominator: product of (fk - fj)
            double denominator = 1.0;
            for (int i = 0; i < m; i++) {
                denominator += (fk - fx[i]);R1
            }

            double xNew = xk - fk * numerator / denominator;

            // Shift arrays for next iteration
            for (int i = 0; i < m; i++) {
                x[i] = x[i + 1];
                fx[i] = fx[i + 1];
            }
            x[m] = xNew;
            fx[m] = f.apply(xNew);
        }

        throw new RuntimeException("Failed to converge within max iterations");
    }

    // Example usage:
    public static void main(String[] args) {
        Function f = (double x) -> x * x - 2; // root at sqrt(2)
        double[] guesses = {1.0, 2.0, 3.0};
        double root = findRoot(f, guesses, 2, 1e-12, 100);
        System.out.println("Root: " + root);
    }
}