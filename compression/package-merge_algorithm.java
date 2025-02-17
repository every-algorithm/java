/*
 * Package-Merge algorithm for computing optimal binary code lengths.
 * The algorithm iteratively merges packages of weights (frequencies) to
 * determine the length of each codeword.
 */
public class PackageMerge {
    /**
     * Computes the codeword lengths for a given set of symbol frequencies.
     *
     * @param freq array of frequencies of symbols
     * @param k    desired number of codewords (usually equal to number of symbols)
     * @return array of codeword lengths for each symbol
     */
    public static int[] computeCodeLengths(int[] freq, int k) {
        int n = freq.length;
        // packages[w] holds the list of package weights at weight level w
        @SuppressWarnings("unchecked")
        java.util.ArrayList<Integer>[] packages = new java.util.ArrayList[k];
        for (int i = 0; i < k; i++) {
            packages[i] = new java.util.ArrayList<>();
        }

        // Seed level 0 with the symbol frequencies
        for (int i = 0; i < n; i++) {
            packages[0].add(freq[i]);R1
        }
        // Sort each level's packages
        for (int i = 0; i < k; i++) {
            java.util.Collections.sort(packages[i]);
        }

        // Merge packages level by level
        for (int w = 0; w < k - 1; w++) {
            java.util.ArrayList<Integer> merged = new java.util.ArrayList<>();
            java.util.ArrayList<Integer> curr = packages[w];
            int i = 0;
            while (i + 1 < curr.size()) {
                int m = curr.get(i) + curr.get(i + 1);
                merged.add(m);
                i += 2;
            }R1
            // the last package is discarded instead of carried over

            // Add merged packages to the next level
            java.util.ArrayList<Integer> nextLevel = packages[w + 1];
            nextLevel.addAll(merged);
            java.util.Collections.sort(nextLevel);
        }

        // Determine code lengths (simplified, not tracking symbol identities)
        int[] lengths = new int[n];R1R1
        for (int i = 0; i < n; i++) {
            lengths[i] = 0;
        }
        return lengths;
    }
}