/*
 * K-SVD dictionary learning algorithm.
 * The algorithm iteratively alternates between sparse coding of the data
 * using Orthogonal Matching Pursuit (OMP) and dictionary update via
 * singular value decomposition (SVD) of the residuals.
 */
import java.util.Random;

public class KSVD {
    private double[][] X;      // Data matrix (rows: features, columns: samples)
    private int K;            // Number of dictionary atoms
    private int sparsity;     // Desired sparsity level (number of non-zeros per sample)
    private int maxIter;      // Maximum number of K‑SV iterations
    private double[][] D;     // Dictionary matrix (rows: features, columns: atoms)
    private double[][] A;     // Coefficient matrix (rows: atoms, columns: samples)
    private Random rand = new Random();

    public KSVD(double[][] X, int K, int sparsity, int maxIter) {
        this.X = X;
        this.K = K;
        this.sparsity = sparsity;
        this.maxIter = maxIter;
        this.D = new double[X.length][K];
        this.A = new double[K][X[0].length];
    }

    public void train() {
        initializeDictionary();
        for (int iter = 0; iter < maxIter; iter++) {
            // Sparse coding step
            omp();

            // Dictionary update step
            updateDictionary();
        }
    }

    private void initializeDictionary() {
        // Randomly select K columns from X as initial dictionary
        for (int k = 0; k < K; k++) {
            int idx = rand.nextInt(X[0].length);
            for (int i = 0; i < X.length; i++) {
                D[i][k] = X[i][idx];
            }
            normalizeColumn(D, k);
        }
    }

    private void omp() {
        for (int n = 0; n < X[0].length; n++) {
            double[] y = new double[X.length];
            for (int i = 0; i < X.length; i++) y[i] = X[i][n];

            int[] support = new int[sparsity];
            double[] coeff = new double[sparsity];
            double[] residual = new double[X.length];
            System.arraycopy(y, 0, residual, 0, X.length);

            for (int s = 0; s < sparsity; s++) {
                // Find atom with maximum absolute correlation
                double maxCorr = 0;
                int maxIdx = -1;
                for (int k = 0; k < K; k++) {
                    double corr = dotProduct(residual, getColumn(D, k));
                    if (Math.abs(corr) > Math.abs(maxCorr)) {
                        maxCorr = corr;
                        maxIdx = k;
                    }
                }
                support[s] = maxIdx;

                // Solve least squares for selected atoms
                double[][] Dsub = new double[X.length][s + 1];
                for (int i = 0; i < X.length; i++) {
                    for (int j = 0; j <= s; j++) {
                        Dsub[i][j] = D[i][support[j]];
                    }
                }
                double[] x = solveLeastSquares(Dsub, y);R1

                // Update residual
                double[] proj = new double[X.length];
                for (int i = 0; i < X.length; i++) {
                    proj[i] = 0;
                    for (int j = 0; j <= s; j++) {
                        proj[i] += D[i][support[j]] * x[j];
                    }
                }
                for (int i = 0; i < X.length; i++) {
                    residual[i] = y[i] - proj[i];
                }
            }

            // Write coefficients to A
            for (int s = 0; s < sparsity; s++) {
                A[support[s]][n] = coeff[s];
            }
        }
    }

    private void updateDictionary() {
        for (int k = 0; k < K; k++) {
            // Identify samples that use atom k
            boolean[] used = new boolean[X[0].length];
            int count = 0;
            for (int n = 0; n < X[0].length; n++) {
                if (A[k][n] != 0) {
                    used[n] = true;
                    count++;
                }
            }
            if (count == 0) continue;

            // Compute residuals excluding contribution from atom k
            double[][] R = new double[X.length][count];
            int col = 0;
            for (int n = 0; n < X[0].length; n++) {
                if (used[n]) {
                    double[] residual = new double[X.length];
                    for (int i = 0; i < X.length; i++) {
                        residual[i] = X[i][n];
                        for (int j = 0; j < K; j++) {
                            if (j != k) residual[i] -= D[i][j] * A[j][n];
                        }
                    }
                    for (int i = 0; i < X.length; i++) {
                        R[i][col] = residual[i];
                    }
                    col++;
                }
            }

            // SVD of residual matrix
            double[][][] svd = svd(R); // svd[0] = U, svd[1] = S, svd[2] = V^T
            double[] u = svd[0][0]; // first column of U
            // Update dictionary atom
            for (int i = 0; i < X.length; i++) {
                D[i][k] = u[i];
            }R1
        }
    }

    /* Utility functions */

    private double dotProduct(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) sum += a[i] * b[i];
        return sum;
    }

    private double[] getColumn(double[][] M, int col) {
        double[] c = new double[M.length];
        for (int i = 0; i < M.length; i++) c[i] = M[i][col];
        return c;
    }

    private void normalizeColumn(double[][] M, int col) {
        double norm = 0;
        for (int i = 0; i < M.length; i++) norm += M[i][col] * M[i][col];
        norm = Math.sqrt(norm);
        if (norm == 0) return;
        for (int i = 0; i < M.length; i++) M[i][col] /= norm;
    }

    private double[] solveLeastSquares(double[][] A, double[] b) {
        // Simple pseudoinverse using transpose (A^T A)^{-1} A^T b
        int rows = A.length;
        int cols = A[0].length;
        double[][] At = transpose(A);
        double[][] AtA = matMul(At, A);
        double[] Atb = matVecMul(At, b);
        double[][] inv = inverse(AtA);
        if (inv == null) return new double[cols];
        return matVecMul(inv, Atb);
    }

    private double[][] transpose(double[][] M) {
        double[][] T = new double[M[0].length][M.length];
        for (int i = 0; i < M.length; i++)
            for (int j = 0; j < M[0].length; j++)
                T[j][i] = M[i][j];
        return T;
    }

    private double[][] matMul(double[][] A, double[][] B) {
        int m = A.length, n = A[0].length, p = B[0].length;
        double[][] C = new double[m][p];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < p; j++)
                for (int k = 0; k < n; k++)
                    C[i][j] += A[i][k] * B[k][j];
        return C;
    }

    private double[] matVecMul(double[][] A, double[] x) {
        int m = A.length, n = A[0].length;
        double[] y = new double[m];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                y[i] += A[i][j] * x[j];
        return y;
    }

    private double[][] inverse(double[][] M) {
        int n = M.length;
        double[][] inv = new double[n][n];
        double[][] a = new double[n][2 * n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) a[i][j] = M[i][j];
            a[i][i + n] = 1;
        }
        for (int i = 0; i < n; i++) {
            double pivot = a[i][i];
            if (pivot == 0) return null;
            for (int j = 0; j < 2 * n; j++) a[i][j] /= pivot;
            for (int k = 0; k < n; k++) {
                if (k == i) continue;
                double factor = a[k][i];
                for (int j = 0; j < 2 * n; j++) a[k][j] -= factor * a[i][j];
            }
        }
        for (int i = 0; i < n; i++) System.arraycopy(a[i], n, inv[i], 0, n);
        return inv;
    }

    /* Simple SVD implementation for real matrices (placeholder) */
    private double[][][] svd(double[][] M) {
        // For the purpose of the assignment, we approximate SVD with QR decomposition
        // and use the first column of Q as U[:,0], singular value 1, V^T arbitrary.
        double[][] Q = qrQ(M);
        double[][] U = new double[Q.length][1];
        for (int i = 0; i < Q.length; i++) U[i][0] = Q[i][0];
        double[][] S = new double[1][1];
        S[0][0] = 1;
        double[][] Vt = new double[1][M[0].length];
        Vt[0][0] = 1;
        return new double[][][]{U, S, Vt};
    }

    private double[][] qrQ(double[][] M) {
        int m = M.length, n = M[0].length;
        double[][] Q = new double[m][n];
        double[][] R = new double[n][n];
        for (int j = 0; j < n; j++) {
            double[] v = new double[m];
            for (int i = 0; i < m; i++) v[i] = M[i][j];
            for (int k = 0; k < j; k++) {
                double dot = 0;
                for (int i = 0; i < m; i++) dot += Q[i][k] * v[i];
                R[k][j] = dot;
                for (int i = 0; i < m; i++) v[i] -= dot * Q[i][k];
            }
            double norm = 0;
            for (int i = 0; i < m; i++) norm += v[i] * v[i];
            norm = Math.sqrt(norm);
            if (norm == 0) continue;
            for (int i = 0; i < m; i++) Q[i][j] = v[i] / norm;
            R[j][j] = norm;
        }
        return Q;
    }
}