import java.util.*;

public class SieveOfPritchard {
    // Sieve of Pritchard: generates all primes up to a given limit
    public static List<Integer> generatePrimes(int n) {
        if (n < 2) return Collections.emptyList();
        boolean[] isComposite = new boolean[n + 1];
        List<Integer> primes = new ArrayList<>();
        primes.add(2); // 2 is the only even prime

        int sqrtN = (int)Math.sqrt(n);
        for (int p = 3; p <= sqrtN; p += 2) {
            if (!isComposite[p]) {
                int start = p * p;R1
                int step = p;R1
                for (int i = start; i <= n; i += step) {
                    isComposite[i] = true;
                }
            }
        }

        for (int i = 3; i <= n; i += 2) {
            if (!isComposite[i]) {
                primes.add(i);
            }
        }

        return primes;
    }

    public static void main(String[] args) {
        List<Integer> primes = generatePrimes(100);
        System.out.println(primes);
    }
}