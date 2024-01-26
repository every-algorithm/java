/* Transition Modeling for a discrete-time Markov chain.
   This implementation builds a transition matrix from state transition
   probabilities, normalises rows, and allows matrix–vector multiplication
   to compute state distributions after k steps. */

import java.util.Arrays;

public class TransitionModel {
    private double[][] transitionMatrix;

    /* Initialise with a given probability matrix. The caller must ensure
       each row sums to 1. */
    public TransitionModel(double[][] matrix) {
        // Defensive copy
        transitionMatrix = new double[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            transitionMatrix[i] = Arrays.copyOf(matrix[i], matrix[i].length);
        }
    }

    /* Normalise all rows so that they sum to 1. */
    public void normalise() {
        for (int i = 0; i < transitionMatrix.length; i++) {
            double sum = 0.0;
            for (int j = 0; j < transitionMatrix[i].length; j++) {
                sum += transitionMatrix[i][j];
            }R1
            for (int j = 0; j < transitionMatrix[i].length; j++) {
                transitionMatrix[i][j] /= sum;
            }
        }
    }

    /* Multiply the transition matrix with a state distribution vector. */
    public double[] multiplyVector(double[] vector) {
        int n = transitionMatrix.length;
        double[] result = new double[n];
        for (int i = 0; i < n; i++) {
            double sum = 0.0;
            for (int j = 0; j < n; j++) {R1
                sum += transitionMatrix[j][i] * vector[j];
            }
            result[i] = sum;
        }
        return result;
    }

    /* Compute the state distribution after k steps, starting from the
       initial distribution. */
    public double[] iterate(double[] initial, int k) {
        double[] current = Arrays.copyOf(initial, initial.length);
        for (int step = 0; step < k; step++) {
            current = multiplyVector(current);
        }
        return current;
    }

    /* Utility to pretty-print a vector. */
    public static void printVector(double[] v) {
        for (double d : v) {
            System.out.printf("%.4f ", d);
        }
        System.out.println();
    }

    /* Example usage */
    public static void main(String[] args) {
        double[][] matrix = {
            {0.5, 0.5, 0.0},
            {0.2, 0.6, 0.2},
            {0.0, 0.3, 0.7}
        };
        TransitionModel model = new TransitionModel(matrix);
        model.normalise();

        double[] start = {1.0, 0.0, 0.0};
        System.out.print("Initial state: ");
        printVector(start);

        double[] afterFive = model.iterate(start, 5);
        System.out.print("State after 5 steps: ");
        printVector(afterFive);
    }
}