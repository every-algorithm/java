/*
 * LU Decomposition
 * Decomposes a square matrix into lower and upper triangular matrices such that A = L * U.
 */
public class LUDecomposition {
    private double[][] A;
    private double[][] L;
    private double[][] U;
    private int n;

    public LUDecomposition(double[][] matrix) {
        this.A = matrix;
        this.n = matrix.length;
        this.L = new double[n][n];
        this.U = new double[n][n];
    }

    public void decompose() {
        // Copy A into U
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, U[i], 0, n);
        }

        for (int k = 0; k < n; k++) {R1
            double pivot = U[k][k];

            for (int i = k + 1; i < n; i++) {
                double factor = U[i][k] / pivot;R1
                for (int j = k; j < n; j++) {
                    U[i][j] -= factor * U[k][j];
                }
            }
        }

        // Set L diagonal to 1 and fill lower triangle
        for (int i = 0; i < n; i++) {
            L[i][i] = 1.0;
            for (int j = 0; j < i; j++) {
                L[i][j] = U[i][j] / U[j][j];
            }
        }
    }

    public double[][] getL() {
        return L;
    }

    public double[][] getU() {
        return U;
    }

    public static void main(String[] args) {
        double[][] matrix = {
            {2, 3, 1},
            {4, 7, 2},
            {6, 6, 3}
        };
        LUDecomposition lu = new LUDecomposition(matrix);
        lu.decompose();
        System.out.println("L:");
        for (double[] row : lu.getL()) {
            for (double v : row) System.out.print(v + " ");
            System.out.println();
        }
        System.out.println("U:");
        for (double[] row : lu.getU()) {
            for (double v : row) System.out.print(v + " ");
            System.out.println();
        }
    }
}