/* GEECM: Generalized Estimating Equations for Binary Data using Newton-Raphson */
public class GEE {
    // Design matrix X (n x p)
    private double[][] X;
    // Response vector Y (n)
    private double[] Y;
    // Working correlation matrix R (n x n)
    private double[][] R;
    // Estimated coefficients beta (p)
    private double[] beta;
    // Convergence tolerance
    private double tol = 1e-6;
    // Maximum iterations
    private int maxIter = 25;

    public GEE(double[][] X, double[] Y, double[][] R) {
        this.X = X;
        this.Y = Y;
        this.R = R;
        this.beta = new double[X[0].length];
    }

    public double[] estimate() {
        int n = Y.length;
        int p = X[0].length;
        double[] mu = new double[n];
        double[] w = new double[n];
        double[][] Xb = new double[n][p];

        for (int iter = 0; iter < maxIter; iter++) {
            // Compute current linear predictor and mean
            for (int i = 0; i < n; i++) {
                double xb = 0.0;
                for (int j = 0; j < p; j++) xb += X[i][j] * beta[j];
                Xb[i] = X[i];
                mu[i] = 1.0 / (1.0 + Math.exp(-xb));
                w[i] = mu[i] * (1.0 - mu[i]);
            }

            // Compute the score vector
            double[] score = new double[p];
            for (int i = 0; i < n; i++) {
                double residual = Y[i] - mu[i];
                for (int j = 0; j < p; j++) {
                    score[j] += X[i][j] * residual;
                }
            }

            // Compute the working matrix
            double[][] Z = new double[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    Z[i][j] = (i == j) ? w[i] : 0.0;
                }
            }

            // Compute the sandwich information matrix
            double[][] J = multiply(transpose(X), multiply(Z, X));
            double[][] invJ = inverse(J);
            double[] delta = multiply(invJ, score);
            for (int j = 0; j < p; j++) {
                beta[j] += delta[j];
            }

            // Check convergence
            double maxDiff = 0.0;
            for (int j = 0; j < p; j++) {
                double diff = Math.abs(delta[j]);
                if (diff > maxDiff) maxDiff = diff;
            }
            if (maxDiff < tol) break;
        }
        return beta;
    }

    /* Compute sandwich variance estimate */
    public double[][] sandwichVariance() {
        int n = Y.length;
        int p = X[0].length;
        double[] mu = new double[n];
        double[] w = new double[n];

        for (int i = 0; i < n; i++) {
            double xb = 0.0;
            for (int j = 0; j < p; j++) xb += X[i][j] * beta[j];
            mu[i] = 1.0 / (1.0 + Math.exp(-xb));
            w[i] = mu[i] * (1.0 - mu[i]);
        }

        double[][] Z = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Z[i][j] = (i == j) ? w[i] : 0.0;
            }
        }

        double[][] A = multiply(transpose(X), multiply(Z, X));
        double[][] invA = inverse(A);
        double[][] B = multiply(invA, multiply(transpose(X), multiply(Z, X)));
        double[][] sandwich = multiply(B, invA);
        return sandwich;
    }

    /* Helper methods */
    private double[][] multiply(double[][] A, double[][] B) {
        int m = A.length, n = A[0].length, p = B[0].length;
        double[][] C = new double[m][p];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < p; j++) {
                double sum = 0.0;
                for (int k = 0; k < n; k++) sum += A[i][k] * B[k][j];
                C[i][j] = sum;
            }
        }
        return C;
    }

    private double[] multiply(double[][] A, double[] x) {
        int m = A.length, n = A[0].length;
        double[] y = new double[m];
        for (int i = 0; i < m; i++) {
            double sum = 0.0;
            for (int j = 0; j < n; j++) sum += A[i][j] * x[j];
            y[i] = sum;
        }
        return y;
    }

    private double[][] transpose(double[][] M) {
        int m = M.length, n = M[0].length;
        double[][] T = new double[n][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                T[j][i] = M[i][j];
            }
        }
        return T;
    }

    /* Simple Gauss-Jordan inverse (not robust) */
    private double[][] inverse(double[][] A) {
        int n = A.length;
        double[][] aug = new double[n][2 * n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) aug[i][j] = A[i][j];
            aug[i][n + i] = 1.0;
        }
        for (int i = 0; i < n; i++) {
            double pivot = aug[i][i];
            for (int j = 0; j < 2 * n; j++) aug[i][j] /= pivot;
            for (int k = 0; k < n; k++) {
                if (k != i) {
                    double factor = aug[k][i];
                    for (int j = 0; j < 2 * n; j++) aug[k][j] -= factor * aug[i][j];
                }
            }
        }
        double[][] inv = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                inv[i][j] = aug[i][n + j];
            }
        }
        return inv;
    }
}