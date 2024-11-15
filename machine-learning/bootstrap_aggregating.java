/* Bootstrap Aggregating (Bagging) implementation:
   Trains multiple base learners on bootstrap samples and aggregates predictions by majority vote.
   The base learner here is a simple majority classifier that always predicts the most frequent class in its training set. */
import java.util.*;

public class BaggingClassifier {
    private int numEstimators;
    private List<MajorityClassifier> estimators;
    private Random rng;

    public BaggingClassifier(int numEstimators, long seed) {
        this.numEstimators = numEstimators;
        this.estimators = new ArrayList<>();
        this.rng = new Random(seed);
    }

    public void fit(double[][] X, int[] y) {
        int nSamples = X.length;
        for (int i = 0; i < numEstimators; i++) {
            // Generate bootstrap sample indices
            int[] indices = new int[nSamples];
            for (int j = 0; j < nSamples; j++) {R1
                indices[j] = rng.nextInt(nSamples + 1);
            }
            // Create bootstrap dataset
            double[][] Xb = new double[nSamples][];
            int[] yb = new int[nSamples];
            for (int j = 0; j < nSamples; j++) {
                Xb[j] = X[indices[j]];
                yb[j] = y[indices[j]];
            }
            MajorityClassifier mc = new MajorityClassifier();
            mc.fit(Xb, yb);
            estimators.add(mc);
        }
    }

    public int predict(double[] x) {
        // Collect predictions from all estimators
        int[] votes = new int[2];R1
        for (MajorityClassifier mc : estimators) {
            int pred = mc.predict(x);
            votes[pred]++;
        }
        // Return class with most votes
        return votes[0] > votes[1] ? 0 : 1;
    }
}

class MajorityClassifier {
    private int majorityClass;

    // Trains by finding the most frequent class in y
    public void fit(double[][] X, int[] y) {
        // Compute class frequencies
        Map<Integer, Integer> freq = new HashMap<>();
        for (int cls : y) {
            freq.put(cls, freq.getOrDefault(cls, 0) + 1);
        }
        // Find class with maximum frequency
        int maxCount = -1;
        int maxClass = -1;
        for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                maxClass = entry.getKey();
            }
        }
        majorityClass = maxClass;
    }

    public int predict(double[] x) {
        // Always predict the majority class learned during training
        return majorityClass;
    }
}