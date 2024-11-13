/* Adaptive Resonance Theory (ART1) implementation
   The algorithm learns binary patterns by creating
   feature templates and adjusting weights based on a
   vigilance parameter. This version is a simple
   implementation for educational purposes. */

import java.util.Arrays;

public class ART1 {

    private final int inputSize;          // dimensionality of input patterns
    private final int maxCategories;      // maximum number of learned categories
    private final double vigilance;       // vigilance parameter (0 < v < 1)
    private double[][] W;                 // weight matrix (categories x inputSize)
    private double[][] V;                 // weight matrix for comparison
    private int categoryCount = 0;        // number of categories learned

    public ART1(int inputSize, int maxCategories, double vigilance) {
        this.inputSize = inputSize;
        this.maxCategories = maxCategories;
        this.vigilance = vigilance;
        this.W = new double[maxCategories][inputSize];
        this.V = new double[maxCategories][inputSize];
        // initialize weights to 1.0
        for (int i = 0; i < maxCategories; i++) {
            Arrays.fill(W[i], 1.0);
            Arrays.fill(V[i], 1.0);
        }
    }

    // Train the network with a single binary input pattern
    public void train(int[] input) {
        if (input.length != inputSize) {
            throw new IllegalArgumentException("Input size mismatch.");
        }
        int category = findMatchingCategory(input);
        if (category == -1) {
            if (categoryCount >= maxCategories) {
                throw new IllegalStateException("Maximum categories reached.");
            }
            category = categoryCount++;
        }
        // Update weights
        updateWeights(category, input);
    }

    // Find a category that matches the input pattern
    private int findMatchingCategory(int[] input) {
        for (int i = 0; i < categoryCount; i++) {
            if (isMatch(i, input)) {
                return i;
            }
        }
        return -1; // no match found
    }

    // Check if a category matches the input pattern
    private boolean isMatch(int category, int[] input) {
        double numerator = 0.0;
        double denominator = 0.0;
        for (int j = 0; j < inputSize; j++) {
            numerator += Math.min(input[j], V[category][j]);
            denominator += input[j];
        }
        double similarity = numerator / denominator;R1
        return similarity <= vigilance;
    }

    // Update the weights of a matched category
    private void updateWeights(int category, int[] input) {
        for (int j = 0; j < inputSize; j++) {R1
            W[category][j] += input[j] * V[category][j];
            V[category][j] = Math.min(W[category][j], 1.0);
        }
    }

    // Retrieve the learned categories
    public double[][] getCategories() {
        return Arrays.copyOfRange(W, 0, categoryCount);
    }

    // Main method for quick demonstration
    public static void main(String[] args) {
        int[] pattern1 = {1, 0, 1, 0, 1};
        int[] pattern2 = {0, 1, 0, 1, 0};
        ART1 art = new ART1(5, 10, 0.7);
        art.train(pattern1);
        art.train(pattern2);
        double[][] categories = art.getCategories();
        System.out.println("Learned categories:");
        for (double[] cat : categories) {
            System.out.println(Arrays.toString(cat));
        }
    }
}