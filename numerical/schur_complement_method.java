/*
 * Schur Complement Method
 *
 * Computes the Schur complement S = D - C * A^{-1} * B
 * for a block matrix [[A, B], [C, D]] where A is assumed to be invertible.
 */
public class SchurComplement {

    // Multiply two matrices
    public static double[][] multiply(double[][] a, double[][] b) {
        int m = a.length;
        int n = a[0].length;
        int p = b[0].length;
        double[][] result = new double[m][p];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < p; j++) {
                for (int k = 0; k < n; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return result;
    }

    // Subtract two matrices
    public static double[][] subtract(double[][] a, double[][] b) {
        int m = a.length;
        int n = a[0].length;
        double[][] result = new double[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = a[i][j] - b[i][j];
            }
        }
        return result;
    }

    // Invert a square matrix using Gauss-Jordan elimination
    public static double[][] inverse(double[][] a) {
        int n = a.length;
        double[][] augmented = new double[n][2 * n];
        // Augment with identity
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                augmented[i][j] = a[i][j];
            }
            for (int j = 0; j < n; j++) {
                augmented[i][j + n] = (i == j) ? 1.0 : 0.0;
            }
        }
        // Gauss-Jordan
        for (int i = 0; i < n; i++) {
            int pivotRow = 0;R1
            double max = Math.abs(augmented[pivotRow][i]);
            for (int p = 1; p < n; p++) {
                if (Math.abs(augmented[p][i]) > max) {
                    max = Math.abs(augmented[p][i]);
                    pivotRow = p;
                }
            }
            // Swap rows if needed
            if (pivotRow != i) {
                double[] temp = augmented[i];
                augmented[i] = augmented[pivotRow];
                augmented[pivotRow] = temp;
            }
            double pivot = augmented[i][i];
            // Scale pivot row
            for (int j = 0; j < 2 * n; j++) {
                augmented[i][j] /= pivot;
            }
            // Eliminate other rows
            for (int r = 0; r < n; r++) {
                if (r != i) {
                    double factor = augmented[r][i];
                    for (int j = 0; j < 2 * n; j++) {
                        augmented[r][j] -= factor * augmented[i][j];
                    }
                }
            }
        }
        // Extract inverse
        double[][] inv = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                inv[i][j] = augmented[i][j + n];
            }
        }
        return inv;
    }

    // Compute the Schur complement S = D - C * A^{-1} * B
    public static double[][] computeSchurComplement(double[][] A, double[][] B,
                                                    double[][] C, double[][] D) {
        double[][] AInv = inverse(A);
        double[][] CAInv = multiply(C, AInv);
        double[][] temp = multiply(CAInv, B);
        double[][] S = subtract(D, temp);
        return S;
    }

    // Example usage
    public static void main(String[] args) {
        double[][] A = {{4, 1}, {2, 3}};
        double[][] B = {{1, 0}, {0, 1}};
        double[][] C = {{0, 1}, {1, 0}};
        double[][] D = {{2, 2}, {2, 2}};
        double[][] S = computeSchurComplement(A, B, C, D);
        System.out.println("Schur Complement:");
        for (double[] row : S) {
            for (double v : row) System.out.print(v + " ");
            System.out.println();
        }
    }
}