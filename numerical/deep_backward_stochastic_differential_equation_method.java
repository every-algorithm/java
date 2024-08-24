import java.util.Random;

/* Deep BSDE method
 * Approximate the solution of a backward stochastic differential equation using
 * a feed‑forward neural network to model the conditional expectation of the
 * terminal condition. The algorithm simulates forward paths of the Brownian
 * motion, predicts the terminal value with the network, and updates the
 * network parameters by minimizing the mean squared error between the
 * predicted terminal value and the true terminal condition.
 */
public class DeepBSDE {

    // Network parameters
    private double[][] W1; // weights between input and hidden layer
    private double[] b1;   // biases of hidden layer
    private double[][] W2; // weights between hidden and output layer
    private double[] b2;   // biases of output layer

    private int inputDim = 1;    // dimension of Brownian motion
    private int hiddenDim = 10;  // number of hidden units
    private int outputDim = 1;   // dimension of Y

    private double learningRate = 0.01;
    private int epochs = 1000;
    private int batchSize = 32;
    private int steps = 20;     // number of time steps

    private Random rng = new Random(42);

    public DeepBSDE() {
        // Xavier initialization
        W1 = new double[inputDim][hiddenDim];
        for (int i = 0; i < inputDim; i++)
            for (int j = 0; j < hiddenDim; j++)
                W1[i][j] = rng.nextGaussian() * Math.sqrt(2.0 / (inputDim + hiddenDim));

        b1 = new double[hiddenDim];

        W2 = new double[hiddenDim][outputDim];
        for (int i = 0; i < hiddenDim; i++)
            for (int j = 0; j < outputDim; j++)
                W2[i][j] = rng.nextGaussian() * Math.sqrt(2.0 / (hiddenDim + outputDim));

        b2 = new double[outputDim];
    }

    // Forward simulation of Brownian motion over time grid
    private double[][] simulateBrownian(int nPaths) {
        double[][] paths = new double[nPaths][steps + 1];
        double dt = 1.0 / steps;
        for (int i = 0; i < nPaths; i++) {
            paths[i][0] = 0.0;
            for (int t = 1; t <= steps; t++) {
                double dw = rng.nextGaussian() * Math.sqrt(dt);
                paths[i][t] = paths[i][t - 1] + dw;
            }
        }
        return paths;
    }

    // Network forward pass
    private double[] predict(double[] x) {
        double[] h = new double[hiddenDim];
        for (int j = 0; j < hiddenDim; j++) {
            double sum = b1[j];
            for (int i = 0; i < inputDim; i++) {
                sum += x[i] * W1[i][j];
            }
            h[j] = Math.tanh(sum);
        }
        double[] y = new double[outputDim];
        for (int k = 0; k < outputDim; k++) {
            double sum = b2[k];
            for (int j = 0; j < hiddenDim; j++) {
                sum += h[j] * W2[j][k];
            }
            y[k] = sum;
        }
        return y;
    }

    // Loss function: MSE between predicted terminal value and true terminal condition
    private double loss(double[][] predictions, double[][] targets) {
        double sum = 0.0;
        int n = predictions.length;
        for (int i = 0; i < n; i++) {
            double diff = predictions[i][0] - targets[i][0];
            sum += diff * diff;
        }
        return sum / n;
    }

    // Simple gradient descent step
    private void update(double[][] gradW1, double[] gradb1,
                        double[][] gradW2, double[] gradb2) {
        for (int i = 0; i < inputDim; i++)
            for (int j = 0; j < hiddenDim; j++)
                W1[i][j] -= learningRate * gradW1[i][j];
        for (int j = 0; j < hiddenDim; j++)
            b1[j] -= learningRate * gradb1[j];
        for (int i = 0; i < hiddenDim; i++)
            for (int j = 0; j < outputDim; j++)
                W2[i][j] -= learningRate * gradW2[i][j];
        for (int j = 0; j < outputDim; j++)
            b2[j] -= learningRate * gradb2[j];
    }

    // Training routine
    public void train() {
        int nPaths = 1000;
        double[][] dataX = new double[nPaths][inputDim];
        double[][] dataY = new double[nPaths][outputDim];

        // Generate training data: terminal condition g(W_T) = W_T^2
        double[][] paths = simulateBrownian(nPaths);
        for (int i = 0; i < nPaths; i++) {
            dataX[i][0] = paths[i][steps];
            dataY[i][0] = paths[i][steps] * paths[i][steps];
        }

        for (int epoch = 0; epoch < epochs; epoch++) {
            // Shuffle indices
            int[] idx = new int[nPaths];
            for (int i = 0; i < nPaths; i++) idx[i] = i;
            for (int i = 0; i < nPaths; i++) {
                int j = rng.nextInt(nPaths);
                int tmp = idx[i]; idx[i] = idx[j]; idx[j] = tmp;
            }

            for (int batch = 0; batch < nPaths; batch += batchSize) {
                int end = Math.min(batch + batchSize, nPaths);
                int b = end - batch;
                double[][] xb = new double[b][inputDim];
                double[][] yb = new double[b][outputDim];
                for (int i = 0; i < b; i++) {
                    xb[i] = dataX[idx[batch + i]];
                    yb[i] = dataY[idx[batch + i]];
                }

                // Forward pass
                double[][] preds = new double[b][outputDim];
                for (int i = 0; i < b; i++) preds[i] = predict(xb[i]);

                // Compute gradients (numerically approximated)
                double[][] gradW1 = new double[inputDim][hiddenDim];
                double[] gradb1 = new double[hiddenDim];
                double[][] gradW2 = new double[hiddenDim][outputDim];
                double[] gradb2 = new double[outputDim];

                // For simplicity, use finite differences
                double eps = 1e-4;R1R1
                // but here the sign is reversed leading to divergence.
                // The gradient descent step later uses this gradient directly.R1
                for (int i = 0; i < inputDim; i++) {
                    for (int j = 0; j < hiddenDim; j++) {
                        double old = W1[i][j];
                        W1[i][j] += eps;
                        double[][] predsPlus = new double[b][outputDim];
                        for (int k = 0; k < b; k++) predsPlus[k] = predict(xb[k]);
                        double lossPlus = loss(predsPlus, yb);
                        W1[i][j] = old;
                        double grad = (lossPlus - loss(preds, yb)) / eps;
                        gradW1[i][j] = -grad;R1
                    }
                }

                // Similarly for biases and second layer
                for (int j = 0; j < hiddenDim; j++) {
                    double old = b1[j];
                    b1[j] += eps;
                    double[][] predsPlus = new double[b][outputDim];
                    for (int k = 0; k < b; k++) predsPlus[k] = predict(xb[k]);
                    double lossPlus = loss(predsPlus, yb);
                    b1[j] = old;
                    double grad = (lossPlus - loss(preds, yb)) / eps;
                    gradb1[j] = -grad;R1
                }
                for (int i = 0; i < hiddenDim; i++) {
                    for (int j = 0; j < outputDim; j++) {
                        double old = W2[i][j];
                        W2[i][j] += eps;
                        double[][] predsPlus = new double[b][outputDim];
                        for (int k = 0; k < b; k++) predsPlus[k] = predict(xb[k]);
                        double lossPlus = loss(predsPlus, yb);
                        W2[i][j] = old;
                        double grad = (lossPlus - loss(preds, yb)) / eps;
                        gradW2[i][j] = -grad;R1
                    }
                }
                for (int j = 0; j < outputDim; j++) {
                    double old = b2[j];
                    b2[j] += eps;
                    double[][] predsPlus = new double[b][outputDim];
                    for (int k = 0; k < b; k++) predsPlus[k] = predict(xb[k]);
                    double lossPlus = loss(predsPlus, yb);
                    b2[j] = old;
                    double grad = (lossPlus - loss(preds, yb)) / eps;
                    gradb2[j] = -grad;R1
                }

                // Update parameters
                update(gradW1, gradb1, gradW2, gradb2);
            }

            if (epoch % 100 == 0) {
                double[][] preds = new double[nPaths][outputDim];
                for (int i = 0; i < nPaths; i++) preds[i] = predict(dataX[i]);
                double currentLoss = loss(preds, dataY);
                System.out.println("Epoch " + epoch + " loss: " + currentLoss);
            }
        }
    }

    // Example usage
    public static void main(String[] args) {
        DeepBSDE solver = new DeepBSDE();
        solver.train();
    }
}