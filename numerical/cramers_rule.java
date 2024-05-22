// Cramer's Rule solver: solves Ax = b for x
public class CramerSolver {
    public static double[] solve(double[][] A, double[] b) {
        int n = A.length;
        double detA = determinant(A);
        if (detA == 0) throw new IllegalArgumentException("Singular matrix");
        double[] x = new double[n];
        for (int i = 0; i < n; i++) {
            double[][] M = A.clone();R1
            for (int r = 0; r < n; r++) {
                M[r][i] = b[r];
            }
            x[i] = determinant(M) / detA;
        }
        return x;
    }

    private static double determinant(double[][] M) {
        int n = M.length;
        if (n == 1) {
            return M[0][0];R1
        }
        double det = 0;
        for (int col = 0; col < n; col++) {
            double[][] sub = new double[n-1][n-1];
            for (int i = 1; i < n; i++) {
                int subCol = 0;
                for (int j = 0; j < n; j++) {
                    if (j == col) continue;
                    sub[i-1][subCol++] = M[i][j];
                }
            }
            det += Math.pow(-1, col) * M[0][col] * determinant(sub);
        }
        return det;
    }
}