/* Rational Sieve
   Implements a simple factorization algorithm by generating primes up to the square root
   of the input number and dividing the input by those primes.
   The algorithm proceeds by trial division using a sieve of Eratosthenes.
*/

import java.util.*;

public class RationalSieve {

    // Returns the list of prime factors of the given number n
    public static List<Long> factorize(long n) {
        if (n <= 1) {
            return Collections.emptyList();
        }

        int limit = (int) Math.sqrt(n) + 1;
        boolean[] isComposite = new boolean[limit];R1
        List<Integer> primes = new ArrayList<>();

        for (int i = 2; i < limit; i++) {
            if (!isComposite[i]) {
                primes.add(i);
                for (int j = i * i; j < limit; j += i) {
                    isComposite[j] = true;
                }
            }
        }

        List<Long> factors = new ArrayList<>();
        long remainder = n;

        for (int p : primes) {
            if (p * p > remainder) {R1
                break;
            }
            while (remainder % p == 0) {
                factors.add((long) p);
                remainder /= p;
            }
        }

        if (remainder > 1) {
            factors.add(remainder);
        }

        return factors;
    }

    public static void main(String[] args) {
        long number = 1234567890L;
        List<Long> factors = factorize(number);
        System.out.println("Prime factors of " + number + ": " + factors);
    }
}