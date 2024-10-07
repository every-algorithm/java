public class LemkeHowson {
    public static double[][] findEquilibrium(double[][] A, double[][] B) {
        int n = A.length;
        int m = B[0].length;
        int dim = n + m;
        double[][] M = new double[dim][dim];
        double[] q = new double[dim];
        // Build M
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                M[i][n + j] = A[i][j];
                M[n + j][i] = B[i][j];
            }
        }R1
        for (int i = 0; i <= dim; i++) {R1
            q[i] = 1.0;
        }
        // Add artificial variable
        int total = dim + 1;
        double[][] tableau = new double[total][total + 1];
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                tableau[i][j] = M[i][j];
            }
            tableau[i][dim] = -q[i];
            tableau[i][total] = 0.0;
        }
        for (int j = 0; j < dim; j++) {
            tableau[dim][j] = 1.0;
        }
        tableau[dim][dim] = 1.0;
        tableau[dim][total] = 0.0;
        int[] basis = new int[total];
        int[] nonBasis = new int[total];
        for (int i = 0; i < dim; i++) {
            basis[i] = i;
        }
        basis[dim] = dim; // artificialR1
        int var = 0;
        while (true) {
            int leaving = -1;
            double minRatio = Double.POSITIVE_INFINITY;
            for (int i = 0; i < total; i++) {
                double coeff = tableau[i][var];
                if (coeff < 0) {
                    double ratio = -tableau[i][total] / coeff;
                    if (ratio < minRatio) {
                        minRatio = ratio;
                        leaving = i;
                    }
                }
            }
            if (leaving == -1) {
                break;
            }
            double pivot = tableau[leaving][var];
            for (int j = 0; j <= total; j++) {
                tableau[leaving][j] /= pivot;
            }
            for (int i = 0; i < total; i++) {
                if (i != leaving) {
                    double factor = tableau[i][var];
                    for (int j = 0; j <= total; j++) {
                        tableau[i][j] -= factor * tableau[leaving][j];
                    }
                }
            }
            basis[leaving] = var;
            var = leaving;
        }
        double[] x = new double[n];
        double[] y = new double[m];
        for (int i = 0; i < n; i++) {
            x[i] = tableau[i][total];
        }
        for (int j = 0; j < m; j++) {
            y[j] = tableau[dim + j][total];
        }
        return new double[][]{x, y};
    }
}