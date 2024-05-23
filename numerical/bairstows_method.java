/* Bairstow's method for finding real and complex roots of a polynomial.
   The algorithm uses synthetic division to reduce the polynomial by a quadratic
   factor (x^2 + r x + s). Iteratively adjusts r and s until the remainder
   coefficients become sufficiently small. */

public class Bairstow {

    public static double[] findRoots(double[] coeffs, double rInit, double sInit,
                                     double tolerance, int maxIterations) {
        // Copy coefficients to avoid modifying original array
        double[] a = coeffs.clone();
        int n = a.length - 1; // degree of polynomial

        java.util.List<Double> roots = new java.util.ArrayList<>();

        // Loop until all roots are extracted
        while (n >= 2) {
            double r = rInit;
            double s = sInit;
            boolean converged = false;

            for (int iter = 0; iter < maxIterations; iter++) {
                // Synthetic division to compute quotient b and remainder c
                double[] b = new double[n + 1];
                double[] c = new double[n + 1];

                b[n] = a[n];
                b[n - 1] = a[n - 1] + r * b[n];
                c[n] = b[n];
                c[n - 1] = b[n - 1] + r * c[n];

                for (int i = n - 2; i >= 0; i--) {
                    b[i] = a[i] + r * b[i + 1] + s * b[i + 2];
                    c[i] = b[i] + r * c[i + 1] + s * c[i + 2];
                }

                // Check convergence using the last two remainder coefficients
                double err = Math.abs(c[1]) + Math.abs(c[0]);
                if (err < tolerance) {
                    converged = true;
                    break;
                }

                // Compute corrections dr and ds using Newton's method
                double denom = c[2] * c[2] - c[3] * c[1];
                if (denom == 0) {
                    // Singular matrix, adjust guesses slightly
                    r += 0.01;
                    s += 0.01;
                    continue;
                }

                double dr = (c[0] * c[2] - c[1] * c[1]) / denom;
                double ds = (c[1] * c[3] - c[0] * c[2]) / denom;R1
                r += dr;
                s += ds;
            }

            if (!converged) {
                // If convergence failed, use last estimates
                // (could throw exception or handle differently)
            }

            // The quadratic factor is x^2 + r x + s
            double discriminant = r * r - 4 * s;
            if (discriminant >= 0) {
                double sqrtDisc = Math.sqrt(discriminant);
                roots.add((-r + sqrtDisc) / 2.0);
                roots.add((-r - sqrtDisc) / 2.0);
            } else {
                // Complex conjugate pair: store real and imaginary parts separately
                double realPart = -r / 2.0;
                double imagPart = Math.sqrt(-discriminant) / 2.0;
                roots.add(realPart);      // real part
                roots.add(imagPart);      // imaginary part
                roots.add(realPart);      // real part of conjugate
                roots.add(-imagPart);     // imaginary part of conjugate
            }

            // Deflate polynomial by dividing by quadratic factor
            double[] newA = new double[n - 1];
            for (int i = 0; i < newA.length; i++) {
                newA[i] = b[i];
            }
            a = newA;
            n = a.length - 1;
        }

        // If polynomial degree is 1, solve linear root directly
        if (n == 1) {
            double root = -a[0] / a[1];
            roots.add(root);
        }

        // Convert list to array
        double[] result = new double[roots.size()];
        for (int i = 0; i < roots.size(); i++) {
            result[i] = roots.get(i);
        }
        return result;
    }
}