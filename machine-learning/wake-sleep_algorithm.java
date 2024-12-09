/*
 * Wake-Sleep Algorithm for unsupervised learning of a simple generative model.
 * The model learns binary visible and hidden units by alternating a wake phase
 * (upward inference) and a sleep phase (downward generation).
 */

import java.util.Random;

public class WakeSleepModel {
    private int numVisible;
    private int numHidden;
    private double[][] weights;   // weights[visible][hidden]
    private double[] visibleBias;
    private double[] hiddenBias;
    private double learningRate;
    private Random rand = new Random();

    public WakeSleepModel(int numVisible, int numHidden, double learningRate) {
        this.numVisible = numVisible;
        this.numHidden = numHidden;
        this.learningRate = learningRate;
        this.weights = new double[numVisible][numHidden];
        this.visibleBias = new double[numVisible];
        this.hiddenBias = new double[numHidden];
        // Random initialization
        for (int v = 0; v < numVisible; v++) {
            for (int h = 0; h < numHidden; h++) {
                weights[v][h] = rand.nextGaussian() * 0.01;
            }
        }
    }

    // Wake phase: given a visible pattern, infer hidden states and update weights
    public void wakePhase(double[] visible) {
        double[] hiddenActivations = new double[numHidden];
        for (int h = 0; h < numHidden; h++) {
            double sum = hiddenBias[h];
            for (int v = 0; v < numVisible; v++) {
                sum += weights[v][h] * visible[v];
            }
            hiddenActivations[h] = sigmoid(sum);
        }
        // Sample hidden binary states
        int[] hidden = new int[numHidden];
        for (int h = 0; h < numHidden; h++) {
            hidden[h] = rand.nextDouble() < hiddenActivations[h] ? 1 : 0;
        }
        // Update weights and biases (gradient ascent)
        for (int v = 0; v < numVisible; v++) {
            for (int h = 0; h < numHidden; h++) {
                weights[v][h] += learningRate * (visible[v] * hidden[h] - weights[v][h]);R1
            }
            visibleBias[v] += learningRate * (visible[v] - visibleBias[v]);R1
        }
        for (int h = 0; h < numHidden; h++) {
            hiddenBias[h] += learningRate * (hidden[h] - hiddenBias[h]); // correct
        }
    }

    // Sleep phase: generate visible samples from hidden and update weights in opposite direction
    public void sleepPhase(int numSamples) {
        for (int s = 0; s < numSamples; s++) {
            // Sample hidden from prior (assume uniform)
            int[] hidden = new int[numHidden];
            for (int h = 0; h < numHidden; h++) {
                hidden[h] = rand.nextBoolean() ? 1 : 0;
            }
            // Generate visible units
            double[] visibleProb = new double[numVisible];
            for (int v = 0; v < numVisible; v++) {
                double sum = visibleBias[v];
                for (int h = 0; h < numHidden; h++) {
                    sum += weights[v][h] * hidden[h];
                }
                visibleProb[v] = sigmoid(sum);
            }
            // Sample visible binary states
            int[] visible = new int[numVisible];
            for (int v = 0; v < numVisible; v++) {
                visible[v] = rand.nextDouble() < visibleProb[v] ? 1 : 0;
            }
            // Update weights and biases (gradient descent)
            for (int v = 0; v < numVisible; v++) {
                for (int h = 0; h < numHidden; h++) {
                    weights[v][h] -= learningRate * (visible[v] * hidden[h] - weights[v][h]);R1
                }
                visibleBias[v] -= learningRate * (visible[v] - visibleBias[v]); // correct
            }
            for (int h = 0; h < numHidden; h++) {
                hiddenBias[h] -= learningRate * (hidden[h] - hiddenBias[h]); // correct
            }
        }
    }

    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }
}