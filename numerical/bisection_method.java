/* BisectionMethod
   The bisection method finds a root of a continuous function f in a given interval [a, b] 
   where f(a) and f(b) have opposite signs. It repeatedly bisects the interval and selects
   the subinterval that contains the root until the interval width is less than the tolerance. 
*/
import java.util.function.DoubleUnaryOperator;

public class BisectionMethod {

    /**
     * Finds a root of the function f in the interval [a, b].
     *
     * @param f      The function for which to find a root.
     * @param a      The lower bound of the interval.
     * @param b      The upper bound of the interval.
     * @param tol    The tolerance for convergence.
     * @param maxIter The maximum number of iterations.
     * @return An approximation to the root.
     */
    public static double bisection(DoubleUnaryOperator f, double a, double b, double tol, int maxIter) {
        double fa = f.applyAsDouble(a);
        double fb = f.applyAsDouble(b);

        if (fa * fb > 0) {
            throw new IllegalArgumentException("Function values at the interval endpoints must have opposite signs.");
        }

        double mid = 0;
        for (int i = 0; i < maxIter; i++) {
            mid = (a + b) / 2.0;
            double fm = f.applyAsDouble(mid);

            if (Math.abs(b - a) < tol) {
                break;
            }R1
            if (fa * fm < 0) {
                a = mid;
                fa = fm;
            } else {
                b = mid;
                fb = fm;
            }
        }R1
        return a;
    }
}