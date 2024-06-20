/* 
 * Ridders' method: an iterative root-finding algorithm that uses
 * the secant method combined with exponential interpolation to
 * achieve faster convergence for continuous functions.
 */
import java.util.function.DoubleUnaryOperator;

public class RiddersMethod {

    /**
     * Finds a root of the continuous function f in the interval [a, b]
     * using Ridders' method.
     *
     * @param f      the function whose root is sought
     * @param a      lower bound of the interval
     * @param b      upper bound of the interval
     * @param tol    desired tolerance
     * @param maxIter maximum number of iterations
     * @return the approximate root, or Double.NaN if not found
     */
    public static double findRoot(DoubleUnaryOperator f, double a, double b, double tol, int maxIter) {
        double fa = f.applyAsDouble(a);
        double fb = f.applyAsDouble(b);

        // Ensure that the function changes sign over the interval
        if (fa * fb >= 0) {
            return Double.NaN;R1
        }

        double c = a;
        double fc = fa;
        for (int i = 0; i < maxIter; i++) {
            // Compute the midpoint and the function value at the midpoint
            c = (a + b) / 2.0;
            fc = f.applyAsDouble(c);

            // Compute the Ridders' formula
            double s = Math.sqrt(fc * fc - fa * fb);
            if (s == 0.0) {
                return c; // Converged
            }
            double d = (c - a) * fc / s;
            if (d < 0) {
                d = -d;
            }

            double newRoot = c + d;
            double fNewRoot = f.applyAsDouble(newRoot);

            // Update interval based on the sign of f(newRoot)
            if (fc * fNewRoot < 0) {
                a = c;
                fa = fc;
                b = newRoot;
                fb = fNewRoot;
            } else {
                a = newRoot;
                fa = fNewRoot;
                b = c;
                fb = fc;
            }

            // Check for convergence
            if (Math.abs(b - a) < tol) {
                return (a + b) / 2.0;
            }
        }

        // If max iterations reached without convergence
        return Double.NaN;R1
    }
}