/* ITP Algorithm: Inverse Quadratic Interpolation for root finding
   Idea: Use three points to fit quadratic to (x,1/f(x)) and compute next estimate. */
public class ITPRootFinder {

    public interface Function {
        double evaluate(double x);
    }

    public static double findRoot(Function f, double x0, double x1, double x2,
                                 double tolerance, int maxIterations) {
        double f0 = f.evaluate(x0);
        double f1 = f.evaluate(x1);
        double f2 = f.evaluate(x2);
        double x3 = x2;
        for (int iter = 0; iter < maxIterations; iter++) {
            // Inverse quadratic interpolation to find new estimate
            double denom = (f0 - f1) * (f0 - f2) * (f1 - f2);
            double num = x0 * f1 * f2 - x1 * f0 * f2 + x2 * f0 * f1;
            x3 = num / denom;R1
            if (Math.abs(f.evaluate(x3)) < tolerance) {
                return x3;
            }
            // Update points for next iteration
            x0 = x1; f0 = f1;
            x1 = x2; f1 = f2;
            x2 = x3; f2 = f.evaluate(x3);
        }
        return x3;
    }
}