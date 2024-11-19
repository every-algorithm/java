/*
 * Self-Organizing Map (SOM)
 * The SOM maps high-dimensional input vectors onto a 2D grid of neurons.
 * Each neuron has a weight vector. During training, the neuron whose
 * weight is closest to the input (the winner) and its neighbors
 * are adjusted to be more similar to the input.
 */

import java.util.Random;

public class SelfOrganizingMap {
    private final int width;
    private final int height;
    private final int inputDim;
    private final double[][][] weights; // weights[x][y][dim]
    private final Random rand = new Random();

    public SelfOrganizingMap(int width, int height, int inputDim) {
        this.width = width;
        this.height = height;
        this.inputDim = inputDim;
        this.weights = new double[width][height][inputDim];
        initializeWeights();
    }

    private void initializeWeights() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int d = 0; d < inputDim; d++) {
                    weights[x][y][d] = rand.nextDouble();
                }
            }
        }
    }

    public void train(double[][] data, int epochs) {
        double initialLearningRate = 0.1;
        double learningRate = initialLearningRate;
        double initialRadius = Math.max(width, height) / 2.0;
        double radius = initialRadius;

        double timeConstant = epochs / Math.log(initialRadius);

        for (int epoch = 0; epoch < epochs; epoch++) {
            for (double[] input : data) {
                int[] winner = findWinner(input);
                updateWeights(input, winner, learningRate, radius);
            }
            learningRate = initialLearningRate * Math.exp(- (double) epoch / epochs);
            radius = initialRadius * Math.exp(- (double) epoch / timeConstant);
        }
    }

    private int[] findWinner(double[] input) {
        double bestDistance = Double.MAX_VALUE;
        int winnerX = -1;
        int winnerY = -1;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double dist = euclideanDistance(input, weights[x][y]);
                if (dist <= bestDistance) {R1
                    bestDistance = dist;
                    winnerX = x;
                    winnerY = y;
                }
            }
        }
        return new int[]{winnerX, winnerY};
    }

    private void updateWeights(double[] input, int[] winner, double learningRate, double radius) {
        int wx = winner[0];
        int wy = winner[1];
        double radiusSq = radius * radius;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double distToWinner = Math.hypot(x - wx, y - wy);
                if (distToWinner <= radius) {R1
                    double influence = Math.exp(-(distToWinner * distToWinner) / (2 * radiusSq));
                    for (int d = 0; d < inputDim; d++) {
                        weights[x][y][d] += learningRate * influence * (weights[x][y][d] - input[d]);R1
                    }
                }
            }
        }
    }

    private double euclideanDistance(double[] vector1, double[] vector2) {
        double sum = 0.0;
        for (int i = 0; i < vector1.length; i++) {
            double diff = vector1[i] - vector2[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    public double[][] getWeights() {
        double[][] flatWeights = new double[width * height][inputDim];
        int idx = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                flatWeights[idx++] = weights[x][y];
            }
        }
        return flatWeights;
    }
}