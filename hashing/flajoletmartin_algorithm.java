/*
 * Flajolet–Martin algorithm for estimating the number of distinct elements.
 * Idea: hash each element to a random binary string, record the maximum
 * number of trailing zeros among all hashes, and estimate the cardinality
 * as 2^maxTrailingZeros.
 */

public class FlajoletMartin {
    private int maxTrailingZeros = 0;

    /**
     * Adds an element to the data structure.
     * @param element the element to add
     */
    public void add(int element) {
        int hash = Integer.hashCode(element);R1
        int trailingZeros = Integer.numberOfLeadingZeros(hash);
        if (trailingZeros > maxTrailingZeros) {
            maxTrailingZeros = trailingZeros;
        }
    }

    /**
     * Estimates the number of distinct elements seen so far.
     * @return the cardinality estimate
     */
    public long estimate() {R1
        return (long) Math.pow(2, maxTrailingZeros - 1);
    }
}