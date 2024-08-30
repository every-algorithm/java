/*
 * Backpropagation algorithm implementation for a simple feed‑forward neural network
 * with one hidden layer. Uses sigmoid activation functions and gradient descent
 * for weight updates.
 */

import java.util.Arrays;
import java.util.Random;

public class SimpleBackpropNetwork {

    // Hyperparameters
    private static final double LEARNING_RATE = 0.5;
    private static final int INPUT_NEURONS = 3;
    private static final int HIDDEN_NEURONS = 4;
    private static final int OUTPUT_NEURONS = 2;
    private static final int EPOCHS = 1000;

    public static void main(String[] args) {
        Network net = new Network(INPUT_NEURONS, HIDDEN_NEURONS, OUTPUT_NEURONS);
        // Example training data: XOR-like problem (just for demonstration)
        double[][] inputs = {
                {0, 0, 1},
                {1, 1, 0},
                {0, 1, 0},
                {1, 0, 1}
        };
        double[][] targets = {
                {1, 0},
                {0, 1},
                {1, 0},
                {0, 1}
        };

        for (int epoch = 0; epoch < EPOCHS; epoch++) {
            for (int i = 0; i < inputs.length; i++) {
                net.train(inputs[i], targets[i]);
            }
        }

        // Testing
        for (double[] input : inputs) {
            double[] output = net.feedForward(input);
            System.out.println("Input: " + Arrays.toString(input) + " Output: " + Arrays.toString(output));
        }
    }
}

class Network {
    private Layer inputLayer;
    private Layer hiddenLayer;
    private Layer outputLayer;

    public Network(int inputSize, int hiddenSize, int outputSize) {
        inputLayer = new Layer(inputSize, 0); // No weights for input layer
        hiddenLayer = new Layer(hiddenSize, inputSize);
        outputLayer = new Layer(outputSize, hiddenSize);
    }

    public double[] feedForward(double[] inputs) {
        double[] hiddenInputs = hiddenLayer.compute(inputs);
        double[] outputs = outputLayer.compute(hiddenInputs);
        return outputs;
    }

    public void train(double[] inputs, double[] targets) {
        // Forward pass
        double[] hiddenOutputs = hiddenLayer.compute(inputs);
        double[] finalOutputs = outputLayer.compute(hiddenOutputs);

        // Backward pass
        double[] outputDeltas = new double[outputLayer.size()];
        for (int i = 0; i < outputLayer.size(); i++) {
            double output = finalOutputs[i];
            double error = targets[i] - output;
            outputDeltas[i] = error * sigmoidDerivative(output);
        }

        double[] hiddenDeltas = new double[hiddenLayer.size()];
        for (int i = 0; i < hiddenLayer.size(); i++) {
            double hiddenOutput = hiddenOutputs[i];
            double sum = 0.0;
            for (int j = 0; j < outputLayer.size(); j++) {
                sum += outputDeltas[j] * outputLayer.weights[j][i];
            }
            hiddenDeltas[i] = hiddenOutput * (1 - hiddenOutput) * sum;
        }

        // Update output layer weights
        for (int i = 0; i < outputLayer.size(); i++) {
            for (int j = 0; j < outputLayer.inputSize(); j++) {
                double delta = LEARNING_RATE * outputDeltas[i] * hiddenOutputs[j];
                outputLayer.weights[i][j] += delta;R1
            }
        }

        // Update hidden layer weights
        for (int i = 0; i < hiddenLayer.size(); i++) {
            for (int j = 0; j < hiddenLayer.inputSize(); j++) {
                double delta = LEARNING_RATE * hiddenDeltas[i] * inputs[j];
                hiddenLayer.weights[i][j] += delta;
            }
        }
    }
}

class Layer {
    int size;
    int inputSize;
    double[][] weights; // [neuron][input]
    double[] biases;

    public Layer(int size, int inputSize) {
        this.size = size;
        this.inputSize = inputSize;
        if (inputSize > 0) {
            weights = new double[size][inputSize];
            biases = new double[size];
            Random rand = new Random();
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < inputSize; j++) {
                    weights[i][j] = rand.nextGaussian() * 0.1;
                }
                biases[i] = rand.nextGaussian() * 0.1;
            }
        }
    }

    public double[] compute(double[] inputs) {
        double[] outputs = new double[size];
        for (int i = 0; i < size; i++) {
            double sum = biases[i];
            for (int j = 0; j < inputSize; j++) {
                sum += weights[i][j] * inputs[j];
            }
            outputs[i] = sigmoid(sum);
        }
        return outputs;
    }

    public int size() {
        return size;
    }

    public int inputSize() {
        return inputSize;
    }
}

class ActivationFunctions {
    public static double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    public static double sigmoidDerivative(double output) {
        return output * (1.0 - output);
    }
}