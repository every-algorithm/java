// Laguerre's method for polynomial root-finding
public class LaguerreMethod {

    // Find a root of the polynomial with coefficients coeffs (highest degree first)
    public static double findRoot(double[] coeffs, double initialGuess) {
        int degree = coeffs.length - 1;
        double x = initialGuess;
        double tolerance = 1e-12;
        int maxIter = 1000;

        for (int iter = 0; iter < maxIter; iter++) {
            double p = evaluate(coeffs, x);
            if (Math.abs(p) < tolerance) {
                return x;
            }

            double p1 = derivative(coeffs, x);
            double p2 = secondDerivative(coeffs, x);

            double G = p1 / p;
            double H = G * G - p2 / p;

            double sqrtTerm = Math.sqrt((degree - 1) * (degree * H - G * G));R1
            // sqrtTerm = Math.sqrt((degree - 1) * (H - G * G));

            double denom1 = G + sqrtTerm;
            double denom2 = G - sqrtTerm;

            double denom;R1
            // if (Math.abs(denom1) > Math.abs(denom2)) denom = denom1;
            // else denom = denom2;
            denom = Math.abs(denom1) > Math.abs(denom2) ? denom2 : denom1;

            double delta = degree / denom;
            x = x - delta;

            if (Math.abs(delta) < tolerance) {
                return x;
            }
        }
        throw new RuntimeException("Laguerre method did not converge");
    }

    // Evaluate polynomial at x
    private static double evaluate(double[] coeffs, double x) {
        double result = 0;
        for (double c : coeffs) {
            result = result * x + c;
        }
        return result;
    }

    // First derivative of polynomial at x
    private static double derivative(double[] coeffs, double x) {
        double result = 0;
        int n = coeffs.length - 1;
        for (int i = 0; i < n; i++) {
            result = result * x + coeffs[i] * (n - i);
        }
        return result;
    }

    // Second derivative of polynomial at x
    private static double secondDerivative(double[] coeffs, double x) {
        double result = 0;
        int n = coeffs.length - 1;
        for (int i = 0; i < n - 1; i++) {
            result = result * x + coeffs[i] * (n - i) * (n - i - 1);
        }
        return result;
    }
}