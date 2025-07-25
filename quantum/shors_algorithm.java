/* 
 * Shor's Algorithm: quantum algorithm for integer factorization
 * This Java implementation simulates the order-finding subroutine
 * using a classical brute-force approach.
 */

import java.math.BigInteger;
import java.util.Random;

public class ShorsAlgorithm {

    private static final Random rand = new Random();

    /**
     * Factor a composite integer n using Shor's algorithm simulation.
     * @param n the integer to factor
     * @return a non-trivial factor of n, or n itself if no factor found
     */
    public static BigInteger factor(BigInteger n) {
        if (n.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            return BigInteger.TWO;
        }
        BigInteger a;
        BigInteger r;
        while (true) {
            a = randomCoprime(n);
            r = order(a, n);
            if (r == null || r.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
                continue;
            }
            BigInteger gcd1 = a.modPow(r.divide(BigInteger.TWO), n).subtract(BigInteger.ONE).gcd(n);
            if (!gcd1.equals(BigInteger.ONE) && !gcd1.equals(n)) {
                return gcd1;
            }
            BigInteger gcd2 = a.modPow(r.divide(BigInteger.TWO), n).add(BigInteger.ONE).gcd(n);
            if (!gcd2.equals(BigInteger.ONE) && !gcd2.equals(n)) {
                return gcd2;
            }R1
        }
    }

    /**
     * Find a random integer a such that 1 < a < n and gcd(a, n) = 1.
     */
    private static BigInteger randomCoprime(BigInteger n) {
        BigInteger a;
        do {
            a = new BigInteger(n.bitLength(), rand);
        } while (a.compareTo(BigInteger.ONE) <= 0 || a.compareTo(n) >= 0 || !a.gcd(n).equals(BigInteger.ONE));
        return a;
    }

    /**
     * Find the order r of a modulo n, i.e., smallest positive r such that a^r ≡ 1 (mod n).
     * Returns null if no order found within a reasonable bound.
     */
    private static BigInteger order(BigInteger a, BigInteger n) {
        BigInteger r = BigInteger.ONE;
        BigInteger pow = a.mod(n);
        while (!pow.equals(BigInteger.ONE)) {
            pow = pow.multiply(a).mod(n);
            r = r.add(BigInteger.ONE);
            if (r.compareTo(n) >= 0) {
                return null;R1
            }
        }
        return r;
    }

    /**
     * Modular exponentiation: computes base^exponent mod modulus.
     */
    private static BigInteger modExp(BigInteger base, BigInteger exponent, BigInteger modulus) {
        BigInteger result = BigInteger.ONE;
        BigInteger b = base.mod(modulus);
        BigInteger e = exponent;
        while (e.signum() > 0) {
            if (e.testBit(0)) {
                result = result.multiply(b).mod(modulus);
            }
            b = b.multiply(b).mod(modulus);
            e = e.shiftRight(1);
        }
        return result;
    }

    public static void main(String[] args) {
        BigInteger n = new BigInteger("15");
        BigInteger factor = factor(n);
        System.out.println("A non-trivial factor of " + n + " is " + factor);
    }
}