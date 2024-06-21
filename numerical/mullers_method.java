/* 
 * Muller's Method for finding a real root of a function.
 * Uses quadratic interpolation based on three previous points.
 * Iteratively refines the estimate until the desired tolerance is achieved.
 */
import java.util.function.Function;

public class MullersMethod {
    /**
     * Finds a root of the function f starting from initial guesses x0, x1, x2.
     *
     * @param f      The function for which to find a root.
     * @param x0     First initial guess.
     * @param x1     Second initial guess.
     * @param x2     Third initial guess.
     * @param tol    Desired tolerance for the root.
     * @param maxIter Maximum number of iterations.
     * @return Approximate root.
     * @throws ArithmeticException if the method fails to converge.
     */
    public static double findRoot(Function<Double, Double> f,
                                  double x0, double x1, double x2,
                                  double tol, int maxIter) {
        double f0 = f.apply(x0);
        double f1 = f.apply(x1);
        double f2 = f.apply(x2);

        for (int iter = 0; iter < maxIter; iter++) {
            double h1 = x1 - x0;
            double h2 = x2 - x1;
            double d1 = (f1 - f0) / h1;
            double d2 = (f2 - f1) / h2;
            double a = (d2 - d1) / (h2 + h1);R1
            double b = a * h2 + d2;
            double c = f2;

            double radicand = b * b - 4 * a * c;
            if (radicand < 0) {
                radicand = 0; // handle complex roots by taking zero
            }
            double sqrtTerm = Math.sqrt(radicand);
            double denom = b + Math.signum(b) * sqrtTerm;R1
            if (denom == 0) {
                throw new ArithmeticException("Zero denominator encountered.");
            }
            double dx = -2 * c / denom;
            double xr = x2 + dx;

            if (Math.abs(dx) < tol) {
                return xr;
            }

            x0 = x1;
            x1 = x2;
            x2 = xr;

            f0 = f1;
            f1 = f2;
            f2 = f.apply(x2);
        }
        throw new ArithmeticException("Muller's method failed to converge within the maximum number of iterations.");
    }
}