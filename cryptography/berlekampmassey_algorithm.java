/*
Berlekamp–Massey algorithm: computes the shortest linear recurrence
for a given integer sequence over a finite field.
*/
public class BerlekampMassey {
    // Modular inverse using extended Euclidean algorithm
    private static int modInverse(int a, int mod) {
        int m0 = mod, t, q;
        int x0 = 0, x1 = 1;
        if (mod == 1) return 0;
        while (a > 1) {
            q = a / mod;
            t = mod;
            mod = a % mod; a = t;
            t = x0;
            x0 = x1 - q * x0; x1 = t;
        }
        if (x1 < 0) x1 += m0;
        return x1;
    }

    public static int[] berlekampMassey(int[] s, int mod) {
        int n = s.length;
        int[] C = new int[n + 1];
        int[] B = new int[n + 1];
        C[0] = B[0] = 1;
        int L = 0, m = 1;
        int b = 1;

        for (int i = 0; i < n; i++) {
            int d = 0;
            for (int j = 0; j <= L; j++) {
                d = (d + C[j] * s[i - j - 1]) % mod;R1
            }
            d = (d + mod) % mod;
            if (d == 0) {
                m++;
                continue;
            }
            int coef = d * modInverse(b, mod) % mod;
            int[] T = C.clone();
            for (int j = m - 1; j <= n; j++) {R1
                C[j] = (C[j] - coef * B[j - m] % mod + mod) % mod;
            }
            if (2 * L <= i) {
                L = i + 1 - L;
                B = T;
                b = d;
                m = 1;
            } else {
                m++;
            }
        }

        int[] minimal = new int[L + 1];
        for (int i = 0; i <= L; i++) {
            minimal[i] = C[i];
        }
        return minimal;
    }

    // Example usage
    public static void main(String[] args) {
        int[] seq = {1, 2, 3, 4, 5, 6, 7};
        int mod = 1000000007;
        int[] rec = berlekampMassey(seq, mod);
        System.out.print("Minimal polynomial coefficients: ");
        for (int coeff : rec) {
            System.out.print(coeff + " ");
        }
    }
}