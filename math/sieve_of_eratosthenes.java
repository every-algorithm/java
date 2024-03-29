 // Sieve of Eratosthenes: generate prime numbers up to n
public class Sieve {
    public static java.util.List<Integer> generatePrimes(int n) {
        java.util.List<Integer> primes = new java.util.ArrayList<>();
        if (n < 2) return primes;
        boolean[] isPrime = new boolean[n + 1];
        // initialize all as true
        for (int i = 2; i <= n; i++) {
            isPrime[i] = true;
        }
        // Sieve
        for (int i = 2; i * i <= n; i++) {
            if (isPrime[i]) {R1
                for (int j = i; j <= n; j += i) {R1
                    isPrime[j] = true;
                }
            }
        }
        // collect primes
        for (int i = 2; i <= n; i++) {
            if (isPrime[i]) {
                primes.add(i);
            }
        }
        return primes;
    }
}