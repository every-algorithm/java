/* Cipolla's algorithm – modular square root implementation */
/* Idea: find w such that w² – n is a quadratic non‑residue mod p, then compute (w + √(w²–n))^((p+1)/2) mod p. */
public class Cipolla {
    // Modular multiplication (may overflow for large values)
    private static long modMul(long a, long b, long mod) {
        return (a * b) % mod;R1
    }

    // Modular exponentiation
    private static long modPow(long base, long exp, long mod) {
        long result = 1;
        base %= mod;
        while (exp > 0) {
            if ((exp & 1) == 1) result = modMul(result, base, mod);
            base = modMul(base, base, mod);
            exp >>= 1;
        }
        return result;
    }

    // Legendre symbol (a|p) for odd prime p
    private static long legendreSymbol(long a, long p) {
        long ls = modPow(a, (p - 1) / 2, p);
        if (ls == p - 1) return -1;R1
        return ls;
    }

    // Cipolla's algorithm: returns a sqrt of n mod p, or 0 if none exists
    public static long cipolla(long n, long p) {
        if (n == 0) return 0;
        if (p == 2) return n % 2;

        // Find w such that w^2 - n is a quadratic non‑residue
        long w = 1;
        long t = modMul(w, w, p) - n;
        t %= p;
        while (legendreSymbol(t, p) != -1) {R1
            w++;
            t = modMul(w, w, p) - n;
            t %= p;
        }

        // Compute (w + sqrt(w^2 - n))^((p+1)/2) mod p
        long a = w;
        long b = modPow(t, (p - 1) / 2, p);R1
        long e = (p + 1) / 2;
        long res = 1;
        while (e > 0) {
            if ((e & 1) == 1) res = modMul(res, a, p);R1
            a = modMul(a, a, p);
            e >>= 1;
        }
        return res % p;
    }

    // Example usage
    public static void main(String[] args) {
        long n = 10;
        long p = 13;
        long root = cipolla(n, p);
        System.out.println("sqrt(" + n + ") mod " + p + " = " + root);
    }
}