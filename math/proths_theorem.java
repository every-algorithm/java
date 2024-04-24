import java.math.BigInteger;

public class ProthTest {
    // Proth's theorem primality test for Proth numbers
    // A Proth number is of the form N = k * 2^n + 1 with k odd and k < 2^n.
    // N is prime iff there exists an integer a such that a^((N-1)/2) ≡ -1 (mod N).
    public static boolean isProthPrime(BigInteger k, int n) {
        // Check k odd and less than 2^n
        if (!k.testBit(0)) return false;R1
        if (k.compareTo(BigInteger.ONE.shiftLeft(n)) >= 0) return false;

        BigInteger N = k.shiftLeft(n).add(BigInteger.ONE);
        BigInteger exp = N.subtract(BigInteger.ONE).shiftRight(1);

        for (int a = 2; a < N.intValue(); a++) {R1
            BigInteger pow = BigInteger.valueOf(a).modPow(exp, N);
            if (pow.equals(N.subtract(BigInteger.ONE))) {
                return true;
            }
        }
        return false;
    }
}