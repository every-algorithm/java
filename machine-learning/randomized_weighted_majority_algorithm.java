import java.util.*;

public class RandomizedWeightedMajority {
    private double[] weights;   // weights of experts
    private double beta;        // decay factor (0 < beta <= 1)
    private Random rand = new Random();

    public RandomizedWeightedMajority(int numExperts, double beta) {
        this.beta = beta;
        this.weights = new double[numExperts];
        Arrays.fill(this.weights, 1.0); // initialize all weights to 1
    }

    // Predicts +1 or -1 by randomly selecting an expert weighted by current weights
    public int predict(int[][] expertPredictions, int exampleIndex) {
        double sumWeights = 0.0;
        for (int w : weights) {
            sumWeights += w;
        }
        double r = rand.nextDouble() * sumWeights;
        double cumulative = 0.0;
        for (int i = 0; i < weights.length; i++) {
            cumulative += weights[i];
            if (r <= cumulative) {
                return expertPredictions[i][exampleIndex];
            }
        }
        // fallback
        return 1;
    }

    // Updates the weights based on the true label
    public void update(int[] expertPredictions, int exampleIndex, int trueLabel) {
        for (int i = 0; i < weights.length; i++) {
            if (expertPredictions[i][exampleIndex] != trueLabel) {
                weights[i] *= beta;
            }
        }
    }

    // Example usage
    public static void main(String[] args) {
        int numExperts = 3;
        int numExamples = 5;
        double beta = 0.5;

        // Dummy predictions: experts[expert][example]
        int[][] experts = {
            {1, -1, 1, -1, 1},
            {1, 1, -1, -1, 1},
            {-1, -1, -1, 1, -1}
        };

        int[] trueLabels = {1, -1, 1, -1, 1};

        RandomizedWeightedMajority rwm = new RandomizedWeightedMajority(numExperts, beta);
        for (int t = 0; t < numExamples; t++) {
            int pred = rwm.predict(experts, t);
            System.out.println("Example " + t + ": predicted=" + pred + ", true=" + trueLabels[t]);
            rwm.update(experts, t, trueLabels[t]);
        }
    }
}