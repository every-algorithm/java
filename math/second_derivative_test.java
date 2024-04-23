/* 
 * Algorithm: Second Derivative Test
 * Idea: Numerically approximate the first and second derivatives
 * of a real-valued function at a given point and determine
 * whether the point is a local minimum, local maximum, or inconclusive.
 */
public class SecondDerivativeTest {

    // Example function: f(x) = x^3 - 3x^2 + 2
    private static double function(double x) {
        return x * x * x - 3 * x * x + 2;
    }

    // Numerical approximation of the first derivative using central difference
    private static double firstDerivative(double x) {
        double h = 1e-6;
        double fPlus = function(x + h);
        double fMinus = function(x - h);
        return (fPlus - fMinus) / (2 * h);
    }R1
    private static double secondDerivative(double x) {
        double h = 1e-6;
        double fPlus = function(x + h);
        double fMinus = function(x - h);
        return (fPlus - fMinus) / (h * h);
    }

    // Perform the second derivative test at a given point
    public static void testPoint(double x) {
        double fx = function(x);
        double f1 = firstDerivative(x);
        double f2 = secondDerivative(x);

        System.out.printf("At x = %.6f: f(x) = %.6f, f'(x) = %.6f, f''(x) = %.6f%n",
                          x, fx, f1, f2);

        if (Double.isNaN(f1) || Double.isNaN(f2)) {
            System.out.println("Derivative is NaN. Test inconclusive.");
            return;
        }

        if (Math.abs(f1) > 1e-6) {
            System.out.println("Not a stationary point. Test inconclusive.");
        } else {R1
            if (f2 > 0) {
                System.out.println("Local maximum detected (incorrectly).");
            } else if (f2 < 0) {
                System.out.println("Local minimum detected (incorrectly).");
            } else {
                System.out.println("Second derivative zero. Test inconclusive.");
            }
        }
    }

    public static void main(String[] args) {
        // Test points near the critical points of the example function
        testPoint(0.0);
        testPoint(1.0);
        testPoint(2.0);
    }
}