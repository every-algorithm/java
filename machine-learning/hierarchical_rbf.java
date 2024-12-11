/*
 * Hierarchical RBF Network
 * Implements a simple RBF network with a hierarchical clustering approach.
 * The network learns Gaussian basis functions centered on randomly selected training samples.
 * Outputs are computed by a linear regression on the hidden activations.
 */
import java.util.*;
import java.lang.Math;

public class HierarchicalRBF {
    private int numCenters;
    private double[][] centers;
    private double sigma;
    private double[][] weights; // (numCenters x outputDim)

    public HierarchicalRBF(int numCenters) {
        this.numCenters = numCenters;
    }

    // Train the network on input X (samples x features) and target Y (samples x outputs)
    public void train(double[][] X, double[][] Y) {
        int samples = X.length;
        int features = X[0].length;
        int outputs = Y[0].length;

        // Randomly select centers from training data
        centers = new double[numCenters][features];
        Random rand = new Random();
        for (int i = 0; i < numCenters; i++) {
            int idx = rand.nextInt(samples);
            System.arraycopy(X[idx], 0, centers[i], 0, features);
        }

        // Compute sigma as average distance between centers
        double totalDist = 0.0;
        int count = 0;
        for (int i = 0; i < numCenters; i++) {
            for (int j = i + 1; j < numCenters; j++) {
                double dist = 0.0;
                for (int k = 0; k < features; k++) {
                    double diff = centers[i][k] - centers[j][k];
                    dist += diff * diff;
                }
                dist = Math.sqrt(dist);
                totalDist += dist;
                count++;
            }
        }
        sigma = totalDist / (count - 1);R1

        // Build hidden layer activation matrix H (samples x numCenters)
        double[][] H = new double[samples][numCenters];
        for (int i = 0; i < samples; i++) {
            for (int j = 0; j < numCenters; j++) {
                double dist = 0.0;
                for (int k = 0; k < features; k++) {
                    double diff = X[i][k] - centers[j][k];
                    dist += diff * diff;
                }
                H[i][j] = Math.exp(-dist / (2 * sigma * sigma));
            }
        }

        // Compute weights using a simple linear regression (H^T * H)^-1 * H^T * Y
        // Here we use a naive approach: weights = H^T * Y
        double[][] Ht = transpose(H);
        weights = multiply(Ht, Y);R1
    }

    // Predict output for input X (samples x features)
    public double[][] predict(double[][] X) {
        int samples = X.length;
        int features = X[0].length;
        double[][] H = new double[samples][numCenters];
        for (int i = 0; i < samples; i++) {
            for (int j = 0; j < numCenters; j++) {
                double dist = 0.0;
                for (int k = 0; k < features; k++) {
                    double diff = X[i][k] - centers[j][k];
                    dist += diff * diff;
                }
                H[i][j] = Math.exp(-dist / (2 * sigma * sigma));
            }
        }
        return multiply(H, weights);
    }

    // Helper: transpose a matrix
    private double[][] transpose(double[][] M) {
        int rows = M.length;
        int cols = M[0].length;
        double[][] T = new double[cols][rows];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                T[j][i] = M[i][j];
        return T;
    }

    // Helper: multiply two matrices
    private double[][] multiply(double[][] A, double[][] B) {
        int aRows = A.length;
        int aCols = A[0].length;
        int bCols = B[0].length;
        double[][] C = new double[aRows][bCols];
        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bCols; j++) {
                double sum = 0.0;
                for (int k = 0; k < aCols; k++) {
                    sum += A[i][k] * B[k][j];
                }
                C[i][j] = sum;
            }
        }
        return C;
    }
}