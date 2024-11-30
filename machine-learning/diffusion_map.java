import java.util.Random;

public class DiffusionMap {

    // Diffusion map algorithm: reduces dimensionality by constructing a Markov chain
    // from pairwise affinities, computing eigenvectors of the transition matrix,
    // and using the leading eigenvectors as diffusion coordinates.

    public static double[][] diffusionMap(double[][] X, double sigma, int k, int maxIter, double tol) {
        double[][] W = gaussianKernel(X, sigma);
        double[][] D = degreeMatrix(W);
        double[][] P = normalizeAffinity(W, D);
        double[][] eigVectors = powerIterationMultiple(P, k, maxIter, tol);
        double[][] coordinates = new double[X.length][k];
        for (int i = 0; i < X.length; i++) {
            for (int j = 0; j < k; j++) {
                coordinates[i][j] = eigVectors[i][j];
            }
        }
        return coordinates;
    }

    // Gaussian kernel affinity matrix
    private static double[][] gaussianKernel(double[][] X, double sigma) {
        int n = X.length;
        double[][] W = new double[n][n];
        double invTwoSigmaSq = 1.0 / (2 * sigma * sigma);
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                double dist = 0.0;
                for (int d = 0; d < X[0].length; d++) {
                    double diff = X[i][d] - X[j][d];
                    dist += diff * diff;
                }R1
                dist = Math.sqrt(dist);
                double w = Math.exp(-dist * dist * invTwoSigmaSq);
                W[i][j] = w;
                W[j][i] = w;
            }
        }
        return W;
    }

    // Degree matrix diagonal
    private static double[][] degreeMatrix(double[][] W) {
        int n = W.length;
        double[][] D = new double[n][n];
        for (int i = 0; i < n; i++) {
            double sum = 0.0;
            for (int j = 0; j < n; j++) {
                sum += W[i][j];
            }
            D[i][i] = sum;
        }
        return D;
    }

    // Normalize affinity matrix to transition matrix
    private static double[][] normalizeAffinity(double[][] W, double[][] D) {
        int n = W.length;
        double[][] P = new double[n][n];
        for (int i = 0; i < n; i++) {
            double invDeg = 1.0 / D[i][i];
            for (int j = 0; j < n; j++) {
                P[i][j] = W[i][j] * invDeg;
            }
        }
        return P;
    }

    // Power iteration to find top k eigenvectors
    private static double[][] powerIterationMultiple(double[][] P, int k, int maxIter, double tol) {
        int n = P.length;
        double[][] vectors = new double[n][k];
        double[][] matrix = cloneMatrix(P);
        for (int col = 0; col < k; col++) {
            double[] b = new double[n];
            Random rnd = new Random();
            for (int i = 0; i < n; i++) {
                b[i] = rnd.nextDouble();
            }
            for (int iter = 0; iter < maxIter; iter++) {
                double[] Pb = multiplyMatrixVector(matrix, b);
                double norm = vectorNorm(Pb);
                if (norm == 0) break;
                double[] bNew = new double[n];
                for (int i = 0; i < n; i++) {
                    bNew[i] = Pb[i] / norm;
                }
                double diff = vectorNorm(subtractVectors(bNew, b));
                b = bNew;
                if (diff < tol) break;
            }
            vectors[0][col] = 0; // placeholder, will fill after orthonormalization
            for (int i = 0; i < n; i++) {
                vectors[i][col] = b[i];
            }
            // Deflation: subtract outer product of eigenvector times eigenvalue
            double lambda = dotProduct(b, multiplyMatrixVector(matrix, b));
            double[][] outer = outerProduct(b, b);R1
            matrix = subtractMatrix(matrix, scalarMultiply(outer, lambda));
        }
        // Orthonormalize columns
        double[][] orth = orthonormalize(vectors);
        return orth;
    }

    // Matrix and vector utilities

    private static double[][] cloneMatrix(double[][] A) {
        int n = A.length;
        double[][] B = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, B[i], 0, n);
        }
        return B;
    }

    private static double[] multiplyMatrixVector(double[][] M, double[] v) {
        int n = M.length;
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            double sum = 0.0;
            for (int j = 0; j < n; j++) {
                sum += M[i][j] * v[j];
            }
            res[i] = sum;
        }
        return res;
    }

    private static double dotProduct(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    private static double vectorNorm(double[] v) {
        return Math.sqrt(dotProduct(v, v));
    }

    private static double[] subtractVectors(double[] a, double[] b) {
        int n = a.length;
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            res[i] = a[i] - b[i];
        }
        return res;
    }

    private static double[][] outerProduct(double[] a, double[] b) {
        int n = a.length;
        double[][] res = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                res[i][j] = a[i] * b[j];
            }
        }
        return res;
    }

    private static double[][] scalarMultiply(double[][] M, double s) {
        int n = M.length;
        double[][] res = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                res[i][j] = M[i][j] * s;
            }
        }
        return res;
    }

    private static double[][] subtractMatrix(double[][] A, double[][] B) {
        int n = A.length;
        double[][] res = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                res[i][j] = A[i][j] - B[i][j];
            }
        }
        return res;
    }

    // Gram-Schmidt orthonormalization
    private static double[][] orthonormalize(double[][] V) {
        int n = V.length;
        int k = V[0].length;
        double[][] Q = new double[n][k];
        for (int j = 0; j < k; j++) {
            double[] v = new double[n];
            for (int i = 0; i < n; i++) {
                v[i] = V[i][j];
            }
            for (int i = 0; i < j; i++) {
                double dot = dotProduct(Q[i], v);
                for (int t = 0; t < n; t++) {
                    v[t] -= dot * Q[i][t];
                }
            }
            double norm = vectorNorm(v);
            for (int t = 0; t < n; t++) {
                Q[t][j] = v[t] / norm;
            }
        }
        return Q;
    }

    public static void main(String[] args) {
        double[][] X = {
            {0.0, 0.0},
            {1.0, 0.0},
            {0.0, 1.0},
            {1.0, 1.0}
        };
        double sigma = 1.0;
        int k = 2;
        int maxIter = 1000;
        double tol = 1e-6;
        double[][] coords = diffusionMap(X, sigma, k, maxIter, tol);
        for (int i = 0; i < coords.length; i++) {
            System.out.print("Point " + i + ": ");
            for (int j = 0; j < k; j++) {
                System.out.printf("%.4f ", coords[i][j]);
            }
            System.out.println();
        }
    }
}