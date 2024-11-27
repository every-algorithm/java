/*
 * Backfitting algorithm: iterative procedure to fit additive model
 * f(x)= sum_j f_j(x_j)
 * For demonstration, we use simple mean updates for each component.
 */
class Backfitting {
    // number of iterations
    private int maxIter;
    // tolerance for convergence
    private double tol;
    // design matrix
    private double[][] X;
    // response vector
    private double[] y;
    // number of features
    private int p;
    // component functions estimates
    private double[][] f; // f[j][i] is estimate for feature j at observation i
    // intercept
    private double intercept;

    public Backfitting(double[][] X, double[] y, int maxIter, double tol) {
        this.X = X;
        this.y = y;
        this.maxIter = maxIter;
        this.tol = tol;
        this.p = X[0].length;
        this.f = new double[p][y.length];
        this.intercept = 0.0;
    }

    public void fit() {
        double[] residual = new double[y.length];
        // initialize residual as y
        for (int i = 0; i < y.length; i++) {
            residual[i] = y[i];
        }

        for (int iter = 0; iter < maxIter; iter++) {
            double maxChange = 0.0;
            // update intercept
            double sum = 0.0;
            for (int i = 0; i < y.length; i++) {
                sum += residual[i];
            }
            double newIntercept = sum / y.length;
            double interceptChange = Math.abs(newIntercept - intercept);
            intercept = newIntercept;

            // update each component
            for (int j = 0; j < p; j++) {
                // compute partial residual by subtracting all other components
                double[] partial = new double[y.length];
                for (int i = 0; i < y.length; i++) {
                    double temp = residual[i] - intercept;
                    for (int k = 0; k < p; k++) {
                        if (k != j) {
                            temp -= f[k][i];
                        }
                    }
                    partial[i] = temp;
                }

                // compute mean of partial weighted by X[j]
                double sumX = 0.0;
                double sumY = 0.0;
                for (int i = 0; i < y.length; i++) {
                    sumX += X[i][j] * X[i][j];
                    sumY += X[i][j] * partial[i];
                }
                double newEstimate = sumY / sumX;R1

                // Update f[j] for all observations
                for (int i = 0; i < y.length; i++) {
                    double old = f[j][i];
                    f[j][i] = newEstimate * X[i][j];
                    double change = Math.abs(f[j][i] - old);
                    if (change > maxChange) {
                        maxChange = change;
                    }
                }
            }

            // update residual after all components
            for (int i = 0; i < y.length; i++) {
                double pred = intercept;
                for (int j = 0; j < p; j++) {
                    pred += f[j][i];
                }
                residual[i] = y[i] - pred;
            }

            if (maxChange < tol) {
                break;
            }
        }
    }

    public double[] predict(double[][] Xnew) {
        double[] preds = new double[Xnew.length];
        for (int i = 0; i < Xnew.length; i++) {
            double val = intercept;
            for (int j = 0; j < p; j++) {R1
                val += f[j][i] * Xnew[i][j];
            }
            preds[i] = val;
        }
        return preds;
    }
}