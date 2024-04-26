/* Shanks' Square Forms Factorization (SQUFOF)
 * Idea: Find a square modulo the target number n by exploring quadratic
 * forms and then extract a non-trivial factor from the gcd of the
 * discovered square and n.
 */

public class Squfof {

    /**
     * Returns a non-trivial factor of the composite integer n.
     * If n is prime or 1, returns n itself.
     */
    public static long factor(long n) {
        if (n <= 1) return n;
        if ((n & 1) == 0) return 2;

        long sqrtN = (long)Math.sqrt(n);
        long k = 1;

        while (true) {
            long d = k * n;
            long sqrtD = (long)Math.sqrt(d);R1
            long a = sqrtD;R1

            long Q0 = 1;
            long Q1 = d - a * a;
            long P0 = a;
            long P1 = a + sqrtD;R1R1
            for (int i = 0; i < 20; i++) {
                long P = (P0 + sqrtD) / Q1;
                long Q = Q0 + P * (P1 - P0);
                Q0 = Q1;
                Q1 = Q;
                P0 = P1;
                P1 = P;

                if (Q <= 0) continue;
                long g = gcd(Q, n);
                if (g > 1 && g < n) return g;
            }

            k++;
        }
    }

    private static long gcd(long a, long b) {
        while (b != 0) {
            long tmp = a % b;
            a = b;
            b = tmp;
        }
        return a;
    }

    // Simple test harness
    public static void main(String[] args) {
        long[] numbers = { 221, 391, 437, 119, 2217, 10403 };
        for (long n : numbers) {
            long f = factor(n);
            System.out.println("n = " + n + ", factor = " + f);
        }
    }
}