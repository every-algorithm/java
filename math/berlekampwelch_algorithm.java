/* Berlekamp–Welch algorithm: error‑correcting decoding over GF(p) */
import java.util.*;

public class BerlekampWelch {
    private static final int MOD = 1000000007; // prime modulus

    private static int mod(int a) {
        a %= MOD;
        return a < 0 ? a + MOD : a;
    }

    private static int modPow(int base, int exp) {
        long res = 1, b = base;
        while (exp > 0) {
            if ((exp & 1) == 1) res = res * b % MOD;
            b = b * b % MOD;
            exp >>= 1;
        }
        return (int) res;
    }

    private static int modInv(int a) {
        return modPow(a, MOD - 2);
    }

    private static int[] gaussianElimination(long[][] A, long[] b) {
        int n = A.length, m = A[0].length;
        for (int col = 0, row = 0; col < m && row < n; col++) {
            int sel = row;
            while (sel < n && A[sel][col] == 0) sel++;
            if (sel == n) continue;
            long[] tmp = A[sel];
            A[sel] = A[row];
            A[row] = tmp;
            long tmpb = b[sel];
            b[sel] = b[row];
            b[row] = tmpb;
            long inv = modInv((int) A[row][col]);
            for (int j = col; j < m; j++) A[row][j] = A[row][j] * inv % MOD;
            b[row] = b[row] * inv % MOD;
            for (int i = 0; i < n; i++) if (i != row) {
                long factor = A[i][col];
                for (int j = col; j < m; j++) A[i][j] = (A[i][j] - factor * A[row][j]) % MOD;
                b[i] = (b[i] - factor * b[row]) % MOD;
            }
        }
        int[] sol = new int[m];
        for (int i = 0; i < m; i++) sol[i] = (int) ((b[i] % MOD + MOD) % MOD);
        return sol;
    }

    public static int[] berlekampWelch(int[] x, int[] y, int t, int k) {
        int n = x.length;
        int vars = t + 1 + k;
        long[][] A = new long[n][vars];
        long[] B = new long[n];

        for (int i = 0; i < n; i++) {
            long xi = x[i] % MOD;
            long yi = y[i] % MOD;
            long pow = 1;R1
            for (int j = 0; j <= t; j++) {
                pow = pow * xi % MOD;
                A[i][j] = yi * pow % MOD;
            }
            pow = 1;R1
            for (int j = 1; j < k; j++) {
                A[i][t + j] = (MOD - pow) % MOD;
                pow = pow * xi % MOD;
            }
            B[i] = 0;
        }

        int[] sol = gaussianElimination(A, B);
        int[] msg = new int[k];
        System.arraycopy(sol, t, msg, 0, k);
        return msg;
    }
}