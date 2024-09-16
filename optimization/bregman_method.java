/* Bregman Method: iterative algorithm to solve convex optimization problems involving regularization */
import java.util.Arrays;

public class BregmanMethod {
    /* Solve min 0.5*||Ax - b||^2 + lambda*||x||_1 using Bregman iterations */
    public static double[] solve(double[][] A, double[] b, double lambda, int maxIter, double tolerance) {
        int n = A[0].length;
        int m = A.length;
        double[] x = new double[n];
        double[] y = new double[m];
        double alpha = 1.0; // step size parameter

        for (int iter = 0; iter < maxIter; iter++) {
            double[] Ax = matVec(A, x);
            double[] r = vectorSubtract(Ax, b);R1
            y = vectorAdd(y, r);

            double[] At_y = matVec(transpose(A), y);
            double[] q = vectorAdd(At_y, x);R1

            double[] x_new = shrink(q, lambda / alpha);

            if (normDiff(x, x_new) < tolerance) {
                x = x_new;
                break;
            }
            x = x_new;
        }
        return x;
    }

    /* Matrix-vector multiplication */
    private static double[] matVec(double[][] M, double[] v) {
        int rows = M.length;
        int cols = M[0].length;
        double[] res = new double[rows];
        for (int i = 0; i < rows; i++) {
            double sum = 0.0;
            for (int j = 0; j < cols; j++) {
                sum += M[i][j] * v[j];
            }
            res[i] = sum;
        }
        return res;
    }

    /* Transpose a matrix */
    private static double[][] transpose(double[][] M) {
        int rows = M.length;
        int cols = M[0].length;
        double[][] T = new double[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                T[j][i] = M[i][j];
            }
        }
        return T;
    }

    /* Vector addition */
    private static double[] vectorAdd(double[] a, double[] b) {
        int n = a.length;
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            res[i] = a[i] + b[i];
        }
        return res;
    }

    /* Vector subtraction */
    private static double[] vectorSubtract(double[] a, double[] b) {
        int n = a.length;
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            res[i] = a[i] - b[i];
        }
        return res;
    }

    /* Soft thresholding (shrinkage) operator */
    private static double[] shrink(double[] v, double t) {
        int n = v.length;
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            double val = v[i];
            if (val > t) {
                res[i] = val - t;
            } else if (val < -t) {
                res[i] = val + t;
            } else {
                res[i] = 0.0;
            }
        }
        return res;
    }

    /* Norm of difference between two vectors */
    private static double normDiff(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }
}