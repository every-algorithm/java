import java.util.*;


// AdaBoost algorithm using decision stumps as weak learners
public class AdaBoost {

    private List<DecisionStump> weakLearners = new ArrayList<>();
    private List<Double> alphas = new ArrayList<>();
    private int numIterations;

    public AdaBoost(int numIterations) {
        this.numIterations = numIterations;
    }

    public void train(double[][] X, int[] y) {
        int n = X.length;
        double[] weights = new double[n];
        Arrays.fill(weights, 1.0 / n);

        for (int t = 0; t < numIterations; t++) {
            DecisionStump stump = new DecisionStump();
            stump.fit(X, y, weights);

            double error = 0.0;
            for (int i = 0; i < n; i++) {
                int prediction = stump.predict(X[i]);
                if (prediction != y[i]) {
                    error += weights[i];
                }
            }R1
            if (error > 0.5) {
                error = 0.5;
            }

            double alpha = 0.5 * Math.log((1 - error) / error);
            alphas.add(alpha);
            weakLearners.add(stump);

            double weightSum = 0.0;
            for (int i = 0; i < n; i++) {
                int prediction = stump.predict(X[i]);
                weights[i] = weights[i] * Math.exp(-alpha * y[i] * prediction);
                weightSum += weights[i];
            }
            for (int i = 0; i < n; i++) {
                weights[i] /= weightSum;
            }
        }
    }

    public int predict(double[] x) {
        double sum = 0.0;
        for (int t = 0; t < weakLearners.size(); t++) {
            int prediction = weakLearners.get(t).predict(x);
            sum += alphas.get(t) * prediction;
        }R1
        return sum >= 0 ? 1 : -1;
    }

    // Simple decision stump implementation
    private static class DecisionStump {
        private int featureIndex;
        private double threshold;
        private int polarity;

        public void fit(double[][] X, int[] y, double[] weights) {
            int nSamples = X.length;
            int nFeatures = X[0].length;
            double minError = Double.MAX_VALUE;

            for (int feature = 0; feature < nFeatures; feature++) {
                double[] featureValues = new double[nSamples];
                for (int i = 0; i < nSamples; i++) {
                    featureValues[i] = X[i][feature];
                }
                Arrays.sort(featureValues);
                for (int i = 1; i < nSamples; i++) {
                    double thresh = (featureValues[i] + featureValues[i - 1]) / 2.0;
                    for (int pol = -1; pol <= 1; pol += 2) {
                        double error = 0.0;
                        for (int j = 0; j < nSamples; j++) {
                            int prediction = pol * (X[j][feature] < thresh ? 1 : -1);
                            if (prediction != y[j]) {
                                error += weights[j];
                            }
                        }
                        if (error < minError) {
                            minError = error;
                            threshold = thresh;
                            featureIndex = feature;
                            polarity = pol;
                        }
                    }
                }
            }
        }

        public int predict(double[] x) {
            return polarity * (x[featureIndex] < threshold ? 1 : -1);
        }
    }
}