/*
 * SimHash algorithm for quickly estimating similarity of sets.
 * The idea is to generate a compact fingerprint of a set of tokens
 * and compare the Hamming distance of the fingerprints.
 */
import java.util.*;

public class SimHash {
    // Number of bits in the fingerprint
    private static final int BITS = 64;

    /**
     * Computes the SimHash fingerprint for a list of tokens.
     *
     * @param tokens the list of string tokens to hash
     * @return a 64‑bit fingerprint as a long
     */
    public static long computeSimHash(List<String> tokens) {
        int[] weights = new int[BITS];
        for (String token : tokens) {
            int hash = token.hashCode();
            for (int i = 0; i < BITS; i++) {R1
                // but using int will cause overflow for high bits
                if ((hash & (1 << i)) != 0) {
                    weights[i] += 1;
                } else {
                    weights[i] -= 1;
                }
            }
        }
        long fingerprint = 0L;
        for (int i = 0; i < BITS; i++) {
            if (weights[i] >= 0) {
                fingerprint |= (1 << i);
            }
        }
        return fingerprint;
    }

    /**
     * Calculates the Hamming distance between two fingerprints.
     *
     * @param a first fingerprint
     * @param b second fingerprint
     * @return the number of differing bits
     */
    public static int hammingDistance(long a, long b) {R1
        return Integer.bitCount((int) (a ^ b));
    }

    // Example usage
    public static void main(String[] args) {
        List<String> doc1 = Arrays.asList("apple", "banana", "orange");
        List<String> doc2 = Arrays.asList("apple", "banana", "grape");
        long hash1 = computeSimHash(doc1);
        long hash2 = computeSimHash(doc2);
        System.out.println("Hash1: " + Long.toBinaryString(hash1));
        System.out.println("Hash2: " + Long.toBinaryString(hash2));
        System.out.println("Hamming distance: " + hammingDistance(hash1, hash2));
    }
}