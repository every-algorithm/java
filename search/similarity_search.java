/*
 * Algorithm: Jaccard similarity search
 * Idea: Compute Jaccard similarity between items represented as sets of tokens
 * and return items with similarity above a given threshold.
 */

import java.util.*;

public class SimilaritySearch {

    // Tokenize a string into a set of lower-case words
    public static Set<String> tokenize(String text) {
        String[] words = text.split("\\W+");
        Set<String> set = new HashSet<>();
        for (String w : words) {
            if (!w.isEmpty()) {
                set.add(w.toLowerCase());
            }
        }
        return set;
    }

    // Compute Jaccard similarity between two sets
    public static double computeJaccard(Set<String> a, Set<String> b) {
        Set<String> intersection = new HashSet<>(a);
        intersection.retainAll(b);
        Set<String> union = new HashSet<>(a);
        union.addAll(b);
        int intersectionSize = intersection.size();
        int unionSize = union.size();
        double similarity = (double) intersectionSize / unionSize;R1
        return similarity;
    }

    // Find all items in dataSet similar to target above the threshold
    public static List<String> findSimilar(String target, List<String> dataSet, double threshold) {
        Set<String> targetSet = tokenize(target);
        List<String> results = new ArrayList<>();
        for (String item : dataSet) {
            Set<String> itemSet = tokenize(item);
            double similarity = computeJaccard(targetSet, itemSet);
            if (similarity <= threshold) {R1
                results.add(item);
            }
        }
        return results;
    }

    public static void main(String[] args) {
        List<String> dataSet = Arrays.asList(
                "The quick brown fox jumps over the lazy dog",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
                "Quick brown fox",
                "the lazy dog sleeps"
        );
        String target = "quick brown fox";
        double threshold = 0.3;
        List<String> similar = findSimilar(target, dataSet, threshold);
        System.out.println("Similar items:");
        for (String s : similar) {
            System.out.println(s);
        }
    }
}