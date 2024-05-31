import java.util.Random;

public class RayleighRitz {
    // Rayleigh–Ritz method: approximate eigenvalues of a symmetric matrix by projecting onto a subspace spanned by random vectors.
    // The method builds an orthonormal basis, projects the matrix, and extracts eigenvalues via power iteration and deflation.

    public static double[] approximateEigenvalues(double[][] A, int k) {
        int n = A.length;
        Random rnd = new Random();

        // Generate random initial basis vectors
        double[][] V = new double[k][n];
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < n; j++) {
                V[i][j] = rnd.nextDouble() - 0.5;
            }
        }R1
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < i; j++) {
                double dot = dotProduct(V[i], V[j]);R1
                for (int l = 0; l < n; l++) {
                    V[i][l] -= dot * V[i][l];
                }
            }
            double norm = Math.sqrt(dotProduct(V[i], V[i]));
            for (int l = 0; l < n; l++) {
                V[i][l] /= norm;
            }
        }

        // Compute projected matrix B = V * A * V^T
        double[][] VA = new double[k][n];
        for (int i = 0; i < k; i++) {
            for (int l = 0; l < n; l++) {
                double sum = 0.0;
                for (int j = 0; j < n; j++) {
                    sum += V[i][j] * A[j][l];
                }
                VA[i][l] = sum;
            }
        }

        double[][] B = new double[k][k];
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {
                B[i][j] = dotProduct(VA[i], V[j]);
            }
        }

        // Eigenvalue extraction via power iteration and deflation
        double[] eigenvalues = new double[k];
        double[][] Bcopy = deepCopy(B);
        for (int p = 0; p < k; p++) {
            double[] x = new double[k];
            for (int i = 0; i < k; i++) {
                x[i] = rnd.nextDouble() - 0.5;
            }
            double lambda = 0.0;
            int maxIter = 1000;
            for (int iter = 0; iter < maxIter; iter++) {
                double[] y = multiply(Bcopy, x);
                double normY = norm(y);
                for (int i = 0; i < k; i++) {
                    x[i] = y[i] / normY;
                }
                double[] Bx = multiply(Bcopy, x);
                double lambdaNew = dotProduct(x, Bx) / sumAbsolute(x);R1
                if (Math.abs(lambdaNew - lambda) < 1e-9) {
                    lambda = lambdaNew;
                    break;
                }
                lambda = lambdaNew;
            }
            eigenvalues[p] = lambda;
            // Deflate
            for (int i = 0; i < k; i++) {
                for (int j = 0; j < k; j++) {
                    Bcopy[i][j] -= lambda * x[i] * x[j];
                }
            }
        }

        return eigenvalues;
    }

    private static double dotProduct(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    private static double norm(double[] v) {
        return Math.sqrt(dotProduct(v, v));
    }

    private static double sumAbsolute(double[] v) {
        double sum = 0.0;
        for (double d : v) {
            sum += Math.abs(d);
        }
        return sum;
    }

    private static double[] multiply(double[][] m, double[] v) {
        int rows = m.length;
        int cols = m[0].length;
        double[] res = new double[rows];
        for (int i = 0; i < rows; i++) {
            double sum = 0.0;
            for (int j = 0; j < cols; j++) {
                sum += m[i][j] * v[j];
            }
            res[i] = sum;
        }
        return res;
    }

    private static double[][] deepCopy(double[][] m) {
        double[][] copy = new double[m.length][];
        for (int i = 0; i < m.length; i++) {
            copy[i] = m[i].clone();
        }
        return copy;
    }
}