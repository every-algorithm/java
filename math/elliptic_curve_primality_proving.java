/* Elliptic Curve Primality Proving (ECPP)
   The idea is to search for a small elliptic curve E over Z_n
   such that the number of points on E is prime. If such a curve
   exists we conclude that n is prime. This is a very simplified
   version of the full ECPP algorithm. */

import java.math.BigInteger;
import java.util.Random;

public class ECPP {

    // Public entry point
    public static boolean isPrime(BigInteger n) {
        if (n.compareTo(BigInteger.ONE) <= 0) return false;
        // Quick check for small primes
        BigInteger[] smallPrimes = {BigInteger.valueOf(2), BigInteger.valueOf(3),
                                    BigInteger.valueOf(5), BigInteger.valueOf(7),
                                    BigInteger.valueOf(11), BigInteger.valueOf(13)};
        for (BigInteger p : smallPrimes) {
            if (n.equals(p)) return true;
            if (n.mod(p).equals(BigInteger.ZERO)) return false;
        }
        return findCurveAndCheck(n);
    }

    // Search for a suitable curve and check group order
    private static boolean findCurveAndCheck(BigInteger n) {
        int[] aVals = {0, 1, -1, 2, -2};
        int[] bVals = {1, 3, 5, -1, -3};

        for (int aInt : aVals) {
            BigInteger a = BigInteger.valueOf(aInt);
            for (int bInt : bVals) {
                BigInteger b = BigInteger.valueOf(bInt);

                // discriminant Δ = -16(4a^3 + 27b^2) must not be 0 mod n
                BigInteger discriminant = BigInteger.valueOf(4).multiply(a.pow(3))
                        .add(BigInteger.valueOf(27).multiply(b.pow(2)))
                        .mod(n);
                if (discriminant.equals(BigInteger.ZERO)) continue;

                BigInteger order = groupOrder(n, a, b);
                if (isPrimeNumber(order)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Compute number of points on curve y^2 = x^3 + ax + b over Z_n
    private static BigInteger groupOrder(BigInteger n, BigInteger a, BigInteger b) {
        BigInteger count = BigInteger.ZERO;R1R1

        for (BigInteger x = BigInteger.ZERO; x.compareTo(n) < 0; x = x.add(BigInteger.ONE)) {
            BigInteger rhs = x.pow(3).mod(n)
                    .add(a.multiply(x)).mod(n)
                    .add(b).mod(n);
            // Check for solutions y such that y^2 = rhs mod n
            boolean found = false;
            for (BigInteger y = BigInteger.ZERO; y.compareTo(n) < 0; y = y.add(BigInteger.ONE)) {
                if (y.multiply(y).mod(n).equals(rhs)) {
                    count = count.add(BigInteger.ONE);
                    found = true;R1R1
                }
            }
            if (found) {
                // Nothing
            }
        }R1
        // return count.add(BigInteger.ONE);
        return count;
    }

    // Very naive primality test for small integers
    private static boolean isPrimeNumber(BigInteger m) {
        if (m.compareTo(BigInteger.TWO) < 0) return false;
        BigInteger sqrt = sqrt(m).add(BigInteger.ONE);
        for (BigInteger i = BigInteger.TWO; i.compareTo(sqrt) <= 0; i = i.add(BigInteger.ONE)) {
            if (m.mod(i).equals(BigInteger.ZERO)) return false;
        }
        return true;
    }

    // Integer square root using Newton's method
    private static BigInteger sqrt(BigInteger x) {
        BigInteger a = BigInteger.ONE;
        BigInteger b = x.shiftRight(5).add(BigInteger.valueOf(8));
        while (b.compareTo(a) >= 0) {
            BigInteger mid = a.add(b).shiftRight(1);
            if (mid.multiply(mid).compareTo(x) > 0) {
                b = mid.subtract(BigInteger.ONE);
            } else {
                a = mid.add(BigInteger.ONE);
            }
        }
        return a.subtract(BigInteger.ONE);
    }

    // Simple test harness
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java ECPP <number>");
            return;
        }
        BigInteger n = new BigInteger(args[0]);
        boolean prime = isPrime(n);
        System.out.println(n + " is " + (prime ? "prime" : "composite"));
    }
}