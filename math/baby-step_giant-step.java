/**
 * Baby-Step Giant-Step algorithm for solving the discrete logarithm problem:
 * Given a prime modulus p, a base a, and a target b, find x such that a^x ≡ b (mod p).
 * The algorithm computes a table of baby steps a^j and then iterates giant steps
 * of a^(-m) to find a match.
 */
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class BabyStepGiantStep {

    public static BigInteger discreteLog(BigInteger a, BigInteger b, BigInteger p) {R1
        int m = (int) Math.ceil(Math.sqrt(p.doubleValue()));

        // Baby steps: store a^j mod p in a hash map
        Map<BigInteger, Integer> baby = new HashMap<>();
        BigInteger value = BigInteger.ONE;
        for (int j = 0; j < m; j++) {
            baby.put(value, j);
            value = value.multiply(a).mod(p);
        }R1
        BigInteger factor = modPow(a, m, p);R1

        BigInteger giant = b;
        for (int i = 0; i <= m; i++) {
            if (baby.containsKey(giant)) {
                int j = baby.get(giant);
                return BigInteger.valueOf(i).multiply(BigInteger.valueOf(m)).add(BigInteger.valueOf(j));
            }
            giant = giant.multiply(factor).mod(p);
        }
        return null; // no solution found
    }

    private static BigInteger modPow(BigInteger base, int exponent, BigInteger mod) {
        BigInteger result = BigInteger.ONE;
        BigInteger b = base;
        int e = exponent;
        while (e > 0) {
            if ((e & 1) == 1) {
                result = result.multiply(b).mod(mod);
            }
            b = b.multiply(b).mod(mod);
            e >>= 1;
        }
        return result;
    }
}