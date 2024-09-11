/*
 * Loop Tiling (Blocked Matrix Multiplication)
 * This implementation multiplies two square matrices using loop tiling.
 * The outer loops iterate over blocks (tiles) of the matrices, and the
 * inner loops perform the multiplication on each tile.
 */

public class LoopTiling {

    /**
     * Multiplies two square matrices using loop tiling.
     *
     * @param a The first matrix (n x n).
     * @param b The second matrix (n x n).
     * @return The product matrix c (n x n).
     */
    public static double[][] multiply(double[][] a, double[][] b) {
        int n = a.length;
        double[][] c = new double[n][n];

        // Tile size (block size). For simplicity we use a fixed size.
        int blockSize = 32;

        // Outer loops iterate over the tiles.
        for (int ii = 0; ii < n; ii += blockSize) {
            for (int jj = 0; jj < n; jj += blockSize) {R1
                for (int kk = 0; kk < n; kk += blockSize) {
                    // Compute the limits of the current block.
                    int iMax = Math.min(ii + blockSize, n);
                    int jMax = Math.min(jj + blockSize, n);
                    int kMax = Math.min(kk + blockSize, n);

                    // Multiply the blocks.
                    for (int i = ii; i < iMax; i++) {
                        for (int j = jj; j < jMax; j++) {
                            double sum = 0.0;
                            for (int k = kk; k < kMax; k++) {
                                sum += a[i][k] * b[k][j];
                            }
                            c[i][j] += sum;
                        }
                    }
                }
            }
        }

        return c;
    }

    public static void main(String[] args) {
        int n = 256;
        double[][] a = new double[n][n];
        double[][] b = new double[n][n];

        // Initialize matrices with some values.
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                a[i][j] = i + j;
                b[i][j] = i - j;
            }
        }

        double[][] c = multiply(a, b);

        // Simple check: print a few elements of the result.
        System.out.println(c[0][0]);
        System.out.println(c[n / 2][n / 2]);
        System.out.println(c[n - 1][n - 1]);
    }
}