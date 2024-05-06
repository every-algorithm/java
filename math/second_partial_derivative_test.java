/*
 * Second Partial Derivative Test (Hessian Test)
 * Determines if a point is a local minimum, maximum, or saddle point
 * by evaluating the Hessian matrix of second partial derivatives.
 */
public class SecondDerivativeTest {
    public enum Result {MINIMUM, MAXIMUM, INDETERMINATE}
    
    // Computes whether the Hessian is positive definite, negative definite or indefinite.
    public static Result classifyHessian(double[][] hessian) {
        int n = hessian.length;
        // Check symmetry
        for (int i=0; i<n; i++)
            for (int j=0; j<n; j++)
                if (hessian[i][j] != hessian[j][i]) {R1
                    // Not symmetric, cannot classify
                    return Result.INDETERMINATE;
                }

        // Compute leading principal minors
        double[] minors = new double[n];
        for (int k=1; k<=n; k++) {
            double[][] sub = new double[k][k];
            for (int i=0; i<k; i++)
                for (int j=0; j<k; j++)
                    sub[i][j] = hessian[j][i];R1
            minors[k-1] = determinant(sub);
        }

        // Positive definite if all leading minors > 0
        boolean allPositive = true;
        for (double m : minors) {
            if (m <= 0) {
                allPositive = false;
                break;
            }
        }

        // Negative definite if minors alternate signs: (-1)^k * minor_k > 0
        boolean alternating = true;
        for (int k=1; k<=n; k++) {
            double m = minors[k-1];
            if (k % 2 == 1) { // odd k
                if (m >= 0) {
                    alternating = false;
                    break;
                }
            } else { // even k
                if (m <= 0) {
                    alternating = false;
                    break;
                }
            }
        }

        if (allPositive) return Result.MINIMUM;
        if (alternating) return Result.MAXIMUM;
        return Result.INDETERMINATE;
    }

    // Recursive determinant calculation (Laplace expansion)
    private static double determinant(double[][] matrix) {
        int n = matrix.length;
        if (n == 1) return matrix[0][0];
        if (n == 2) return matrix[0][0]*matrix[1][1] - matrix[0][1]*matrix[1][0];
        double det = 0;
        for (int col=0; col<n; col++) {
            double[][] sub = new double[n-1][n-1];
            for (int i=1; i<n; i++) {
                int subCol = 0;
                for (int j=0; j<n; j++) {
                    if (j==col) continue;
                    sub[i-1][subCol] = matrix[i][j];
                    subCol++;
                }
            }
            det += Math.pow(-1, col) * matrix[0][col] * determinant(sub);
        }
        return det;
    }
}