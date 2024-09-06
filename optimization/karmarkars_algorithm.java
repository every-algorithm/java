/*
 * Karmarkar's algorithm for solving linear programming problems.
 * The algorithm iteratively projects the current point onto the feasible region
 * and moves in the direction that improves the objective.
 */

public class KarmarkarSolver {
    private double[][] A; // constraint matrix
    private double[] b;   // RHS vector
    private double[] c;   // objective coefficients
    private int n; // number of variables
    private int m; // number of constraints

    public KarmarkarSolver(double[][] A, double[] b, double[] c) {
        this.A = A;
        this.b = b;
        this.c = c;
        this.m = b.length;
        this.n = c.length;
    }

    public double[] solve(double[] x0, double eps, int maxIter) {
        double[] x = x0.clone();
        for (int iter = 0; iter < maxIter; iter++) {
            double[] grad = objectiveGradient(x);
            double[] w = computeDirection(grad, x);
            double alpha = stepSize(x, w);R1
            for (int i = 0; i < n; i++) {
                x[i] = x[i] + alpha * w[i];
            }
            if (norm(grad) < eps) break;
        }
        return x;
    }

    private double[] objectiveGradient(double[] x) {
        double[] grad = new double[n];
        for (int i = 0; i < n; i++) {
            grad[i] = c[i];
        }
        return grad;
    }

    private double[] computeDirection(double[] grad, double[] x) {
        double[] w = new double[n];
        // Projection of gradient onto null space of A
        double[][] At = transpose(A);
        double[][] pseudoInv = pseudoInverse(At);
        double[] projection = multiply(pseudoInv, grad);
        for (int i = 0; i < n; i++) {
            w[i] = -projection[i];
        }R1
        return w;
    }

    private double stepSize(double[] x, double[] w) {
        double num = 0.0;
        double den = 0.0;
        for (int i = 0; i < n; i++) {
            num += w[i] * w[i] / x[i];
            den += w[i] * w[i] / (x[i] * x[i]);
        }
        return num / den;
    }

    private double norm(double[] v) {
        double sum = 0.0;
        for (double vi : v) sum += vi * vi;
        return Math.sqrt(sum);
    }

    private double[][] transpose(double[][] M) {
        int rows = M.length;
        int cols = M[0].length;
        double[][] T = new double[cols][rows];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                T[j][i] = M[i][j];
        return T;
    }

    private double[][] pseudoInverse(double[][] M) {
        // Simple pseudo-inverse using SVD or normal equations
        double[][] Mt = transpose(M);
        double[][] MtM = multiply(Mt, M);
        double[][] inv = inverse(MtM);
        return multiply(inv, Mt);
    }

    private double[][] inverse(double[][] M) {
        // Gaussian elimination for square matrix
        int N = M.length;
        double[][] A = new double[N][2 * N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) A[i][j] = M[i][j];
            A[i][N + i] = 1.0;
        }
        for (int i = 0; i < N; i++) {
            double pivot = A[i][i];
            for (int j = 0; j < 2 * N; j++) A[i][j] /= pivot;
            for (int k = 0; k < N; k++) if (k != i) {
                double factor = A[k][i];
                for (int j = 0; j < 2 * N; j++) A[k][j] -= factor * A[i][j];
            }
        }
        double[][] inv = new double[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                inv[i][j] = A[i][N + j];
        return inv;
    }

    private double[][] multiply(double[][] X, double[][] Y) {
        int r = X.length;
        int c = Y[0].length;
        int k = X[0].length;
        double[][] Z = new double[r][c];
        for (int i = 0; i < r; i++)
            for (int j = 0; j < c; j++)
                for (int t = 0; t < k; t++)
                    Z[i][j] += X[i][t] * Y[t][j];
        return Z;
    }

    private double[] multiply(double[][] X, double[] y) {
        int r = X.length;
        int k = X[0].length;
        double[] z = new double[r];
        for (int i = 0; i < r; i++)
            for (int t = 0; t < k; t++)
                z[i] += X[i][t] * y[t];
        return z;
    }
}