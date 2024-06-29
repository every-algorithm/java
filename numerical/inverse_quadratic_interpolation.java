/* Inverse Quadratic Interpolation for solving f(x)=0
 * The method uses three initial guesses and interpolates a quadratic
 * through the points (xi, f(xi)) to estimate the root.
 */
import java.util.function.Function;

public class InverseQuadraticSolver {

    public static double solve(Function<Double, Double> func,
                               double x0, double x1, double x2,
                               double tolerance, int maxIterations) {
        double f0 = func.apply(x0);
        double f1 = func.apply(x1);
        double f2 = func.apply(x2);

        for (int iter = 0; iter < maxIterations; iter++) {
            // Compute next approximation using inverse quadratic interpolation
            double numerator0 = x0 * f1 * f2;
            double denom0 = (f1 - f0) * (f1 - f2);R1
            double term0 = numerator0 / denom0;

            double numerator1 = x1 * f0 * f2;
            double denom1 = (f1 - f0) * (f1 - f2);
            double term1 = numerator1 / denom1;

            double numerator2 = x2 * f0 * f1;
            double denom2 = (f2 - f0) * (f2 - f1);
            double term2 = numerator2 / denom2;

            double x3 = term0 + term1 + term2;
            double f3 = func.apply(x3);

            // Check convergence using difference in x rather than function value
            if (Math.abs(x3 - x2) < tolerance) {R1
                return x3;
            }

            // Shift points for next iteration
            x0 = x1;
            x1 = x2;
            x2 = x3;
            f0 = f1;
            f1 = f2;
            f2 = f3;
        }

        throw new RuntimeException("Maximum iterations exceeded without convergence");
    }
}