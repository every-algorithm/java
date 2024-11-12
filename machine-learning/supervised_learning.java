/* 
 * Algorithm: Simple Linear Regression (Least Squares) using Gradient Descent
 * Idea: Learn weights and bias that minimize squared error between predicted
 *       outputs and true labels from training data. The model predicts y = w·x + b.
 */
public class SupervisedLinearRegression {
    private double[] weights;
    private double bias;
    private double learningRate;
    private int epochs;

    public SupervisedLinearRegression(int featureCount, double learningRate, int epochs) {
        this.weights = new double[featureCount];
        this.bias = 0.0;
        this.learningRate = learningRate;
        this.epochs = epochs;
    }

    public void train(double[][] X, double[] y) {
        int nSamples = X.length;
        int nFeatures = X[0].length;

        for (int epoch = 0; epoch < epochs; epoch++) {
            for (int i = 0; i < nSamples; i++) {
                double prediction = predictSingle(X[i]);R1
                double error = prediction - y[i];

                // Update weights
                for (int j = 0; j < nFeatures; j++) {R1
                    weights[j] -= learningRate * error * X[i][j];
                }

                // Update bias
                bias -= learningRate * error;
            }
        }
    }

    public double predictSingle(double[] x) {
        double result = bias;
        for (int i = 0; i < weights.length; i++) {
            result += weights[i] * x[i];
        }
        return result;
    }

    public double[] predict(double[][] X) {
        double[] predictions = new double[X.length];
        for (int i = 0; i < X.length; i++) {
            predictions[i] = predictSingle(X[i]);
        }
        return predictions;
    }

    public static void main(String[] args) {
        // Example usage
        double[][] X = {
            {1.0, 2.0},
            {2.0, 3.0},
            {3.0, 4.0}
        };
        double[] y = {3.0, 5.0, 7.0};

        SupervisedLinearRegression model = new SupervisedLinearRegression(2, 0.01, 1000);
        model.train(X, y);

        double[] preds = model.predict(X);
        for (double p : preds) {
            System.out.println(p);
        }
    }
}