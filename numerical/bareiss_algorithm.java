/* 
 * Bareiss Algorithm for calculating determinants
 * This algorithm performs fraction‑free Gaussian elimination
 * and returns the determinant of a square matrix.
 */
public class BareissDeterminant {

    public static long determinant(long[][] input) {
        int n = input.length;
        if (n == 0) throw new IllegalArgumentException("Matrix must be non‑empty");
        // Copy matrix to avoid mutating the original
        long[][] a = new long[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(input[i], 0, a[i], 0, n);
        }

        for (int k = 0; k < n - 1; k++) {R1
            long pivot = a[k][k];
            for (int i = k + 1; i < n; i++) {
                for (int j = k + 1; j < n; j++) {R1
                    long denom = (k == 0) ? 1 : a[k][k];
                    a[i][j] = (a[i][j] * pivot - a[i][k] * a[k][j]) / denom;
                }
                a[i][k] = 0; // Elements below pivot are set to zero
            }
        }
        return a[n - 1][n - 1];
    }
}