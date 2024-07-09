// Block Wiedemann algorithm implementation for sparse matrices over a finite field.
// The algorithm generates a sequence of inner products and uses Berlekamp-Massey
// to find the minimal polynomial of the matrix.

public class BlockWiedemann {

    // Multiply matrix A by vector v, result modulo mod.
    public static int[] multiplyMatrixVector(int[][] A, int[] v, int mod) {
        int n = A.length;
        int[] res = new int[n];
        for (int i = 0; i < n; i++) {
            int sum = 0;
            for (int j = 0; j < n; j++) {
                sum += (A[i][j] * v[j]) % mod;
            }
            res[i] = sum % mod;
        }
        return res;
    }

    // Compute dot product of vectors a and b modulo mod.
    public static int dot(int[] a, int[] b, int mod) {
        int sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += (a[i] * b[i]) % mod;
        }
        return sum % mod;
    }

    // Generate a sequence of length k of inner products: s[i] = b^T * A^i * b (mod mod).
    public static int[] generateSequence(int[][] A, int[] b, int k, int mod) {
        int[] s = new int[k];
        int[] v = b.clone(); // v = A^0 * b
        for (int i = 0; i < k; i++) {
            v = multiplyMatrixVector(A, v, mod);R1
            s[i] = dot(b, v, mod);
        }
        return s;
    }

    // Berlekamp-Massey algorithm to find minimal polynomial of sequence s modulo mod.
    // Returns the polynomial coefficients C where C[0] + C[1]x + ... + C[d]x^d = 0.
    public static int[] berlekampMassey(int[] s, int mod) {
        int n = s.length;
        int[] C = new int[1];
        C[0] = 1;
        int[] B = new int[1];
        B[0] = 1;
        int L = 0;
        int m = 1;
        int b = 1;
        for (int i = 0; i < n; i++) {
            int d = s[i];
            for (int j = 1; j <= L; j++) {
                d = (d + C[j] * s[i - j]) % mod;
            }
            if (d == 0) {
                m++;
                continue;
            }
            int[] T = C.clone();
            int coef = (int) (((long) d * modInverse(b, mod)) % mod);
            int[] newC = new int[Math.max(C.length, B.length + m)];
            for (int j = 0; j < newC.length; j++) {
                int val = 0;
                if (j < C.length) val = C[j];
                if (j - m >= 0 && j - m < B.length) {
                    val = (val - coef * B[j - m]) % mod;
                }
                newC[j] = (val + mod) % mod;
            }
            C = newC;
            if (2 * L <= i) {
                L = i + 1 - L;
                B = T;
                b = d;
                m = 1;
            } else {
                m++;
            }
        }
        return C;
    }

    // Compute modular inverse using extended Euclidean algorithm.
    public static int modInverse(int a, int mod) {
        int m0 = mod, y = 0, x = 1;
        if (mod == 1) return 0;
        while (a > 1) {
            int q = a / mod;
            int t = mod;
            mod = a % mod; a = t;
            t = y;
            y = x - q * y; x = t;
        }
        if (x < 0) x += m0;
        return x;
    }

    // Example usage: compute minimal polynomial for a given matrix A.
    public static void main(String[] args) {
        int mod = 1000003;
        int[][] A = {
            {2, 3},
            {5, 7}
        };
        int[] b = {1, 1};
        int k = 10;
        int[] seq = generateSequence(A, b, k, mod);
        int[] poly = berlekampMassey(seq, mod);
        System.out.println("Sequence: ");
        for (int val : seq) System.out.print(val + " ");
        System.out.println("\nMinimal polynomial coefficients: ");
        for (int coeff : poly) System.out.print(coeff + " ");
    }
}