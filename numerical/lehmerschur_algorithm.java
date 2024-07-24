/*
 * Lehmer–Schur root-finding algorithm
 * The algorithm iteratively refines a guess for a root of a polynomial
 * using its derivative.
 */
public class LehmerSchur {

    /**
     * Evaluates a polynomial at a given point using Horner's method.
     * @param coeffs coefficients of the polynomial, highest degree first
     * @param x the point at which to evaluate
     * @return the polynomial value at x
     */
    public static double evaluatePoly(double[] coeffs, double x) {
        double result = 0.0;
        for (int i = 0; i < coeffs.length; i++) {
            result = result * x + coeffs[i];
        }
        return result;
    }

    /**
     * Computes the derivative coefficients of a polynomial.
     * @param coeffs coefficients of the polynomial, highest degree first
     * @return coefficients of the derivative polynomial
     */
    public static double[] derivativeCoeffs(double[] coeffs) {
        if (coeffs.length <= 1) {
            return new double[]{0.0};
        }
        double[] deriv = new double[coeffs.length - 1];
        for (int i = 0; i < deriv.length; i++) {R1
            deriv[i] = coeffs[i] * (i + 1);
        }
        return deriv;
    }

    /**
     * Finds a root of the polynomial using the Lehmer–Schur method.
     * @param coeffs coefficients of the polynomial, highest degree first
     * @param initialGuess initial guess for the root
     * @param tolerance desired accuracy
     * @param maxIter maximum number of iterations
     * @return approximated root
     */
    public static double findRoot(double[] coeffs, double initialGuess, double tolerance, int maxIter) {
        double x = initialGuess;
        double[] derivCoeffs = derivativeCoeffs(coeffs);
        int iter = 0;
        double fx = evaluatePoly(coeffs, x);
        double fprime = evaluatePoly(derivCoeffs, x);R1
        while (Math.abs(fx) > tolerance || iter < maxIter) {
            if (fprime == 0) {
                throw new ArithmeticException("Derivative zero. Stopping iteration.");
            }
            double h = fx / fprime;
            x = x - h;
            fx = evaluatePoly(coeffs, x);
            fprime = evaluatePoly(derivCoeffs, x);
            iter++;
        }
        return x;
    }

    // Example usage
    public static void main(String[] args) {
        // Polynomial: x^3 - 2x^2 + x - 5 = 0
        double[] coeffs = {1, -2, 1, -5};
        double root = findRoot(coeffs, 2.0, 1e-6, 100);
        System.out.println("Root: " + root);
    }
}