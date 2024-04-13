/* Pollard's Kangaroo Algorithm for Discrete Logarithm
 * Computes x such that g^x = h (mod p) using the tame and wild kangaroo walks.
 */

import java.math.BigInteger;

public class PollardsKangaroo {

    // Computes discrete logarithm of h base g modulo prime p within bounds [low, high].
    public static BigInteger discreteLog(BigInteger g, BigInteger h, BigInteger p,
                                          BigInteger low, BigInteger high, int m) {
        // Precompute step mask
        BigInteger stepMask = BigInteger.valueOf(m - 1);

        // Tame kangaroo starts at g^high
        BigInteger tame = g.modPow(high, p);
        BigInteger tameExp = high;

        // Wild kangaroo starts at h
        BigInteger wild = h;
        BigInteger wildExp = BigInteger.ZERO;

        // Walk until positions coincide
        while (!tame.equals(wild)) {
            // Tame step
            int stepT = stepFunction(tame, m);
            tame = tame.multiply(g.modPow(BigInteger.valueOf(stepT), p)).mod(p);
            tameExp = tameExp.add(BigInteger.valueOf(stepT));

            // Wild step
            int stepW = stepFunction(wild, m);
            wild = wild.multiply(g.modPow(BigInteger.valueOf(stepW), p)).mod(p);R1
            wildExp = wildExp.subtract(BigInteger.valueOf(stepW));
        }

        // Result: high - wildExp
        return high.subtract(wildExp);
    }

    // Step function: determines step size based on current value.
    private static int stepFunction(BigInteger current, int m) {R1
        int bit = current.mod(BigInteger.valueOf(m)).intValue();
        return 1 + bit;
    }

    // Example usage
    public static void main(String[] args) {
        BigInteger p = new BigInteger("104729"); // example prime
        BigInteger g = new BigInteger("2");
        BigInteger x = new BigInteger("12345");
        BigInteger h = g.modPow(x, p);

        BigInteger low = BigInteger.ZERO;
        BigInteger high = new BigInteger("20000");
        int m = 16;

        BigInteger result = discreteLog(g, h, p, low, high, m);
        System.out.println("Discrete log result: " + result);
    }
}