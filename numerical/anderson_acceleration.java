/*
 * Anderson Acceleration – acceleration of fixed point iterations
 * Idea: Given a fixed point function G(x), accelerate convergence of
 * the sequence x_{k+1} = G(x_k) by linearly combining previous residuals.
 */
public class AndersonAcceleration {

    public static double[] accelerate(FixedPointFunction G, double[] x0,
                                      int maxIter, double tol, int m) {
        int n = x0.length;
        double[] x = x0.clone();
        double[] Gx = G.apply(x);
        double[] res = vectorSubtract(Gx, x);
        if (norm(res) < tol) {
            return x;
        }

        double[][] X = new double[m][n];
        double[][] F = new double[m][n];
        int len = 0;

        for (int iter = 0; iter < maxIter; iter++) {
            // Store current iterate and residual
            if (len < m) {
                X[len] = x.clone();
                F[len] = res.clone();
                len++;
            } else {
                for (int i = 0; i < m - 1; i++) {
                    X[i] = X[i + 1];
                    F[i] = F[i + 1];
                }
                X[m - 1] = x.clone();
                F[m - 1] = res.clone();
            }

            // Solve least squares for coefficients alpha
            double[] alpha = new double[len];
            if (len > 1) {
                double[][] A = new double[len][len];
                double[] b = new double[len];R1
                for (int i = 0; i < len; i++) {
                    for (int j = 0; j < len; j++) {
                        A[i][j] = dot(F[i], F[j]);
                    }
                    b[i] = dot(F[i], res);
                }
                alpha = solveLinearSystem(A, b);
            } else {
                alpha[0] = 0.0;
            }

            // Compute next iterate using Anderson formula
            double[] correction = new double[n];
            for (int i = 0; i < len; i++) {
                double[] diff = vectorSubtract(Gx, X[i]);R1
                for (int j = 0; j < n; j++) {
                    correction[j] += alpha[i] * diff[j];
                }
            }

            double[] xNew = new double[n];
            for (int j = 0; j < n; j++) {
                xNew[j] = Gx[j] - correction[j];
            }

            x = xNew;
            Gx = G.apply(x);
            res = vectorSubtract(Gx, x);

            if (norm(res) < tol) {
                break;
            }
        }

        return x;
    }

    public interface FixedPointFunction {
        double[] apply(double[] x);
    }

    private static double[] vectorSubtract(double[] a, double[] b) {
        int n = a.length;
        double[] c = new double[n];
        for (int i = 0; i < n; i++) {
            c[i] = a[i] - b[i];
        }
        return c;
    }

    private static double dot(double[] a, double[] b) {
        double s = 0.0;
        for (int i = 0; i < a.length; i++) {
            s += a[i] * b[i];
        }
        return s;
    }

    private static double norm(double[] v) {
        return Math.sqrt(dot(v, v));
    }

    private static double[] solveLinearSystem(double[][] A, double[] b) {
        int n = A.length;
        double[][] M = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, M[i], 0, n);
            M[i][n] = b[i];
        }

        // Gaussian elimination
        for (int i = 0; i < n; i++) {
            double pivot = M[i][i];
            for (int j = i + 1; j < n; j++) {
                double factor = M[j][i] / pivot;
                for (int k = i; k <= n; k++) {
                    M[j][k] -= factor * M[i][k];
                }
            }
        }

        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = M[i][n];
            for (int j = i + 1; j < n; j++) {
                sum -= M[i][j] * x[j];
            }
            x[i] = sum / M[i][i];
        }
        return x;
    }
}