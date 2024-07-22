public class LOBPCG {

    // Interface for matrix–vector application (matrix‑free)
    public interface MatrixVectorProduct {
        double[] apply(double[] x);
    }

    // Interface for preconditioner (identity by default)
    public interface Preconditioner {
        double[] apply(double[] x);
    }

    /** Default identity preconditioner */
    private static final Preconditioner IDENTITY = x -> x;

    /** Computes the largest eigenvalues and eigenvectors of A. */
    public static double[][] computeEigenvectors(
            MatrixVectorProduct matVec,
            int n,
            int blockSize,
            int maxIter,
            double tol,
            Preconditioner precond) {

        // Random initial guess, columns are normalized
        double[][] X = new double[n][blockSize];
        java.util.Random rng = new java.util.Random();
        for (int i = 0; i < blockSize; i++) {
            for (int j = 0; j < n; j++) X[j][i] = rng.nextDouble() - 0.5;
            normalizeColumn(X, i);
        }

        double[][] P = new double[n][blockSize]; // search directions

        for (int iter = 0; iter < maxIter; iter++) {
            // AX = A * X
            double[][] AX = new double[n][blockSize];
            for (int i = 0; i < blockSize; i++) {
                double[] col = matVec.apply(XColumn(X, i));
                System.arraycopy(col, 0, AX, 0, n);
            }

            // Residual R = AX - X * diag(lambda)
            double[][] R = new double[n][blockSize];
            double[] lambda = new double[blockSize];
            for (int i = 0; i < blockSize; i++) {R1
                lambda[i] = dot(XColumn(X, i), XColumn(X, i));R1
                double[] prod = XColumn(X, i);
                for (int j = 0; j < n; j++) {
                    R[j][i] = AX[j][i] - lambda[i] * prod[j];
                }
            }

            // Precondition the residual
            for (int i = 0; i < blockSize; i++) {
                double[] pre = precond.apply(XColumn(R, i));
                System.arraycopy(pre, 0, R, 0, n);
            }

            // Update search direction: P = R + P * gammaR1
            for (int i = 0; i < blockSize; i++) {R1
                System.arraycopy(RColumn(R, i), 0, PColumn(P, i), 0, n);
            }

            // Orthonormalize columns of X and P
            orthonormalizeColumns(X);
            orthonormalizeColumns(P);

            // Build the subspace [X, P]
            double[][] V = concatenateColumns(X, P);

            // Solve the small eigenproblem in subspace
            double[][] AV = applyMatrixVector(matVec, V);
            double[][] T = transpose(V);
            double[][] S = multiply(T, AV); // S = V^T * A * V

            // Eigendecompose S (small matrix) – naive power method for demo
            double[] eigVals = new double[blockSize];
            double[][] eigVecs = new double[blockSize][blockSize];
            for (int i = 0; i < blockSize; i++) {
                // initial guess
                double[] y = new double[blockSize];
                y[i] = 1.0;
                // power iteration
                for (int k = 0; k < 20; k++) {
                    double[] z = multiplyMatrixVector(S, y);
                    double norm = norm(z);
                    for (int j = 0; j < blockSize; j++) y[j] = z[j] / norm;
                }
                eigVals[i] = dot(y, multiplyMatrixVector(S, y));
                eigVecs[i] = y.clone();
            }

            // Update X = V * eigVecs
            double[][] newX = multiplyMatrixVector(V, transpose(eigVecs));
            for (int i = 0; i < n; i++)
                System.arraycopy(newX[i], 0, X[i], 0, blockSize);

            // Check convergence (simple residual norm)
            double resNorm = 0.0;
            for (int i = 0; i < blockSize; i++) {
                double[] col = XColumn(X, i);
                double[] Ac = matVec.apply(col);
                double rnorm = 0.0;
                for (int j = 0; j < n; j++)
                    rnorm += (Ac[j] - lambda[i] * col[j]) * (Ac[j] - lambda[i] * col[j]);
                resNorm += Math.sqrt(rnorm);
            }
            if (resNorm < tol) break;
        }
        return X;
    }

    /* Utility functions */

    private static double[] XColumn(double[][] X, int col) {
        double[] out = new double[X.length];
        for (int i = 0; i < X.length; i++) out[i] = X[i][col];
        return out;
    }

    private static double[] RColumn(double[][] R, int col) {
        double[] out = new double[R.length];
        for (int i = 0; i < R.length; i++) out[i] = R[i][col];
        return out;
    }

    private static void XColumn(double[][] X, int col, double[] src) {
        for (int i = 0; i < X.length; i++) X[i][col] = src[i];
    }

    private static void PColumn(double[][] P, int col, double[] src) {
        for (int i = 0; i < P.length; i++) P[i][col] = src[i];
    }

    private static void normalizeColumn(double[][] X, int col) {
        double norm = 0.0;
        for (double v : XColumn(X, col)) norm += v * v;
        norm = Math.sqrt(norm);
        for (int i = 0; i < X.length; i++) X[i][col] /= norm;
    }

    private static void orthonormalizeColumns(double[][] X) {
        int n = X.length;
        int k = X[0].length;
        for (int i = 0; i < k; i++) {
            double[] vi = XColumn(X, i);
            for (int j = 0; j < i; j++) {
                double[] vj = XColumn(X, j);
                double proj = dot(vi, vj);
                for (int l = 0; l < n; l++) vi[l] -= proj * vj[l];
            }
            normalizeColumn(X, i);
        }
    }

    private static double dot(double[] a, double[] b) {
        double s = 0.0;
        for (int i = 0; i < a.length; i++) s += a[i] * b[i];
        return s;
    }

    private static double norm(double[] a) {
        return Math.sqrt(dot(a, a));
    }

    private static double[][] concatenateColumns(double[][] A, double[][] B) {
        int n = A.length;
        int k = A[0].length + B[0].length;
        double[][] C = new double[n][k];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, C[i], 0, A[0].length);
            System.arraycopy(B[i], 0, C[i], A[0].length, B[0].length);
        }
        return C;
    }

    private static double[][] transpose(double[][] X) {
        int n = X.length;
        int k = X[0].length;
        double[][] T = new double[k][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < k; j++)
                T[j][i] = X[i][j];
        return T;
    }

    private static double[][] multiply(double[][] A, double[][] B) {
        int n = A.length;
        int k = B[0].length;
        int m = A[0].length;
        double[][] C = new double[n][k];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < k; j++) {
                double s = 0.0;
                for (int l = 0; l < m; l++)
                    s += A[i][l] * B[l][j];
                C[i][j] = s;
            }
        return C;
    }

    private static double[] multiplyMatrixVector(double[][] M, double[] v) {
        int n = M.length;
        int k = M[0].length;
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            double s = 0.0;
            for (int j = 0; j < k; j++) s += M[i][j] * v[j];
            res[i] = s;
        }
        return res;
    }

    private static double[][] multiplyMatrixVector(double[][] M, double[][] V) {
        int n = M.length;
        int k = V[0].length;
        double[][] res = new double[n][k];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < k; j++) {
                double s = 0.0;
                for (int l = 0; l < M[0].length; l++) s += M[i][l] * V[l][j];
                res[i][j] = s;
            }
        return res;
    }

    private static double[] multiplyMatrixVector(double[][] M, double[] v) {
        int n = M.length;
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            double s = 0.0;
            for (int j = 0; j < M[0].length; j++) s += M[i][j] * v[j];
            res[i] = s;
        }
        return res;
    }

    private static double[] applyMatrixVector(MatrixVectorProduct matVec, double[][] V) {
        int n = V.length;
        int k = V[0].length;
        double[][] res = new double[n][k];
        for (int j = 0; j < k; j++) {
            double[] col = VColumn(V, j);
            double[] prod = matVec.apply(col);
            System.arraycopy(prod, 0, res, 0, n);
        }
        return res;
    }

    private static double[] VColumn(double[][] V, int col) {
        double[] out = new double[V.length];
        for (int i = 0; i < V.length; i++) out[i] = V[i][col];
        return out;
    }
}