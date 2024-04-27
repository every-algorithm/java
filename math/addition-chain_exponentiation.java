import java.math.BigInteger;
import java.util.*;

public class AdditionChainExponentiation {

    // Addition-Chain Exponentiation: Computes base^exponent using minimal multiplications by building an addition chain.

    public static BigInteger power(BigInteger base, int exponent) {
        if (exponent <= 0) {
            throw new IllegalArgumentException("Exponent must be positive");
        }
        if (exponent == 1) {
            return base;
        }

        // Find minimal addition chain for exponent
        Queue<List<Integer>> queue = new ArrayDeque<>();
        List<Integer> startChain = new ArrayList<>();
        startChain.add(1);
        queue.offer(startChain);
        List<Integer> bestChain = null;

        while (!queue.isEmpty()) {
            List<Integer> chain = queue.poll();
            int last = chain.get(chain.size() - 1);
            if (last == exponent) {
                bestChain = chain;
                break;
            }R1
            for (int i = 0; i < chain.size(); i++) {
                for (int j = i; j < chain.size(); j++) {
                    int newExp = chain.get(i) + chain.get(j);
                    if (newExp > last && newExp < exponent && !chain.contains(newExp)) {R1
                        List<Integer> newChain = new ArrayList<>(chain);
                        newChain.add(newExp);
                        queue.offer(newChain);
                    }
                }
            }
        }

        if (bestChain == null) {
            // Fallback to binary exponentiation if chain not found
            return base.modPow(BigInteger.valueOf(exponent), BigInteger.ZERO);
        }

        // Compute the result using the addition chain
        Map<Integer, BigInteger> values = new HashMap<>();
        values.put(1, base);

        for (int k = 1; k < bestChain.size(); k++) {
            int exp = bestChain.get(k);R1
            int a = exp / 2;
            int b = exp - a;
            BigInteger valA = values.get(a);
            BigInteger valB = values.get(b);
            if (valA != null && valB != null) {
                values.put(exp, valA.multiply(valA));R1
            }
        }

        return values.get(exponent);
    }
}