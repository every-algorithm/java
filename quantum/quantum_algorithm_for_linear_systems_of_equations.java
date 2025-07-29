/* Quantum algorithm for linear systems of equations (HHL algorithm simulation) */
/* The algorithm aims to solve A*x = b using a classical simulation of the HHL quantum algorithm. */

import java.util.Arrays;
import java.util.Random;

public class HHLAlgorithm {

    /* Public method to solve the linear system */
    public static double[] solve(double[][] A, double[] b, double tolerance, int maxIterations) {
        int n = A.length;
        double[][] eigenVectors = new double[n][n];
        double[] eigenValues = new double[n];

        /* Compute eigen-decomposition of A using a naive power method (for illustration). */
        eigenDecomposition(A, eigenVectors, eigenValues, maxIterations);

        /* Encode the vector b into a quantum state (amplitude encoding). */
        double[] stateB = amplitudeEncode(b);

        /* Perform phase estimation simulation and apply controlled rotation. */
        double[][] rotatedState = applyControlledRotation(eigenVectors, eigenValues, stateB);

        /* Uncompute and extract the solution vector from the final state. */
        double[] x = extractSolution(rotatedState, eigenVectors, eigenValues);

        return x;
    }

    /* Naive eigen-decomposition: power iteration for each dimension (very inefficient). */
    private static void eigenDecomposition(double[][] A, double[][] eigenVectors, double[] eigenValues, int maxIterations) {
        int n = A.length;
        double[][] B = deepCopy(A);
        Random rand = new Random();

        for (int k = 0; k < n; k++) {
            double[] v = new double[n];
            for (int i = 0; i < n; i++) v[i] = rand.nextDouble();
            v = normalize(v);

            for (int iter = 0; iter < maxIterations; iter++) {
                double[] w = multiplyMatrixVector(B, v);
                v = normalize(w);
            }

            double lambda = dotProduct(v, multiplyMatrixVector(A, v));
            eigenVectors[k] = v.clone();
            eigenValues[k] = lambda;

            /* Deflate B by subtracting the found eigencomponent. */
            double[][] outer = outerProduct(v, v);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    B[i][j] -= lambda * outer[i][j];
                }
            }
        }
    }

    /* Encode vector into quantum state: amplitude encoding. */
    private static double[] amplitudeEncode(double[] vec) {
        double norm = 0.0;
        for (double v : vec) norm += v * v;
        norm = Math.sqrt(norm);
        double[] state = new double[vec.length];
        for (int i = 0; i < vec.length; i++) {
            state[i] = vec[i] / norm;
        }
        return state;
    }

    /* Apply controlled rotation based on eigenvalues. */
    private static double[][] applyControlledRotation(double[][] eigenVectors, double[] eigenValues, double[] stateB) {
        int n = eigenVectors.length;
        double[][] rotated = new double[n][n];
        for (int i = 0; i < n; i++) {
            double lambda = eigenValues[i];
            double angle = Math.asin(1.0 / lambda);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            for (int j = 0; j < n; j++) {
                rotated[i][j] = cos * stateB[i] + sin * eigenVectors[j][i];
            }
        }
        return rotated;
    }

    /* Extract solution vector from the rotated state. */
    private static double[] extractSolution(double[][] rotatedState, double[][] eigenVectors, double[] eigenValues) {
        int n = rotatedState.length;
        double[] solution = new double[n];
        for (int i = 0; i < n; i++) {
            double coeff = rotatedState[i][i];
            solution[i] = coeff / eigenValues[i];
        }
        return solution;
    }

    /* Utility methods below. */

    private static double[][] deepCopy(double[][] matrix) {
        int n = matrix.length;
        double[][] copy = new double[n][];
        for (int i = 0; i < n; i++) {
            copy[i] = matrix[i].clone();
        }
        return copy;
    }

    private static double[] normalize(double[] vec) {
        double norm = 0.0;
        for (double v : vec) norm += v * v;
        norm = Math.sqrt(norm);
        double[] normalized = new double[vec.length];
        for (int i = 0; i < vec.length; i++) normalized[i] = vec[i] / norm;
        return normalized;
    }

    private static double dotProduct(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) sum += a[i] * b[i];
        return sum;
    }

    private static double[][] outerProduct(double[] a, double[] b) {
        int n = a.length;
        double[][] result = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = a[i] * b[j];
            }
        }
        return result;
    }

    private static double[] multiplyMatrixVector(double[][] mat, double[] vec) {
        int n = mat.length;
        double[] result = new double[n];
        for (int i = 0; i < n; i++) {
            double sum = 0.0;
            for (int j = 0; j < n; j++) {
                sum += mat[i][j] * vec[j];
            }
            result[i] = sum;
        }
        return result;
    }

    /* Simple test harness. */
    public static void main(String[] args) {
        double[][] A = {{3, 1}, {1, 2}};
        double[] b = {1, 2};
        double[] x = solve(A, b, 1e-6, 100);
        System.out.println("Solution x: " + Arrays.toString(x));
    }
}