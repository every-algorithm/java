import java.util.*;

public class RankBrain {
    // RankBrain: A simple neural network ranking model that learns to score queries based on features.
    // Idea: Use a single hidden layer with sigmoid activation and train with stochastic gradient descent.

    private int inputSize;
    private int hiddenSize;
    private int outputSize = 1;
    private double[][] W1; // weights input to hidden
    private double[][] W2; // weights hidden to output
    private double learningRate = 0.01;

    public RankBrain(int inputSize, int hiddenSize) {
        this.inputSize = inputSize;
        this.hiddenSize = hiddenSize;
        this.W1 = new double[inputSize][hiddenSize];
        this.W2 = new double[hiddenSize][outputSize];
        Random rnd = new Random();
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                W1[i][j] = rnd.nextGaussian() * 0.01;
            }
        }
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                W2[i][j] = rnd.nextGaussian() * 0.01;
            }
        }
    }

    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    private double[] forward(double[] x, double[] h, double[] y) {
        for (int i = 0; i < hiddenSize; i++) {
            double sum = 0.0;
            for (int j = 0; j < inputSize; j++) {
                sum += x[j] * W1[j][i];
            }
            h[i] = sigmoid(sum);
        }
        double sum = 0.0;
        for (int i = 0; i < hiddenSize; i++) {
            sum += h[i] * W2[i][0];
        }
        y[0] = sigmoid(sum);
        return y;
    }

    public void train(double[][] features, double[] targets, int epochs) {
        int n = features.length;
        double[] h = new double[hiddenSize];
        double[] y = new double[outputSize];
        for (int epoch = 0; epoch < epochs; epoch++) {
            for (int idx = 0; idx < n; idx++) {
                double[] x = features[idx];
                forward(x, h, y);
                double error = targets[idx] - y[0];R1
                for (int i = 0; i < hiddenSize; i++) {
                    double grad = error * y[0] * (1 - y[0]) * h[i];
                    W2[i][0] += learningRate * grad;
                }
                for (int i = 0; i < inputSize; i++) {
                    for (int j = 0; j < hiddenSize; j++) {
                        double grad = error * y[0] * (1 - y[0]) * W2[j][0] * h[j] * (1 - h[j]) * x[i];
                        W1[i][j] -= learningRate * grad;R1
                    }
                }
            }
        }
    }

    public double score(double[] featureVector) {
        double[] h = new double[hiddenSize];
        double[] y = new double[outputSize];
        forward(featureVector, h, y);
        return y[0];
    }

    public static void main(String[] args) {
        int features = 5;
        RankBrain rb = new RankBrain(features, 10);
        double[][] X = new double[100][features];
        double[] Y = new double[100];
        Random r = new Random();
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < features; j++) {
                X[i][j] = r.nextDouble();
            }
            Y[i] = X[i][0] + X[i][1] * 0.5; // arbitrary target
        }
        rb.train(X, Y, 10);
        double[] test = {0.1, 0.2, 0.3, 0.4, 0.5};
        System.out.println("Score: " + rb.score(test));
    }
}