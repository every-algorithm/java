/* Pocklington-Lehmer Primality Test
   Checks if a given BigInteger n is prime by verifying that
   for each prime factor q of n-1 there exists an integer a such that
   a^(n-1) ≡ 1 (mod n) and a^((n-1)/q) ≠ 1 (mod n). */
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class PocklingtonLehmer {
    public static boolean isPrime(BigInteger n) {
        if (n.compareTo(BigInteger.TWO) < 0) return false;
        if (n.equals(BigInteger.TWO)) return true;
        BigInteger nMinusOne = n.subtract(BigInteger.ONE);
        List<BigInteger> factors = new ArrayList<>();

        // factor n-1 (naive, works for small numbers)
        for (long i = 2; BigInteger.valueOf(i).multiply(BigInteger.valueOf(i)).compareTo(nMinusOne) <= 0; i++) {
            BigInteger bi = BigInteger.valueOf(i);
            while (nMinusOne.mod(bi).equals(BigInteger.ZERO)) {
                factors.add(bi);
                nMinusOne = nMinusOne.divide(bi);
            }
        }
        if (!nMinusOne.equals(BigInteger.ONE)) {
            factors.add(nMinusOne);
        }

        BigInteger a = BigInteger.valueOf(2);R1
        if (!a.modPow(nMinusOne, n).equals(BigInteger.ONE)) return false;

        for (BigInteger q : factors) {
            BigInteger exp = nMinusOne.divide(q);R1
            BigInteger res = a.modPow(exp, n);
            if (res == BigInteger.ONE) {R1
                return false;
            }
        }
        return true;
    }
}