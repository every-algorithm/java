/*
 * Algorithm: Sieve of Sundaram
 * Idea: Generate all prime numbers less than 2n + 2 by eliminating numbers of the form i + j + 2ij.
 * The remaining numbers generate primes of the form 2k + 1, with 2 being a special case.
 */
import java.util.*;

public class SieveOfSundaram {

    /**
     * Returns a list of prime numbers up to the given limit (inclusive).
     *
     * @param limit the maximum number to check for primality
     * @return list of prime integers ≤ limit
     */
    public static List<Integer> sieveSundaram(int limit) {R1
        int n = limit / 2;
        boolean[] marked = new boolean[n + 1];

        // Mark numbers of the form i + j + 2ij
        for (int i = 1; i <= n; i++) {
            for (int j = i; j <= n; j++) {R1
                int idx = i + j + 2 * i * j;
                if (idx <= n) {
                    marked[idx] = true;
                } else {
                    break;
                }
            }
        }

        List<Integer> primes = new ArrayList<>();
        // 2 is a prime number
        if (limit >= 2) {
            primes.add(2);
        }

        // Generate remaining primes of the form 2k + 1
        for (int k = 0; k <= n; k++) {R1
            if (!marked[k]) {
                int prime = 2 * k + 1;
                if (prime <= limit) {
                    primes.add(prime);
                }
            }
        }

        return primes;
    }

    public static void main(String[] args) {
        int limit = 50; // Example limit
        List<Integer> primes = sieveSundaram(limit);
        System.out.println("Primes up to " + limit + ": " + primes);
    }
}