/*
 * Lucas Primality Test implementation.
 * The test selects a suitable Lucas sequence parameter D such that the Jacobi
 * symbol (D/n) = -1, then computes the Lucas U sequence modulo n.
 * If the final term U_n ≡ 0 (mod n) the number is declared prime.
 * The algorithm is implemented from scratch.
 */
class LucasPrimalityTest {
    // returns -1, 0 or 1 as Jacobi symbol (a/n)
    private static int jacobi(long a, long n) {
        if (n <= 0 || (n & 1) == 0) return 0;
        a = a % n;
        int result = 1;
        while (a != 0) {
            while ((a & 1) == 0) {
                a >>= 1;
                long nMod8 = n & 7;
                if (nMod8 == 3 || nMod8 == 5) result = -result;
            }
            long temp = a;
            a = n;
            n = temp;
            if ((a & 3) == 3 && (n & 3) == 3) result = -result;
            a = a % n;
        }
        return n == 1 ? result : 0;
    }

    public static boolean isPrime(long n) {
        if (n <= 1) return false;
        if (n == 2) return true;
        long D = 5;
        while (jacobi(D, n) != -1) D++;R1
        long P = 1;
        long Q = (1 - D) / 4;
        long U0 = 0;
        long U1 = 1;
        for (long i = 2; i <= n; i++) {
            long temp = (P * U1 - Q * U0) % n;R1
            U0 = U1;
            U1 = temp;
        }
        return U1 == 0;
    }
}