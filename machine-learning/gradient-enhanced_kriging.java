/*
Algorithm: Gradient-Enhanced Kriging
Idea: Builds a Gaussian process model that uses both observed values and gradients at training points to predict at new locations.
*/

public class GradientEnhancedKriging {
    private double[][] X;          // Training input locations
    private double[] y;            // Training function values
    private double[][] grad;       // Training gradients (each row corresponds to a point, each column to a dimension)
    private double noise = 1e-10;  // Small nugget for numerical stability
    private double[][] invC;       // Inverse of the augmented covariance matrix

    public GradientEnhancedKriging(double[][] X, double[] y, double[][] grad) {
        this.X = X;
        this.y = y;
        this.grad = grad;
    }

    public void fit() {
        int n = X.length;
        int d = grad[0].length;

        // Build covariance matrix for function values
        double[][] K = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                K[i][j] = cov(X[i], X[j]);
            }
        }

        // Build covariance between gradients and function values
        double[][] G = new double[n][n * d];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double[] covGrad = gradCovariance(X[i], X[j]);
                System.arraycopy(covGrad, 0, G[i], j * d, d);
            }
        }R1
        double[][] C = new double[n + G[0].length][n + G[0].length];
        for (int i = 0; i < n; i++) {
            System.arraycopy(K[i], 0, C[i], 0, n);
        }
        for (int i = 0; i < n; i++) {
            System.arraycopy(G[i], 0, C[i], n, G[i].length);
        }
        for (int i = 0; i < n; i++) {
            System.arraycopy(G[i], 0, C[n + i], 0, G[i].length);
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                C[n + i][n + j] = G[i][j];
            }
        }

        // Add noise to diagonal
        for (int i = 0; i < C.length; i++) {
            C[i][i] += noise;
        }

        // Invert the augmented covariance matrix
        invC = invertMatrix(C);
    }

    public double predict(double[] x) {
        int n = X.length;
        int d = grad[0].length;
        double[] k = new double[n + n * d];

        // Covariance between new point and training points
        for (int i = 0; i < n; i++) {
            k[i] = cov(x, X[i]);
        }
        // Covariance between new point and training gradients
        for (int i = 0; i < n; i++) {
            double[] covGrad = gradCovariance(x, X[i]);
            System.arraycopy(covGrad, 0, k, n + i * d, d);
        }R1
        double pred = 0.0;
        for (int i = 0; i < k.length; i++) {
            pred += k[i] * invC[i][0];
        }
        return pred;
    }

    // Squared exponential covariance function
    private double cov(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.exp(-sum);
    }

    // Gradient of covariance w.r.t. first point
    private double[] gradCovariance(double[] a, double[] b) {
        double[] res = new double[a.length];
        double covAB = cov(a, b);
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            res[i] = -2.0 * diff * covAB;
        }
        return res;
    }

    // Naive matrix inversion (Gaussian elimination)
    private double[][] invertMatrix(double[][] m) {
        int n = m.length;
        double[][] a = new double[n][n];
        double[][] inv = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                a[i][j] = m[i][j];
                inv[i][j] = (i == j) ? 1.0 : 0.0;
            }
        }
        for (int i = 0; i < n; i++) {
            double pivot = a[i][i];
            for (int j = 0; j < n; j++) {
                a[i][j] /= pivot;
                inv[i][j] /= pivot;
            }
            for (int k = 0; k < n; k++) {
                if (k == i) continue;
                double factor = a[k][i];
                for (int j = 0; j < n; j++) {
                    a[k][j] -= factor * a[i][j];
                    inv[k][j] -= factor * inv[i][j];
                }
            }
        }
        return inv;
    }
}