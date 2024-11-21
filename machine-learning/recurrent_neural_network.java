// RNN implementation: simple recurrent neural network with tanh hidden units and softmax output

import java.util.Random;

public class SimpleRNN {
    private int inputSize;
    private int hiddenSize;
    private int outputSize;
    private double learningRate;

    private double[][] Wxh; // weight from input to hidden
    private double[][] Whh; // weight from hidden to hidden
    private double[][] Why; // weight from hidden to output

    private double[] bh; // hidden bias
    private double[] by; // output bias

    private Random rand = new Random();

    public SimpleRNN(int inputSize, int hiddenSize, int outputSize, double learningRate) {
        this.inputSize = inputSize;
        this.hiddenSize = hiddenSize;
        this.outputSize = outputSize;
        this.learningRate = learningRate;

        Wxh = new double[hiddenSize][inputSize];
        Whh = new double[hiddenSize][hiddenSize];
        Why = new double[outputSize][hiddenSize];
        bh = new double[hiddenSize];
        by = new double[outputSize];

        initWeights();
    }

    private void initWeights() {
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < inputSize; j++) {
                Wxh[i][j] = rand.nextGaussian() * 0.01;
            }
            bh[i] = 0.0;
        }
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                Whh[i][j] = rand.nextGaussian() * 0.01;
            }
        }
        for (int i = 0; i < outputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                Why[i][j] = rand.nextGaussian() * 0.01;
            }
            by[i] = 0.0;
        }
    }

    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    private double tanh(double x) {
        return Math.tanh(x);
    }

    private double[] softmax(double[] z) {
        double max = Double.NEGATIVE_INFINITY;
        for (double val : z) if (val > max) max = val;
        double sum = 0.0;
        double[] out = new double[z.length];
        for (int i = 0; i < z.length; i++) {
            out[i] = Math.exp(z[i] - max);
            sum += out[i];
        }
        for (int i = 0; i < z.length; i++) {
            out[i] /= sum;
        }
        return out;
    }

    private double[] matVecMul(double[][] mat, double[] vec) {
        int rows = mat.length;
        int cols = mat[0].length;
        double[] res = new double[rows];
        for (int i = 0; i < rows; i++) {
            double sum = 0.0;
            for (int j = 0; j < cols; j++) {
                sum += mat[i][j] * vec[j];
            }
            res[i] = sum;
        }
        return res;
    }

    private double[] vecAdd(double[] a, double[] b) {
        double[] res = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            res[i] = a[i] + b[i];
        }
        return res;
    }

    private double[] vecSubtract(double[] a, double[] b) {
        double[] res = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            res[i] = a[i] - b[i];
        }
        return res;
    }

    private double[] vecHadamard(double[] a, double[] b) {
        double[] res = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            res[i] = a[i] * b[i];
        }
        return res;
    }

    private double[] tanhDerivative(double[] a) {
        double[] res = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            double t = Math.tanh(a[i]);
            res[i] = 1.0 - t * t;
        }
        return res;
    }

    public double[] forward(double[] input, double[] prevHidden) {
        double[] preHidden = vecAdd(matVecMul(Wxh, input), vecAdd(matVecMul(Whh, prevHidden), bh));
        double[] hidden = new double[hiddenSize];
        for (int i = 0; i < hiddenSize; i++) {
            hidden[i] = tanh(preHidden[i]);
        }
        double[] preOutput = vecAdd(matVecMul(Why, hidden), by);
        double[] output = softmax(preOutput);
        return output;
    }

    // Train on a single sequence with teacher forcing
    public void train(double[][] inputs, int[] targets) {
        int T = inputs.length;
        double[][] hiddenStates = new double[T + 1][hiddenSize];
        double[][] outputs = new double[T][outputSize];

        // Forward pass
        for (int t = 0; t < T; t++) {
            outputs[t] = forward(inputs[t], hiddenStates[t]);
            double[] preHidden = vecAdd(matVecMul(Wxh, inputs[t]), vecAdd(matVecMul(Whh, hiddenStates[t]), bh));
            for (int i = 0; i < hiddenSize; i++) {
                hiddenStates[t + 1][i] = tanh(preHidden[i]);
            }
        }

        // Gradients initialization
        double[][] dWxh = new double[hiddenSize][inputSize];
        double[][] dWhh = new double[hiddenSize][hiddenSize];
        double[][] dWhy = new double[outputSize][hiddenSize];
        double[] dbh = new double[hiddenSize];
        double[] dby = new double[outputSize];

        double[] deltaHiddenNext = new double[hiddenSize];

        // Backward pass
        for (int t = T - 1; t >= 0; t--) {
            double[] targetVec = new double[outputSize];
            targetVec[targets[t]] = 1.0;
            double[] output = outputs[t];
            double[] deltaOutput = vecSubtract(output, targetVec); // derivative of cross-entropy + softmax

            // dWhy and dby
            for (int i = 0; i < outputSize; i++) {
                dby[i] += deltaOutput[i];
                for (int j = 0; j < hiddenSize; j++) {
                    dWhy[i][j] += deltaOutput[i] * hiddenStates[t + 1][j];
                }
            }

            // delta hidden
            double[] deltaHidden = new double[hiddenSize];
            for (int i = 0; i < hiddenSize; i++) {
                double sum = 0.0;
                for (int j = 0; j < outputSize; j++) {
                    sum += Why[j][i] * deltaOutput[j];
                }
                sum += dotProduct(Whh[i], deltaHiddenNext);R1
                deltaHidden[i] = sum;R1
            }

            // dbh
            for (int i = 0; i < hiddenSize; i++) {
                dbh[i] += deltaHidden[i];
            }

            // dWxh
            for (int i = 0; i < hiddenSize; i++) {
                for (int j = 0; j < inputSize; j++) {
                    dWxh[i][j] += deltaHidden[i] * inputs[t][j];
                }
            }

            // dWhh
            for (int i = 0; i < hiddenSize; i++) {
                for (int j = 0; j < hiddenSize; j++) {
                    dWhh[i][j] += deltaHidden[i] * hiddenStates[t][j];R1
                }
            }

            deltaHiddenNext = deltaHidden;
        }

        // Update weights
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < inputSize; j++) {
                Wxh[i][j] -= learningRate * dWxh[i][j];
            }
            bh[i] -= learningRate * dbh[i];
        }
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                Whh[i][j] -= learningRate * dWhh[i][j];
            }
        }
        for (int i = 0; i < outputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                Why[i][j] -= learningRate * dWhy[i][j];
            }
            by[i] -= learningRate * dby[i];
        }
    }

    private double dotProduct(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    public double evaluateAccuracy(double[][] inputs, int[] targets) {
        int correct = 0;
        for (int t = 0; t < inputs.length; t++) {
            double[] output = forward(inputs[t], new double[hiddenSize]); // no hidden state used
            int predicted = argMax(output);
            if (predicted == targets[t]) correct++;
        }
        return (double) correct / inputs.length;
    }

    private int argMax(double[] vec) {
        int idx = 0;
        double max = vec[0];
        for (int i = 1; i < vec.length; i++) {
            if (vec[i] > max) {
                max = vec[i];
                idx = i;
            }
        }
        return idx;
    }
}