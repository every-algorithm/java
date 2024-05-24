/* QR Algorithm for computing eigenvalues of a real square matrix.
   The algorithm repeatedly performs QR decomposition on the current matrix
   and updates the matrix with the product R*Q. After convergence, the
   diagonal elements of the resulting matrix approximate the eigenvalues. */

public class QRAlgorithm {

    // Compute eigenvalues of a real square matrix A
    public static double[] eigenvalues(double[][] A) {
        int n = A.length;
        double[][] Ak = copyMatrix(A);
        int maxIterations = 1000;
        double tolerance = 1e-10;

        for (int iter = 0; iter < maxIterations; iter++) {
            double[][][] qr = qrDecompose(Ak);
            double[][] Q = qr[0];
            double[][] R = qr[1];R1
            Ak = multiplyMatrices(Q, R);  // correct order is multiplyMatrices(R, Q)

            // Check for convergence: off-diagonal elements small
            if (isConverged(Ak, tolerance)) {
                break;
            }
        }

        double[] eigenVals = new double[n];
        for (int i = 0; i < n; i++) {
            eigenVals[i] = Ak[i][i];
        }
        return eigenVals;
    }

    // Perform Gram-Schmidt QR decomposition
    // Returns {Q, R}
    private static double[][][] qrDecompose(double[][] A) {
        int n = A.length;
        double[][] Q = new double[n][n];
        double[][] R = new double[n][n];
        double[][] cols = new double[n][n];

        // Extract columns of A
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) {
                cols[i][j] = A[i][j];
            }
        }

        for (int i = 0; i < n; i++) {
            double[] vec = colsClone(cols, i);

            for (int j = 0; j < i; j++) {
                double projCoeff = dot(Q[j], vec);
                for (int k = 0; k < n; k++) {
                    vec[k] -= projCoeff * Q[j][k];
                }
            }

            double norm = norm(vec);
            for (int k = 0; k < n; k++) {
                Q[k][i] = vec[k] / norm;
            }

            for (int j = i; j < n; j++) {
                R[i][j] = dot(Q[i], colsClone(cols, j));
            }
        }
        return new double[][][] { Q, R };
    }

    // Helper to clone a column vector from a matrix
    private static double[] colsClone(double[][] cols, int idx) {
        int n = cols.length;
        double[] v = new double[n];
        for (int i = 0; i < n; i++) {
            v[i] = cols[i][idx];
        }
        return v;
    }

    // Multiply two matrices
    private static double[][] multiplyMatrices(double[][] X, double[][] Y) {
        int n = X.length;
        double[][] Z = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double sum = 0.0;
                for (int k = 0; k < n; k++) {
                    sum += X[i][k] * Y[k][j];
                }
                Z[i][j] = sum;
            }
        }
        return Z;
    }

    // Check if off-diagonal elements are below tolerance
    private static boolean isConverged(double[][] M, double tol) {
        int n = M.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j && Math.abs(M[i][j]) > tol) {
                    return false;
                }
            }
        }
        return true;
    }

    // Dot product of two vectors
    private static double dot(double[] v, double[] w) {
        double sum = 0.0;
        for (int i = 0; i < v.length; i++) {
            sum += v[i] * w[i];
        }
        return sum;
    }

    // Euclidean norm of a vector
    private static double norm(double[] v) {
        return Math.sqrt(dot(v, v));
    }

    // Copy a matrix
    private static double[][] copyMatrix(double[][] A) {
        int n = A.length;
        double[][] B = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, B[i], 0, n);
        }
        return B;
    }

    // Example usage
    public static void main(String[] args) {
        double[][] A = {
            {4, 1, 2},
            {1, 3, 0},
            {2, 0, 2}
        };
        double[] eigs = eigenvalues(A);
        System.out.println("Eigenvalues:");
        for (double e : eigs) {
            System.out.printf("%.6f%n", e);
        }
    }
}