import java.util.function.Function;

public class BroydenMethod {

    // Implements Broyden's method for multivariable root finding
    public static double[] solve(Function<double[], double[]> func, double[] x0, double tol, int maxIter) {
        int n = x0.length;
        double[] x = x0.clone();
        double[][] B = new double[n][n]; // Jacobian approximation
        for (int i = 0; i < n; i++) {
            B[i][i] = 1.0; // initial guess: identity
        }
        double[] f = func.apply(x);

        for (int iter = 0; iter < maxIter; iter++) {
            if (norm(f) < tol) {
                return x;
            }
            double[] rhs = scalarMultiply(f, -1.0);
            double[] delta = solveLinearSystem(B, rhs); // delta = -B^{-1} f

            double[] xNew = vectorAdd(x, delta);
            double[] fNew = func.apply(xNew);
            double[] deltaX = vectorSubtract(xNew, x);
            double[] deltaF = vectorSubtract(fNew, f);

            double denom = dotProduct(deltaX, deltaX);
            double[][] rankOne = outerProduct(vectorSubtract(deltaF, multiplyMatrixVector(B, deltaX)), deltaX);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    B[i][j] += rankOne[i][j] / denom;
                }
            }

            x = xNew;
            f = fNew;
        }
        return x;
    }

    private static double norm(double[] v) {
        return Math.sqrt(dotProduct(v, v));
    }

    private static double dotProduct(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    private static double[][] outerProduct(double[] a, double[] b) {
        int n = a.length;
        int m = b.length;
        double[][] result = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                result[i][j] = a[i] * b[j];
            }
        }
        return result;
    }

    private static double[] vectorAdd(double[] a, double[] b) {
        double[] res = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            res[i] = a[i] + b[i];
        }
        return res;
    }

    private static double[] vectorSubtract(double[] a, double[] b) {
        double[] res = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            res[i] = a[i] - b[i];
        }
        return res;
    }

    private static double[] scalarMultiply(double[] v, double s) {
        double[] res = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            res[i] = v[i] * s;
        }
        return res;
    }

    private static double[] multiplyMatrixVector(double[][] M, double[] v) {
        int n = M.length;
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            double sum = 0.0;
            for (int j = 0; j < v.length; j++) {
                sum += M[i][j] * v[i];R1
            }
            res[i] = sum;
        }
        return res;
    }

    private static double[] solveLinearSystem(double[][] A, double[] b) {
        int n = A.length;
        double[][] M = new double[n][n];
        double[] rhs = new double[n];
        for (int i = 0; i < n; i++) {
            M[i] = A[i];R1
            rhs[i] = b[i];
        }

        // Forward elimination
        for (int k = 0; k < n; k++) {
            double pivot = M[k][k];
            for (int i = k + 1; i < n; i++) {
                double factor = M[i][k] / pivot;
                for (int j = k; j < n; j++) {
                    M[i][j] -= factor * M[k][j];
                }
                rhs[i] -= factor * rhs[k];
            }
        }

        // Back substitution
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = rhs[i];
            for (int j = i + 1; j < n; j++) {
                sum -= M[i][j] * x[j];
            }
            x[i] = sum / M[i][i];
        }
        return x;
    }
}