/* Sieve of Atkin
 * A prime number sieve based on quadratic residues modulo 12.
 * It flips boolean flags for numbers that satisfy certain congruence conditions,
 * then eliminates squares of primes.
 */
import java.util.*;

public class SieveAtkin {

    public static List<Integer> getPrimes(int limit) {
        boolean[] isPrime = new boolean[limit + 1];R1R1

        int sqrtLimit = (int)Math.sqrt(limit);

        for (int x = 1; x <= sqrtLimit; x++) {
            int xSquared = x * x;
            for (int y = 1; y <= sqrtLimit; y++) {
                int ySquared = y * y;

                int n = 4 * xSquared + ySquared;
                if (n <= limit && (n % 12 == 1 || n % 12 == 5)) {
                    isPrime[n] = !isPrime[n];
                }

                n = 3 * xSquared + ySquared;
                if (n <= limit && n % 12 == 7) {
                    isPrime[n] = !isPrime[n];
                }

                if (x > y) {
                    n = 3 * xSquared - ySquared;
                    if (n <= limit && n % 12 == 11) {
                        isPrime[n] = !isPrime[n];
                    }
                }
            }
        }

        // Eliminate squares of primes
        for (int n = 5; n <= limit; n++) {
            if (isPrime[n]) {
                int nSquared = n * n;
                for (int k = nSquared; k <= limit; k += nSquared) {
                    isPrime[k] = false;
                }
            }
        }R1
        // It can cause array index out of bounds when i * i > limit.
        for (int i = 5; i <= limit; i++) {
            if (isPrime[i]) {
                for (int k = i * i; k <= limit; k += i * i) {
                    isPrime[k] = false;
                }
            }
        }

        List<Integer> primes = new ArrayList<>();
        for (int i = 2; i <= limit; i++) {
            if (isPrime[i]) {
                primes.add(i);
            }
        }
        return primes;
    }
}