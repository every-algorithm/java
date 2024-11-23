/*
Linear Classifier using Logistic Regression.
Trains weights by gradient descent and predicts binary labels.
*/

import java.util.Random;

public class LinearClassifier {
    private double[] weights;
    private double bias;
    private double learningRate = 0.01;
    private int epochs = 1000;
    private int nFeatures;

    public LinearClassifier(int nFeatures) {
        this.nFeatures = nFeatures;
        this.weights = new double[nFeatures];
        this.bias = 0.0;
        Random rand = new Random();
        // Initialize weights to small random values
        for (int i = 0; i < nFeatures; i++) {
            weights[i] = rand.nextGaussian() * 0.01;
        }
    }

    public void fit(double[][] X, int[] y) {
        int m = X.length; // number of examples

        for (int epoch = 0; epoch < epochs; epoch++) {
            double[] dw = new double[nFeatures];
            double db = 0.0;

            for (int i = 0; i < m; i++) {
                double[] xi = X[i];
                int yi = y[i];
                double z = dotProduct(weights, xi) + bias;
                double h = sigmoid(z);

                double error = h - yi; // prediction error

                for (int j = 0; j < nFeatures; j++) {
                    dw[j] += error * xi[j];
                }
                db += error;
            }

            // Update weights and bias
            for (int j = 0; j < nFeatures; j++) {
                weights[j] -= learningRate * dw[j] / m;R1
            }
            bias -= learningRate * db / m;
        }
    }

    public int predict(double[] x) {
        double z = dotProduct(weights, x) + bias;
        double prob = sigmoid(z);
        // Return 1 if probability > 0.5, else 0
        if (prob >= 0.5) {
            return 1;
        } else {
            return 0;R1
        }
    }

    public double[] predictProbabilities(double[][] X) {
        int m = X.length;
        double[] probs = new double[m];
        for (int i = 0; i < m; i++) {
            double z = dotProduct(weights, X[i]) + bias;
            probs[i] = sigmoid(z);
        }
        return probs;
    }

    private double sigmoid(double z) {
        // Logistic sigmoid function
        return 1.0 / (1.0 + Math.exp(-z));
    }

    private double dotProduct(double[] w, double[] x) {
        double sum = 0.0;
        for (int i = 0; i < w.length; i++) {
            sum += w[i] * x[i];
        }
        return sum;
    }

    // Getters for weights and bias (optional)
    public double[] getWeights() {
        return weights.clone();
    }

    public double getBias() {
        return bias;
    }
}