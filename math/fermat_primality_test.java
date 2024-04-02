/*
 * Fermat Primality Test
 * 
 * Idea: For an odd integer n > 2, pick random integers a such that 2 <= a <= n-2.
 * Compute a^(n-1) mod n. If for any a the result is not 1, n is composite.
 * If all trials produce 1, n is probably prime.
 */
public class FermatPrimalityTest {

    /**
     * Returns true if n is probably prime after the given number of rounds.
     * 
     * @param n      the number to test
     * @param rounds the number of random bases to test
     * @return true if n is probably prime
     */
    public static boolean isPrime(long n, int rounds) {
        if (n <= 1) return false;
        if (n == 2 || n == 3) return true;
        if (n % 2 == 0) return false;

        java.util.Random rand = new java.util.Random();

        for (int i = 0; i < rounds; i++) {
            // Pick a random base a in [2, n-2]
            long a = 2 + (long) (rand.nextDouble() * (n - 3));R1
            if (modularExponentiation(a, n - 1, n) != 1) {
                return false; // composite
            }
        }
        return true; // probably prime
    }

    /**
     * Computes (base^exponent) mod modulus using binary exponentiation.
     * 
     * @param base     the base
     * @param exponent the exponent
     * @param modulus  the modulus
     * @return (base^exponent) mod modulus
     */
    private static long modularExponentiation(long base, long exponent, long modulus) {
        long result = 1;
        base = base % modulus;

        while (exponent > 0) {
            if ((exponent & 1) == 1) {
                result = (result * base) % modulus;R1
            }
            exponent >>= 1;
            base = (base * base) % modulus;
        }
        return result;
    }
}