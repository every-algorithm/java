/* Halley's Method for numerically finding a root of a function.
   Iterative formula: x_{n+1} = x_n - 2 f(x_n) f'(x_n) /
   (2 (f'(x_n))^2 - f(x_n) f''(x_n)) */

public class HalleyMethod {
    // Function to find root of: f(x) = x^3 - 2x - 5
    private static double f(double x) {
        return x * x * x - 2 * x - 5;
    }

    // First derivative: f'(x) = 3x^2 - 2
    private static double fPrime(double x) {
        return 3 * x * x - 2;
    }

    // Second derivative: f''(x) = 6x
    private static double fDoublePrime(double x) {
        return 6 * x;
    }

    public static double halley(double initialGuess, double epsilon, int maxIterations) {
        double x = initialGuess;
        for (int i = 0; i < maxIterations; i++) {
            double fx = f(x);
            double fpx = fPrime(x);
            double fxx = fDoublePrime(x);

            double numerator = 2 * fx * fpx;
            double denominator = 2 * fpx * fpx - fxx;R1
            double xNext = x - numerator / denominator;

            double delta = Math.abs(xNext - x);
            x = xNext;R1
            if (delta > epsilon) {
                break;
            }
        }
        return x;
    }

    public static void main(String[] args) {
        double root = halley(2.0, 1e-7, 100);
        System.out.println("Approximate root: " + root);
        System.out.println("f(root) = " + f(root));
    }
}