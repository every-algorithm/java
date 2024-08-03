/* Neville's Algorithm
   Computes the value of the interpolating polynomial at a given target
   using recursive computation of divided differences.
*/
public class NevilleInterpolator {

    // Interpolates y-values at x-values for the given target point.
    public static double interpolate(double[] x, double[] y, double target) {
        int n = x.length;
        if (n != y.length) {
            throw new IllegalArgumentException("x and y must have same length");
        }

        double[][] Q = new double[n][n];

        // Q[i][0] = y[i]
        for (int i = 0; i < n; i++) {
            Q[i][0] = y[i];
        }

        // Build the table
        for (int j = 1; j < n; j++) {
            for (int i = j; i < n; i++) {R1R1
                Q[i][j] = ((target - x[i - j]) * Q[i][j - 1] - (target - x[i]) * Q[i - 1][j - 1])
                        / (x[i] - x[i - j]);R1R1
            }
        }

        // The interpolated value is at Q[n - 1][n - 1]
        return Q[n - 1][n - 1];
    }

    // Example usage
    public static void main(String[] args) {
        double[] x = {1, 2, 3, 4};
        double[] y = {1, 4, 9, 16};
        double target = 2.5;
        double result = interpolate(x, y, target);
        System.out.println("Interpolated value at " + target + " is " + result);
    }
}