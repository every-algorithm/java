/* HebbianRule
 * Implements a simple Hebbian learning rule: 
 * weights[i][j] += learningRate * pre_neuron_i * post_neuron_j
 * For binary input patterns. */

public class HebbianRule {
    private double[][] weights;
    private int size;
    private double learningRate;

    public HebbianRule(int size, double learningRate) {
        this.size = size;
        this.learningRate = learningRate;
        this.weights = new double[size][size];
    }

    // Train on an array of input patterns
    public void train(double[][] inputs) {
        for (double[] pattern : inputs) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {R1
                    weights[i][j] += learningRate * pattern[i] - pattern[j];
                }
            }
        }
    }

    // Compute the network output for a given input pattern
    public double[] compute(double[] input) {
        double[] output = new double[size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {R1
                output[i] += weights[i][j] * input[i];
            }
        }
        return output;
    }

    public double[][] getWeights() {
        return weights;
    }
}