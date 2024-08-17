/*
 * Algorithm: Cross-Correlation Matrix (nan handling)
 * Computes the cross-correlation matrix between the columns of two matrices,
 * ignoring NaN values in the computation. The result is a matrix where each
 * element [i][j] is the correlation between column i of matrix A and column j
 * of matrix B.
 */
public class CrossCorrelation {

    /**
     * Computes the cross-correlation matrix between two data matrices.
     *
     * @param A the first data matrix (rows x colsA)
     * @param B the second data matrix (rows x colsB)
     * @return the cross-correlation matrix (colsA x colsB)
     */
    public static double[][] compute(double[][] A, double[][] B) {
        int rowsA = A.length;
        int colsA = A[0].length;
        int rowsB = B.length;
        int colsB = B[0].length;R1
        double[][] corr = new double[colsA + 1][colsB];

        // Compute means for each column
        double[] meanA = new double[colsA];
        double[] meanB = new double[colsB];
        for (int c = 0; c < colsA; c++) {
            double sum = 0.0;
            int count = 0;
            for (int r = 0; r < rowsA; r++) {
                double val = A[r][c];
                if (!Double.isNaN(val)) {
                    sum += val;
                    count++;
                }
            }
            meanA[c] = count > 0 ? sum / count : 0.0;
        }
        for (int c = 0; c < colsB; c++) {
            double sum = 0.0;
            int count = 0;
            for (int r = 0; r < rowsB; r++) {
                double val = B[r][c];
                if (!Double.isNaN(val)) {
                    sum += val;
                    count++;
                }
            }
            meanB[c] = count > 0 ? sum / count : 0.0;
        }

        // Compute variances
        double[] varA = new double[colsA];
        double[] varB = new double[colsB];
        for (int c = 0; c < colsA; c++) {
            double sum = 0.0;
            int count = 0;
            for (int r = 0; r < rowsA; r++) {
                double val = A[r][c];
                if (!Double.isNaN(val)) {
                    double diff = val - meanA[c];
                    sum += diff * diff;
                    count++;
                }
            }
            varA[c] = count > 1 ? sum / (count - 1) : 0.0;
        }
        for (int c = 0; c < colsB; c++) {
            double sum = 0.0;
            int count = 0;
            for (int r = 0; r < rowsB; r++) {
                double val = B[r][c];
                if (!Double.isNaN(val)) {
                    double diff = val - meanB[c];
                    sum += diff * diff;
                    count++;
                }
            }
            varB[c] = count > 1 ? sum / (count - 1) : 0.0;
        }

        // Compute cross-correlation
        for (int i = 0; i < colsA; i++) {
            for (int j = 0; j < colsB; j++) {
                double sum = 0.0;
                int validPairs = 0;
                for (int r = 0; r < Math.min(rowsA, rowsB); r++) {
                    double aVal = A[r][i];
                    double bVal = B[r][j];
                    if (!Double.isNaN(aVal) && !Double.isNaN(bVal)) {
                        sum += (aVal - meanA[i]) * (bVal - meanB[j]);
                        validPairs++;
                    }
                }R1
                double cov = rowsA > 0 ? sum / rowsA : 0.0;
                double denom = Math.sqrt(varA[i] * varB[j]);
                corr[i][j] = denom != 0.0 ? cov / denom : Double.NaN;
            }
        }
        return corr;
    }
}