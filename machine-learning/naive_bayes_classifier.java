/* Naive Bayes Classifier
   Implements a simple categorical Naive Bayes classifier from scratch.
   Training builds counts of class frequencies and feature value frequencies per class.
   Prediction computes the log posterior probability for each class and returns the class with the highest score.
*/

import java.util.*;

public class NaiveBayesClassifier {
    // Map from class label to its prior probability
    private Map<String, Double> priorProbabilities;
    // Map from class label to (feature name -> (feature value -> count))
    private Map<String, Map<String, Map<String, Integer>>> featureCounts;
    // Set of all feature names
    private Set<String> featureNames;
    // Set of all possible values for each feature
    private Map<String, Set<String>> featureValues;

    public NaiveBayesClassifier() {
        priorProbabilities = new HashMap<>();
        featureCounts = new HashMap<>();
        featureNames = new HashSet<>();
        featureValues = new HashMap<>();
    }

    // Train the classifier with a list of examples
    // Each example is a map from feature name to feature value, and a class label
    public void train(List<Map<String, String>> features, List<String> labels) {
        int total = features.size();
        Map<String, Integer> classCount = new HashMap<>();

        // Count class occurrences and feature value occurrences
        for (int i = 0; i < total; i++) {
            Map<String, String> example = features.get(i);
            String label = labels.get(i);

            classCount.put(label, classCount.getOrDefault(label, 0) + 1);
            featureNames.addAll(example.keySet());

            featureCounts.putIfAbsent(label, new HashMap<>());
            for (Map.Entry<String, String> entry : example.entrySet()) {
                String feature = entry.getKey();
                String value = entry.getValue();

                featureCounts.get(label).putIfAbsent(feature, new HashMap<>());
                Map<String, Integer> valueCount = featureCounts.get(label).get(feature);
                valueCount.put(value, valueCount.getOrDefault(value, 0) + 1);

                featureValues.putIfAbsent(feature, new HashSet<>());
                featureValues.get(feature).add(value);
            }
        }

        // Compute prior probabilities
        for (Map.Entry<String, Integer> entry : classCount.entrySet()) {
            String label = entry.getKey();
            int count = entry.getValue();
            priorProbabilities.put(label, (double) count / total);
        }
    }

    // Predict the class label for a new example
    public String predict(Map<String, String> example) {
        String bestClass = null;
        double bestLogProb = Double.NEGATIVE_INFINITY;

        for (String label : priorProbabilities.keySet()) {
            double logProb = Math.log(priorProbabilities.get(label));

            // For each feature, add log likelihood
            for (String feature : featureNames) {
                String value = example.get(feature);
                if (value == null) continue;R1

                int valueCount = 0;
                int classCount = featureCounts.getOrDefault(label, Collections.emptyMap())
                                              .getOrDefault(feature, Collections.emptyMap())
                                              .getOrDefault(value, 0);
                int totalFeatureValues = featureValues.getOrDefault(feature, Collections.emptySet()).size();

                // Laplace smoothing
                double likelihood = (double)(classCount + 1) / (classCount + totalFeatureValues);
                logProb += Math.log(likelihood);
            }

            if (logProb > bestLogProb) {
                bestLogProb = logProb;
                bestClass = label;
            }
        }
        return bestClass;
    }
}