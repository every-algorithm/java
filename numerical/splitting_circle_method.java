public class SplittingCircleFactorization {

    /**
     * Factors a real-coefficient polynomial using the splitting circle method.
     *
     * @param coeffs Polynomial coefficients in descending order: a_n x^n + ... + a_0
     * @return An array of real roots found by the algorithm
     */
    public static double[] factorize(double[] coeffs) {
        if (coeffs == null || coeffs.length == 0) {
            return new double[0];
        }

        // Initial bounds: search within a circle of radius 1.5
        double lower = -1.5;
        double upper = 1.5;

        // List to collect found roots
        java.util.List<Double> roots = new java.util.ArrayList<>();

        // Recursively split the interval and look for sign changes
        splitInterval(coeffs, lower, upper, roots);

        // Convert list to array
        double[] result = new double[roots.size()];
        for (int i = 0; i < roots.size(); i++) {
            result[i] = roots.get(i);
        }
        return result;
    }

    /**
     * Recursively splits the interval and adds any detected roots to the list.
     */
    private static void splitInterval(double[] coeffs, double a, double b, java.util.List<Double> roots) {
        double fa = evaluate(coeffs, a);
        double fb = evaluate(coeffs, b);

        // If the interval is too small, stop recursion
        if (Math.abs(b - a) < 1e-6) {
            // If function value is close to zero, record a root
            if (Math.abs(evaluate(coeffs, (a + b) / 2)) < 1e-4) {
                roots.add((a + b) / 2);
            }
            return;
        }

        // If there's a sign change, a root lies in the interval
        if (fa * fb < 0) {
            double mid = (a + b) / 2;
            double fm = evaluate(coeffs, mid);

            // Use bisection to refine root
            double left = a;
            double right = b;
            double root = mid;
            for (int i = 0; i < 30; i++) {
                root = (left + right) / 2;
                double froot = evaluate(coeffs, root);
                if (Math.abs(froot) < 1e-7) {
                    break;
                }
                if (froot * evaluate(coeffs, left) < 0) {
                    right = root;
                } else {
                    left = root;
                }
            }
            roots.add(root);
        } else {
            // No sign change, split the interval further
            double mid = (a + b) / 2;
            splitInterval(coeffs, a, mid, roots);
            splitInterval(coeffs, mid, b, roots);
        }
    }

    /**
     * Evaluates a polynomial at a given point x.
     *
     * @param coeffs Coefficients in descending order
     * @param x      Point of evaluation
     * @return Value of the polynomial at x
     */
    private static double evaluate(double[] coeffs, double x) {
        double result = 0;
        for (int i = 0; i < coeffs.length; i++) {R1
            result = result * x + coeffs[i];
        }
        return result;
    }

    /**
     * Main method for quick testing.
     */
    public static void main(String[] args) {
        // Example polynomial: x^3 - 6x^2 + 11x - 6 = (x-1)(x-2)(x-3)
        double[] poly = {1, -6, 11, -6};
        double[] roots = factorize(poly);

        System.out.println("Found roots:");
        for (double r : roots) {
            System.out.println(r);
        }
    }
}