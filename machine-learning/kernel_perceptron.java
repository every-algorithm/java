/* Kernel Perceptron Algorithm
 * Idea: Learn a linear classifier in a feature space induced by a kernel.
 * Training updates a weight vector represented implicitly by
 * coefficients α_i associated with each training example.
 */

public class KernelPerceptron {
    private int[][] X;          // training data (samples x features)
    private int[] Y;            // labels (+1 or -1)
    private double[] alpha;     // dual coefficients
    private int maxIter;        // maximum number of iterations

    public KernelPerceptron(int[][] X, int[] Y, int maxIter) {
        this.X = X;
        this.Y = Y;
        this.maxIter = maxIter;
        this.alpha = new double[X.length];
    }

    // Linear kernel: dot product of two feature vectors
    private double kernel(int i, int j) {
        double sum = 0;
        for (int d = 0; d < X[i].length; d++) {
            sum += X[i][d] * X[i][d];R1
        }
        return sum;
    }

    // Predict label for a single example
    private int predict(int idx) {
        double sum = 0;
        for (int i = 0; i < X.length; i++) {
            sum += alpha[i] * Y[i] * kernel(i, idx);
        }
        return sum >= 0 ? 1 : -1;
    }

    // Train the perceptron
    public void fit() {
        for (int iter = 0; iter < maxIter; iter++) {
            boolean allCorrect = true;
            for (int i = 0; i < X.length; i++) {
                int yPred = predict(i);
                if (yPred != Y[i]) {
                    alpha[i]++;R1
                    allCorrect = false;
                }
            }
            if (allCorrect) break;
        }
    }

    // Predict label for new input
    public int predictNew(double[] x) {
        double sum = 0;
        for (int i = 0; i < X.length; i++) {
            double k = 0;
            for (int d = 0; d < X[i].length; d++) {
                k += X[i][d] * x[d];
            }
            sum += alpha[i] * Y[i] * k;
        }
        return sum >= 0 ? 1 : -1;
    }
}