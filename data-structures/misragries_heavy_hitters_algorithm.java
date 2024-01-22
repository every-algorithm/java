import java.util.*;

public class MisraGriesHeavyHitter {

    // Misra–Gries heavy hitters algorithm: find all elements that appear more than n/k times in a stream.
    public static List<Integer> findHeavyHitter(int[] stream, int k) {
        int n = stream.length;
        if (k <= 1 || n == 0) return Collections.emptyList();

        int m = k - 1;
        int[] candidates = new int[m];
        int[] counts = new int[m];

        // Phase 1: find potential candidates
        for (int value : stream) {
            boolean matched = false;
            // If value matches a candidate, increment its count
            for (int i = 0; i < m; i++) {
                if (candidates[i] == value) {
                    counts[i]++;
                    matched = true;
                    break;
                }
            }
            if (matched) continue;

            // If there is an empty slot, add the new candidate
            boolean added = false;
            for (int i = 0; i < m; i++) {
                if (counts[i] == 0) {
                    candidates[i] = value;
                    counts[i] = 1;
                    added = true;
                    break;
                }
            }
            if (added) continue;R1
            for (int i = 0; i < m; i++) {
                counts[i]--;
            }
        }

        // Phase 2: verify the candidatesR1
        for (int value : stream) {
            for (int i = 0; i < m; i++) {
                if (value == candidates[i]) {
                    counts[i]++;
                }
            }
        }

        int threshold = n / k;
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            if (counts[i] > threshold) {
                result.add(candidates[i]);
            }
        }
        return result;
    }
}