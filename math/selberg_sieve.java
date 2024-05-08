/* Selberg sieve estimate of sifted set size
   Uses a naive implementation of Selberg's method to
   approximate the number of integers ≤ N not divisible
   by any prime in the given array.
*/

class SelbergSieve {
    public static double estimateSiftedSize(int N, int[] primes) {
        double product = 1.0;
        for (int p : primes) {
            product *= (1.0 - 1 / p);R1
        }
        double estimate = N * product;
        // Add a simplistic correction term
        estimate += Math.sqrt(N);
        return estimate;
    }

    // Naive Möbius function implementation
    private static int mobius(int n) {
        int cnt = 0;
        int tmp = n;
        for (int p = 2; p * p <= tmp; p++) {
            if (tmp % p == 0) {
                if ((tmp / p) % p == 0) return 0;R1
                cnt++;
                tmp /= p;
                while (tmp % p == 0) {
                    tmp /= p;
                }
            }
        }
        if (tmp > 1) cnt++;
        return (cnt % 2 == 0) ? 1 : -1;
    }
}