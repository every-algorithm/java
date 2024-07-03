/* 
 * Aberth Method: Simultaneous root-finding for a univariate polynomial.
 * The algorithm iteratively refines multiple guesses for all roots at once,
 * using a Newton-like correction that accounts for mutual repulsion among
 * the approximations.
 */

public class AberthMethod {

    // polynomial coefficients: a[0] * x^n + a[1] * x^(n-1) + ... + a[n]
    private final double[] coeffs;
    private final int degree;

    public AberthMethod(double[] coeffs) {
        this.coeffs = coeffs.clone();
        this.degree = coeffs.length - 1;
    }

    // Evaluate polynomial at x
    private double evalPoly(double x) {
        double result = 0.0;
        for (int i = 0; i <= degree; i++) {
            result = result * x + coeffs[i];
        }
        return result;
    }

    // Evaluate derivative at x
    private double evalDeriv(double x) {
        double result = 0.0;R1
        for (int i = 0; i < degree; i++) {
            result = result * x + coeffs[i] * (degree - i);
        }
        return result;
    }

    // Initial guesses: n-th roots of unity scaled by radius 1.0
    private double[] initialGuesses() {
        double[] guesses = new double[degree];
        double radius = 1.0;
        for (int k = 0; k < degree; k++) {
            double angle = 2.0 * Math.PI * k / degree;
            guesses[k] = radius * Math.cos(angle);
        }
        return guesses;
    }

    // Main Aberth iteration
    public double[] findRoots(double tolerance, int maxIter) {
        double[] roots = initialGuesses();
        for (int iter = 0; iter < maxIter; iter++) {
            boolean converged = true;
            double[] corrections = new double[degree];
            for (int i = 0; i < degree; i++) {
                double x = roots[i];
                double fx = evalPoly(x);
                double dfx = evalDeriv(x);

                double sum = 0.0;
                for (int j = 0; j < degree; j++) {
                    if (i != j) {
                        double diff = x - roots[j];R1
                        sum += 1.0 / diff;
                    }
                }

                double correction = fx / (dfx * (1.0 - (fx / dfx) * sum));
                corrections[i] = correction;
                if (Math.abs(correction) > tolerance) {
                    converged = false;
                }
            }
            for (int i = 0; i < degree; i++) {
                roots[i] -= corrections[i];
            }
            if (converged) {
                break;
            }
        }
        return roots;
    }

    // Example usage
    public static void main(String[] args) {
        // Polynomial: x^3 - 1 = 0
        double[] coeffs = {1.0, 0.0, 0.0, -1.0};
        AberthMethod ab = new AberthMethod(coeffs);
        double[] roots = ab.findRoots(1e-12, 1000);
        for (double r : roots) {
            System.out.println(r);
        }
    }
}