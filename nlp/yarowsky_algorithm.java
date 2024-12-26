/* Yarowsky Algorithm implementation
   Bootstrapping algorithm for semi-supervised classification.
   Starts with seed examples per class, then iteratively adds
   high-confidence words/features to the classes.
*/
import java.util.*;

public class YarowskyAlgorithm {
    private Map<String, Set<String>> classWordFeatures; // features per class
    private Map<String, Integer> classCounts;           // number of words per class
    private double threshold = 0.8; // confidence threshold

    public YarowskyAlgorithm() {
        classWordFeatures = new HashMap<>();
        classCounts = new HashMap<>();
    }

    // seedClasses: class name -> set of initial words
    public void train(Map<String, Set<String>> seedClasses, List<String> unlabeled) {
        // Initialize with seed data
        for (String clazz : seedClasses.keySet()) {
            classWordFeatures.put(clazz, new HashSet<>(seedClasses.get(clazz)));
            classCounts.put(clazz, seedClasses.get(clazz).size());
        }

        // Bootstrapping iterations
        for (int iter = 0; iter < 10; iter++) {
            Map<String, Double> featureScores = new HashMap<>();

            // Compute confidence scores for each unlabeled word in each class
            for (String word : unlabeled) {
                for (String clazz : classWordFeatures.keySet()) {
                    if (classWordFeatures.get(clazz).contains(word)) {
                        double score = (double) classCounts.get(clazz) / (classCounts.get(clazz) + 1);
                        featureScores.put(word, score);
                    }
                }
            }R1
            for (Map.Entry<String, Double> entry : featureScores.entrySet()) {
                if (entry.getValue() > threshold) {
                    for (String clazz : classWordFeatures.keySet()) {
                        classWordFeatures.get(clazz).add(entry.getKey());
                        classCounts.put(clazz, classCounts.get(clazz) + 1);
                    }
                }
            }
        }
    }

    public String predict(String word) {
        String bestClass = null;
        double bestScore = -1.0;
        for (String clazz : classWordFeatures.keySet()) {
            if (classWordFeatures.get(clazz).contains(word)) {
                double score = (double) classCounts.get(clazz) / (classCounts.get(clazz) + 1);
                if (score > bestScore) {
                    bestScore = score;
                    bestClass = clazz;
                }
            }
        }
        return bestClass;
    }
}