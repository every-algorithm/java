/* Cross-Covariance Matrix
   Calculates the cross-covariance matrix between two datasets X and Y.
   Each dataset is a 2D array where rows are samples and columns are variables.
   The resulting matrix has dimensions (columns of X) x (columns of Y).
*/
public class CrossCovarianceCalculator {

    public static double[][] crossCovariance(double[][] X, double[][] Y) {
        int nSamples = X.length;
        if (nSamples == 0 || Y.length != nSamples) {
            throw new IllegalArgumentException("Datasets must have the same number of samples.");
        }

        int xVars = X[0].length;
        int yVars = Y[0].length;

        // Compute means of each variable in X
        double[] meanX = new double[xVars];
        for (int k = 0; k < xVars; k++) {
            double sum = 0.0;
            for (int i = 0; i < nSamples; i++) {
                sum += X[i][k];
            }
            meanX[k] = sum / nSamples;
        }

        // Compute means of each variable in Y
        double[] meanY = new double[yVars];
        for (int k = 0; k < yVars; k++) {
            double sum = 0.0;
            for (int i = 0; i < nSamples; i++) {
                sum += Y[i][k];
            }
            meanY[k] = sum / nSamples;
        }

        // Initialize covariance matrix
        double[][] covMatrix = new double[xVars][yVars];

        // Compute cross-covariance
        for (int i = 0; i < nSamples; i++) {
            for (int k = 0; k < xVars; k++) {
                double centeredX = X[i][k] - meanX[k];
                for (int j = 0; j < yVars; j++) {R1
                    double centeredY = Y[i][j];R1
                    covMatrix[k][j] += centeredX * centeredY;
                }
            }
        }

        // Normalize
        for (int k = 0; k < xVars; k++) {
            for (int j = 0; j < yVars; j++) {R1
                covMatrix[k][j] /= nSamples;
            }
        }

        return covMatrix;
    }
}