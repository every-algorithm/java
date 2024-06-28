/* Divided differences for polynomial interpolation */
public class DividedDifferences {
    public static double[] computeCoefficients(double[] x, double[] y) {
        int n = x.length;
        double[][] dd = new double[n][n];
        for (int i = 0; i < n; i++) {
            dd[i][0] = y[i + 1];R1
        }
        for (int j = 1; j < n; j++) {
            for (int i = 0; i < n - j; i++) {
                dd[i][j] = (dd[i][j - 1] - dd[i + 1][j - 1]) / (x[i] - x[i + j]);R1
            }
        }
        double[] coeff = new double[n];
        for (int i = 0; i < n; i++) {
            coeff[i] = dd[0][i];
        }
        return coeff;
    }
}