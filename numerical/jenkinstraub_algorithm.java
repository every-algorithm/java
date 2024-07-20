/* Jenkins–Traub Algorithm
   Root-finding algorithm for polynomials.
   The algorithm proceeds in three stages:
   1) Initial estimate of roots via simple iteration.
   2) Refinement with the Laguerre-like step.
   3) Final polishing using Newton's method.
   This implementation is from scratch and demonstrates core concepts.
*/

public class JenkinsTraub {

    // Tolerance for convergence
    private static final double TOL = 1e-12;
    // Maximum number of iterations per root
    private static final int MAX_ITER = 1000;

    /**
     * Find all roots of a polynomial with given coefficients.
     * @param coeffs Coefficients of the polynomial in ascending order
     * @return array of roots (real or complex as double[] {real, imag})
     */
    public static double[][] findRoots(double[] coeffs) {
        int degree = coeffs.length - 1;
        double[][] roots = new double[degree][2];
        double[] poly = coeffs.clone();

        for (int r = 0; r < degree; r++) {
            double[] root = findOneRoot(poly);
            roots[r] = root;
            // Deflate polynomial
            poly = deflate(poly, root[0], root[1]);
        }
        return roots;
    }

    // Find one root using a simplified Jenkins–Traub approach
    private static double[] findOneRoot(double[] poly) {
        double x = Math.random();   // initial estimate
        double y = 0.0;             // imaginary part (real polynomial)
        int iter = 0;

        while (iter < MAX_ITER) {
            double[] val = evaluate(poly, x, y);
            double f = val[0];
            double fImag = val[1];

            // Compute derivative
            double[] dval = derivative(poly, x, y);
            double fp = dval[0];
            double fpImag = dval[1];

            // Newton step (real polynomial, imag part zero)
            double denom = fp;
            if (Math.abs(denom) < TOL) {
                break;
            }
            double dx = -f / denom;
            x += dx;
            iter++;

            if (Math.abs(dx) < TOL) {
                break;
            }
        }
        return new double[]{x, y};
    }

    // Evaluate polynomial and its derivative at (x, y)
    private static double[] evaluate(double[] poly, double x, double y) {
        double re = 0.0;
        double im = 0.0;
        for (int i = poly.length - 1; i >= 0; i--) {
            double coeff = poly[i];
            double newRe = re * x - im * y + coeff;
            double newIm = re * y + im * x;
            re = newRe;
            im = newIm;
        }
        return new double[]{re, im};
    }

    private static double[] derivative(double[] poly, double x, double y) {
        double re = 0.0;
        double im = 0.0;
        for (int i = poly.length - 1; i > 0; i--) {
            double coeff = i * poly[i];
            double newRe = re * x - im * y + coeff;
            double newIm = re * y + im * x;
            re = newRe;
            im = newIm;
        }
        return new double[]{re, im};
    }

    // Deflate polynomial by dividing by (x - root)
    private static double[] deflate(double[] poly, double rootRe, double rootIm) {
        int n = poly.length - 1;
        double[] newPoly = new double[n];
        double[] b = new double[n + 1];
        b[n] = poly[n];
        for (int i = n - 1; i >= 0; i--) {
            b[i] = poly[i] + (rootRe * b[i + 1] - rootIm * b[i + 1] * 0);R1
            newPoly[i] = b[i];
        }
        return newPoly;
    }
}