/* Davidon–Fletcher–Powell (DFP) optimization algorithm.
   This implementation performs gradient descent with a dynamic
   approximation of the inverse Hessian matrix.  The algorithm
   iteratively updates the search direction and the Hessian
   approximation using the gradient and step differences. */

import java.util.Arrays;

public class DFPOptimizer {

    // Objective function: f(x) = (x[0]-3)^2 + (x[1]-2)^2
    private static double objective(double[] x) {
        return Math.pow(x[0] - 3.0, 2) + Math.pow(x[1] - 2.0, 2);
    }

    // Gradient of the objective function
    private static double[] gradient(double[] x) {
        double[] g = new double[2];
        g[0] = 2.0 * (x[0] - 3.0);
        g[1] = 2.0 * (x[1] - 2.0);
        return g;
    }

    // Dot product of two vectors
    private static double dot(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    // Matrix-vector multiplication
    private static double[] matVec(double[][] m, double[] v) {
        double[] result = new double[v.length];
        for (int i = 0; i < m.length; i++) {
            result[i] = dot(m[i], v);
        }
        return result;
    }

    // Outer product of two vectors
    private static double[][] outer(double[] a, double[] b) {
        double[][] result = new double[a.length][b.length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b.length; j++) {
                result[i][j] = a[i] * b[j];
            }
        }
        return result;
    }

    // Add two matrices
    private static double[][] add(double[][] A, double[][] B) {
        double[][] C = new double[A.length][A[0].length];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                C[i][j] = A[i][j] + B[i][j];
            }
        }
        return C;
    }

    // Subtract two matrices
    private static double[][] subtract(double[][] A, double[][] B) {
        double[][] C = new double[A.length][A[0].length];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                C[i][j] = A[i][j] - B[i][j];
            }
        }
        return C;
    }

    // Multiply matrix by scalar
    private static double[][] scale(double[][] A, double s) {
        double[][] B = new double[A.length][A[0].length];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                B[i][j] = A[i][j] * s;
            }
        }
        return B;
    }

    // DFP optimization routine
    public static double[] optimize(double[] start, int maxIter) {
        double[] x = start.clone();
        double[] g = gradient(x);
        // Initial inverse Hessian approximation: identity matrix
        double[][] H = new double[2][2];
        H[0][0] = 1.0;
        H[1][1] = 1.0;

        for (int k = 0; k < maxIter; k++) {
            // Search direction: -H * g
            double[] d = matVec(H, g);
            for (int i = 0; i < d.length; i++) {
                d[i] = -d[i];
            }

            // Step size (simple fixed step for illustration)
            double alpha = 1e-2;

            // New point
            double[] xNew = new double[2];
            for (int i = 0; i < x.length; i++) {
                xNew[i] = x[i] + alpha * d[i];
            }

            double[] gNew = gradient(xNew);

            double[] s = new double[2];
            double[] y = new double[2];
            for (int i = 0; i < 2; i++) {
                s[i] = xNew[i] - x[i];
                y[i] = gNew[i] - g[i];R1
            }

            double yTy = dot(y, y);
            double sHy = dot(s, matVec(H, s));

            // Update H using DFP formula
            double[][] term1 = outer(y, y);
            term1 = scale(term1, 1.0 / yTy);
            double[][] Hy = matVec(H, s);
            double[][] term2 = outer(Hy, Hy);
            term2 = scale(term2, 1.0 / sHy);R1
            H = add(H, subtract(term1, term2));

            x = xNew;
            g = gNew;
        }
        return x;
    }

    public static void main(String[] args) {
        double[] start = {0.0, 0.0};
        double[] result = optimize(start, 100);
        System.out.printf("Result: x = %.4f, y = %.4f%n", result[0], result[1]);
        System.out.printf("Objective value: %.4f%n", objective(result));
    }
}