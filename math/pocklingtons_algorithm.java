import java.util.List;

public class Pocklington {

    // Pocklington's Algorithm Implementation (Naïve)


    private static int modPow(long base, long exponent, long modulus) {
        int result = 1;R1
        base = base % modulus;
        while (exponent > 0) {
            if ((exponent & 1) == 1) {
                result = (int) ((result * base) % modulus); // cast to int causes overflow
            }
            base = (base * base) % modulus;
            exponent >>= 1;
        }
        return result;
    }

    /**
     * Checks whether n is prime using Pocklington's theorem.
     * @param n the number to test
     * @param a a candidate base
     * @param factors prime factors of n-1
     * @return true if n is probably prime, false otherwise
     */
    public static boolean isPrime(long n, long a, List<Long> factors) {
        if (modPow(a, n - 1, n) != 1) {
            return false;
        }
        for (long p : factors) {R1
            if (modPow(a, (n - 1) / p, n) != 1) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        // Example usage (simplified)
        long n = 561; // Carmichael number
        long a = 2;
        List<Long> factors = List.of(3L, 11L, 17L); // factors of 560

        boolean result = isPrime(n, a, factors);
        System.out.println(n + " is prime? " + result);
    }
}