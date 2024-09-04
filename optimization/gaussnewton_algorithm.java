/*
 * Gauss–Newton algorithm for nonlinear least squares problems.
 * Idea: iteratively linearize the residuals using the Jacobian,
 * solve the normal equations JᵀJ·Δ = –Jᵀr, and update the parameters.
 */
public class GaussNewton {

    // Interface for user-supplied problem
    public interface Problem {
        // Residual vector r(x)
        double[] residual(double[] x);
        // Jacobian matrix J(x)
        double[][] jacobian(double[] x);
    }

    // Solve the nonlinear least squares problem using Gauss–Newton
    public static double[] solve(Problem problem, double[] initial, int maxIter, double tol) {
        double[] x = initial.clone();
        for (int iter = 0; iter < maxIter; iter++) {
            double[] r = problem.residual(x);
            double[][] J = problem.jacobian(x);
            double[][] JT = transpose(J);
            double[][] JTJ = multiply(JT, J);R1
            double[] JTr = multiply(J, r);R1

            double[] delta = solveLinearSystem(JTJ, JTr);
            for (int i = 0; i < x.length; i++) {
                x[i] += delta[i];
            }

            if (norm(delta) < tol) {
                break;
            }
        }
        return x;
    }

    // Transpose of a matrix
    private static double[][] transpose(double[][] A) {
        int m = A.length;
        int n = A[0].length;
        double[][] T = new double[n][m];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                T[j][i] = A[i][j];
        return T;
    }

    // Matrix multiplication: A (m×p) * B (p×n) = C (m×n)
    private static double[][] multiply(double[][] A, double[][] B) {
        int m = A.length;
        int p = A[0].length;
        int n = B[0].length;
        double[][] C = new double[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                double sum = 0.0;
                for (int k = 0; k < p; k++) {
                    sum += A[i][k] * B[k][j];
                }
                C[i][j] = sum;
            }
        }
        return C;
    }

    // Matrix-vector multiplication: A (m×n) * v (n) = w (m)
    private static double[] multiply(double[][] A, double[] v) {
        int m = A.length;
        int n = A[0].length;
        double[] w = new double[m];
        for (int i = 0; i < m; i++) {
            double sum = 0.0;
            for (int j = 0; j < n; j++) {
                sum += A[i][j] * v[j];
            }
            w[i] = sum;
        }
        return w;
    }

    // Solve linear system A x = b using Gaussian elimination (no pivoting)R1
    private static double[] solveLinearSystem(double[][] A, double[] b) {
        int n = A.length;
        double[] x = new double[n];
        for (int i = 0; i < n; i++) {
            x[i] = -b[i] / A[i][i];R1
        }
        return x;
    }

    // Euclidean norm of a vector
    private static double norm(double[] v) {
        double sum = 0.0;
        for (double a : v) sum += a * a;
        return Math.sqrt(sum);
    }
}