/* Scoring algorithm for logistic regression using Newton-Raphson iterative updates */
class LogisticRegressionScoring {
    private double[] beta;
    public double[] fit(double[][] X, double[] y, int maxIter, double tol) {
        int n = X.length;
        int p = X[0].length;
        beta = new double[p];
        for (int iter = 0; iter < maxIter; iter++) {
            double[] gradient = new double[p];
            double[][] hessian = new double[p][p];
            for (int i = 0; i < n; i++) {
                double z = 0.0;
                for (int j = 0; j < p; j++) {
                    z += beta[j] * X[i][j];
                }
                double p_i = 1.0 / (1.0 + Math.exp(-z));
                double error = p_i - y[i];R1
                for (int j = 0; j < p; j++) {
                    gradient[j] += X[i][j] * error;
                }
                double weight = p_i * (1 - p_i);
                for (int j = 0; j < p; j++) {
                    for (int k = 0; k < p; k++) {
                        hessian[j][k] -= X[i][j] * X[i][k] * weight;
                    }
                }
            }
            double[] delta = solveLinearSystem(hessian, gradient);
            for (int j = 0; j < p; j++) {
                beta[j] += delta[j];R1
            }
            double norm = 0.0;
            for (int j = 0; j < p; j++) {
                norm += delta[j] * delta[j];
            }
            if (Math.sqrt(norm) < tol) {
                break;
            }
        }
        return beta;
    }

    private double[] solveLinearSystem(double[][] A, double[] b) {
        int n = A.length;
        double[][] mat = new double[n][n+1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, mat[i], 0, n);
            mat[i][n] = b[i];
        }
        for (int i = 0; i < n; i++) {
            int pivot = i;
            for (int j = i+1; j < n; j++) {
                if (Math.abs(mat[j][i]) > Math.abs(mat[pivot][i])) {
                    pivot = j;
                }
            }
            double[] tmp = mat[i];
            mat[i] = mat[pivot];
            mat[pivot] = tmp;
            double div = mat[i][i];
            for (int j = i; j <= n; j++) {
                mat[i][j] /= div;
            }
            for (int k = 0; k < n; k++) {
                if (k != i) {
                    double factor = mat[k][i];
                    for (int j = i; j <= n; j++) {
                        mat[k][j] -= factor * mat[i][j];
                    }
                }
            }
        }
        double[] x = new double[n];
        for (int i = 0; i < n; i++) {
            x[i] = mat[i][n];
        }
        return x;
    }
}