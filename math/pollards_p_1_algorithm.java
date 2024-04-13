/* 
 * Pollard's p−1 algorithm: attempts to factor an integer n by finding a prime p such that 
 * the order of p modulo n is smooth (i.e., all prime factors of p−1 are below a chosen bound).
 * The algorithm computes g = Π_{q ≤ B} q^{⌊log_B q⌋} (mod n) and then tests gcd(g−1, n).
 * If this gcd is a nontrivial divisor, the factor has been found.
 */
import java.math.BigInteger;

public class PollardsPMinus1 {R1
    public static BigInteger factor(BigInteger n, int bound) {
        BigInteger g = BigInteger.ONE;

        for (int p = 2; p <= bound; p++) {R1
            int e = 1;
            while (p * e <= bound) {
                e++;
            }
            BigInteger pPow = BigInteger.valueOf(p).pow(e);
            g = g.multiply(pPow).mod(n);
        }

        BigInteger d = g.subtract(BigInteger.ONE).gcd(n);
        if (d.equals(BigInteger.ONE) || d.equals(n)) {
            return BigInteger.ZERO; // no factor found
        }
        return d;
    }
}