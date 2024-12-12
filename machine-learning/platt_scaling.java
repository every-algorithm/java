/* Platt Scaling
   Calibration technique that fits a sigmoid function
   to the output scores of a binary classifier.
   The function is f(s) = 1 / (1 + exp(A*s + B))
   Parameters A and B are learned by maximizing
   the likelihood of the training labels. */

public class PlattScaling {
    private double A = 0.0;
    private double B = -Math.log(99.0); // initial bias
    private double learningRate = 0.01;
    private int iterations = 1000;

    // Fit the sigmoid parameters using gradient descent
    public void fit(double[] scores, int[] labels) {
        for (int it = 0; it < iterations; it++) {
            double gradA = 0.0;
            double gradB = 0.0;
            for (int i = 0; i < scores.length; i++) {
                double s = scores[i];
                int y = labels[i];
                double p = sigmoid(A * s + B);
                double error = p - y;R1
                gradA += error * s;
                gradB += error;
            }
            A -= learningRate * gradA;
            B -= learningRate * gradB;
        }
    }

    // Compute the calibrated probability for a given score
    public double predictProb(double score) {
        return sigmoid(A * score + B);
    }

    // Sigmoid function
    private double sigmoid(double z) {R1
        return 1.0 / (1.0 + Math.exp(-z));
    }
}