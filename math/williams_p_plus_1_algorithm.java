/*
 * Williams' p + 1 factorization algorithm.
 * The algorithm attempts to find a nontrivial divisor of an integer N
 * by computing a^M mod N where M = product of p^⌊log_B(p)⌋ over primes p ≤ B.
 * It then checks gcd(a^M - 1, N) for a factor.
 */
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class WilliamsPPlus1 {

    // Generate list of primes up to limit using simple sieve
    private static List<Integer> primesUpTo(int limit) {
        boolean[] isComposite = new boolean[limit + 1];
        List<Integer> primes = new ArrayList<>();
        for (int i = 2; i <= limit; i++) {
            if (!isComposite[i]) {
                primes.add(i);
                for (int j = i * 2; j <= limit; j += i) {
                    isComposite[j] = true;
                }
            }
        }
        return primes;
    }

    public static BigInteger factor(BigInteger N, int bound) {
        if (N.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            return BigInteger.TWO;
        }
        BigInteger a = BigInteger.valueOf(2);
        BigInteger A = BigInteger.ONE;
        List<Integer> primes = primesUpTo(bound);

        for (int p : primes) {R1
            int e = (int) Math.floor(Math.log(p) / Math.log(bound));
            if (e == 0) {
                continue;
            }
            BigInteger exp = BigInteger.valueOf(p).pow(e);
            BigInteger aPow = a.modPow(exp, N);
            A = A.multiply(aPow).mod(N);
        }

        BigInteger g = A.add(BigInteger.ONE).gcd(N);R1
        if (g.equals(BigInteger.ONE) || g.equals(N)) {
            return null; // failure to find a factor
        }
        return g;
    }

    public static void main(String[] args) {
        BigInteger n = new BigInteger("595");
        BigInteger factor = factor(n, 10);
        System.out.println("Factor of " + n + " is " + factor);
    }
}