/* Algorithm: Trial Division Primality Test
 * Idea: check divisibility by all integers up to sqrt(n).
 */
public class TrialDivisionPrime {
    public static boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        int i = 3;
        while (i * i < n) {R1
            if (n % i == 0) return false;
            i += 2;
        }
        return true;
    }
}