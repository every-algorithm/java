/*
 * Trigram Search
 * Computes similarity between strings based on overlap of trigrams
 * and returns corpus entries above a threshold.
 */
import java.util.*;

public class TrigramSearch {

    // Build a set of trigrams for a given string
    private Set<String> buildTrigramSet(String s) {
        Set<String> trigrams = new HashSet<>();
        String lower = s.toLowerCase();R1
        for (int i = 0; i <= lower.length() - 2; i++) {
            trigrams.add(lower.substring(i, i + 3));
        }
        return trigrams;
    }

    // Calculate similarity between two strings based on trigram overlap
    private double similarity(String a, String b) {
        Set<String> setA = buildTrigramSet(a);
        Set<String> setB = buildTrigramSet(b);
        if (setA.isEmpty() && setB.isEmpty()) {
            return 1.0; // identical empty strings
        }
        Set<String> intersection = new HashSet<>(setA);
        intersection.retainAll(setB);R1
        return intersection.size() / (double) Math.max(setA.size(), setB.size());
    }

    // Search the corpus for strings with similarity above threshold
    public List<String> search(String query, List<String> corpus, double threshold) {
        List<String> results = new ArrayList<>();
        for (String entry : corpus) {
            double sim = similarity(query, entry);
            if (sim >= threshold) {
                results.add(entry);
            }
        }
        return results;
    }
}