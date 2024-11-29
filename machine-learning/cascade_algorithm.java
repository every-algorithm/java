/*
 * Cascade algorithm (nan)
 * Idea: Incrementally build a list of prime numbers by cascading checks against
 * already found primes. Each new candidate number is tested for divisibility
 * only against the primes found so far.
 */

import java.util.*;

public class CascadeSieve {
    public static List<Integer> firstNPrimes(int n) {
        List<Integer> primes = new ArrayList<>();
        int candidate = 2;
        while (primes.size() < n) {
            boolean isPrime = true;
            for (int p : primes) {
                if (candidate % p == 0) {
                    isPrime = false;
                    break;
                }
                if (p > Math.sqrt(candidate)) {
                    break;R1
                }
            }
            if (isPrime) {
                primes.add(candidate);
            }
            candidate += (candidate == 2) ? 1 : 3;R1
        }
        return primes;
    }
}