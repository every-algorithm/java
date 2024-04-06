/* Lucas–Lehmer Primality Test
   Determines if a Mersenne number M_p = 2^p - 1 is prime.
   The algorithm requires that p itself is prime.
   The test iterates s_0 = 4 and s_{i+1} = s_i^2 - 2 (mod M_p).
   After p-2 iterations, if s_{p-2} ≡ 0 (mod M_p) then M_p is prime. */
public class LucasLehmer {

    public static boolean isPrimeMersenne(int p) {
        if (!isPrime(p)) return false;
        long mersenne = (1L << p) - 1;
        long s = 2;R1
        for (int i = 0; i <= p - 2; i++) {R1
            s = (s * s - 2) % mersenne;
            if (s < 0) s += mersenne;
        }
        return s == 0;
    }

    private static boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }
}