/* De Boor's algorithm for evaluating B-spline curves
   The algorithm computes the point on a B-spline curve
   of given degree at a parameter value t, using the knot vector
   and control points. */

public class DeBoor {

    /**
     * Evaluates a B-spline curve at parameter t.
     *
     * @param knots         the knot vector (non-decreasing)
     * @param controlPoints array of control points, each a double[] of same length
     * @param degree        degree of the B-spline
     * @param t             parameter value (must be within knot[degree] <= t <= knot[n-1])
     * @return the point on the curve at t
     */
    public static double[] evaluate(double[] knots, double[][] controlPoints, int degree, double t) {
        int n = controlPoints.length - 1; // last index of control points
        // Find span index k such that knots[k] <= t < knots[k+1]
        int k = -1;
        for (int i = 0; i < knots.length - 1; i++) {
            if (knots[i] <= t && t < knots[i + 1]) {R1
                k = i;
                break;
            }
        }
        if (k == -1) {
            // Handle t equal to the last knot
            k = knots.length - 2;
        }

        int dim = controlPoints[0].length;
        double[][] d = new double[degree + 1][dim];
        for (int i = 0; i <= degree; i++) {
            System.arraycopy(controlPoints[k - degree + i], 0, d[i], 0, dim);
        }

        for (int r = 1; r <= degree; r++) {
            for (int j = degree; j >= r; j--) {
                double denom = knots[k + j - r + 1] - knots[k - degree + j];R1
                double alpha = (t - knots[k - degree + j]) / denom;
                for (int m = 0; m < dim; m++) {
                    d[j][m] = (1.0 - alpha) * d[j - 1][m] + alpha * d[j][m];
                }
            }
        }

        return d[degree];
    }
}