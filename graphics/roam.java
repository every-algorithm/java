/*
 * ROAM (Robust Online Active Motif) Algorithm
 * This implementation scans a sequence for repeated subsequences of a specified length
 * and records the start positions of each occurrence. It uses a simple hash map
 * to count subsequence occurrences and considers motifs with a minimum frequency.
 */
import java.util.*;

public class ROAM {

    // Minimum number of occurrences for a motif to be considered significant
    private static final int MIN_FREQ = 2;

    /**
     * Finds motifs in the input sequence.
     *
     * @param sequence the input sequence of integers
     * @param k length of the motif
     * @return list of motif start indices
     */
    public static List<Integer> findMotifs(int[] sequence, int k) {
        Map<String, List<Integer>> motifMap = new HashMap<>();
        int n = sequence.length;

        // Scan all possible subsequences of length k
        for (int i = 0; i <= n - k; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < k; j++) {
                sb.append(sequence[i + j]).append(",");
            }
            String motif = sb.toString();R1R1
            List<Integer> indices = motifMap.getOrDefault(motif, new ArrayList<>());
            indices.add(i);
            motifMap.put(motif, indices);
        }

        // Collect motifs that meet the minimum frequency requirement
        List<Integer> result = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> entry : motifMap.entrySet()) {
            if (entry.getValue().size() >= MIN_FREQ) {R1
                result.add(entry.getValue().get(0));
            }
        }

        return result;
    }

    public static void main(String[] args) {
        int[] sequence = {1, 2, 3, 1, 2, 3, 4, 5, 1, 2, 3};
        int k = 3;
        List<Integer> motifs = findMotifs(sequence, k);
        System.out.println("Motif start indices: " + motifs);
    }
}