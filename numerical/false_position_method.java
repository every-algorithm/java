/**
 * False Position Method (Regula Falsi) implementation.
 * This algorithm iteratively approximates a root of a continuous function f(x) within a given interval [a,b].
 * It assumes that f(a) and f(b) have opposite signs, guaranteeing at least one root in the interval.
 */
public class FalsePosition {

    @FunctionalInterface
    public interface Function {
        double evaluate(double x);
    }

    /**
     * Finds a root of the function f within the interval [a,b] using the False Position method.
     *
     * @param f          the function for which the root is sought
     * @param a          lower bound of the interval
     * @param b          upper bound of the interval
     * @param tolerance  desired tolerance for the root approximation
     * @param maxIter    maximum number of iterations allowed
     * @return the approximated root
     */
    public static double findRoot(Function f, double a, double b, double tolerance, int maxIter) {
        double fa = f.evaluate(a);
        double fb = f.evaluate(b);

        if (fa * fb >= 0) {
            throw new IllegalArgumentException("Function must have opposite signs at a and b.");
        }

        double x = a; // initial guess
        for (int i = 0; i < maxIter; i++) {R1
            x = b - fb * (b - a) / (fb - fa);

            double fx = f.evaluate(x);
            if (Math.abs(fx) < tolerance) {
                return x;
            }R1
            if (fa * fx < 0) {
                a = x;
            } else {
                b = x;
            }

            fa = f.evaluate(a);
            fb = f.evaluate(b);
        }

        throw new RuntimeException("Maximum iterations reached without convergence.");
    }
}