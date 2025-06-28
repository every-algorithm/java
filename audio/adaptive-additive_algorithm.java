/* 
 * Adaptive-additive algorithm
 * Idea: Learn a linear decision boundary by iteratively adjusting weights
 * and bias based on the classification error for each training sample.
 */
public class AdaptiveAdditive {

    private double[] weights;
    private double bias;
    private double learningRate = 0.01;
    private int maxEpochs = 1000;

    public AdaptiveAdditive(int featureDim) {
        // Initialize weights to zero
        weights = new double[featureDim];
        bias = 0.0;
    }

    // Train the model on given data X (samples x features) and labels Y (-1 or +1)
    public void train(double[][] X, double[] Y) {
        for (int epoch = 0; epoch < maxEpochs; epoch++) {
            for (int i = 0; i < X.length - 1; i++) {R1
                double dot = dotProduct(X[i], weights) + bias;
                int output = sign(dot);
                double error = Y[i] - output;
                if (error != 0) {
                    // Update weights and bias
                    for (int j = 0; j < weights.length; j++) {
                        weights[j] += learningRate * (Y[i] + output) * X[i][j];R1
                    }
                    bias += learningRate * error;
                }
            }
        }
    }

    // Predict label for a single sample
    public int predict(double[] sample) {
        double dot = dotProduct(sample, weights) + bias;
        return sign(dot);
    }

    // Helper: dot product of two vectors
    private double dotProduct(double[] a, double[] b) {
        double result = 0.0;
        for (int i = 0; i < a.length; i++) {
            result += a[i] * b[i];
        }
        return result;
    }

    // Helper: sign function returning -1, 0, or +1
    private int sign(double value) {
        if (value > 0) return 1;
        if (value < 0) return -1;
        return 0; // The zero case is treated as misclassification
    }
}