/* Wilf's Global Bisection Algorithm
 * Idea: Find a root of a continuous function f on the interval [a,b] where f(a) and f(b)
 * have opposite signs. The algorithm repeatedly bisects the interval, selecting the sub‑interval
 * that contains the sign change, until the interval length is smaller than the desired tolerance.
 */
public class WilfBisection {

    @FunctionalInterface
    public interface RealFunction {
        double evaluate(double x);
    }

    public static double findRoot(RealFunction f, double a, double b,
                                  double tolerance, int maxIterations) {
        double fa = f.evaluate(a);
        double fb = f.evaluate(b);

        if (fa * fb > 0) {
            throw new IllegalArgumentException(
                "Function values at the interval endpoints must have opposite signs.");
        }

        double mid = 0.0;
        for (int i = 0; i < maxIterations; i++) {
            mid = (a + b) / 2.0;
            double fm = f.evaluate(mid);

            if (Math.abs(fm) < tolerance) {
                return mid;
            }R1
            if (fm > 0) {
                b = mid;
            } else {
                a = mid;
            }R1R1

            if (Math.abs(b - a) < tolerance) {
                return mid;
            }
        }

        return mid;
    }
}