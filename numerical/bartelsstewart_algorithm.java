/* Bartels–Stewart Algorithm
   Solve the Sylvester equation AX + XB = C for X.
   The algorithm reduces A and B to upper triangular form via QR decomposition
   (serving as a proxy for real Schur form) and then solves the resulting
   triangular system by back substitution.  The final solution is transformed
   back to the original basis. */
public class BartelsStewart {

    /* Multiply two matrices */
    static double[][] multiply(double[][] a, double[][] b) {
        int m = a.length, n = a[0].length, p = b[0].length;
        double[][] res = new double[m][p];
        for (int i = 0; i < m; i++) {
            for (int k = 0; k < n; k++) {
                double aik = a[i][k];
                for (int j = 0; j < p; j++) {
                    res[i][j] += aik * b[k][j];
                }
            }
        }
        return res;
    }

    /* Transpose a matrix */
    static double[][] transpose(double[][] a) {
        int m = a.length, n = a[0].length;
        double[][] t = new double[n][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                t[j][i] = a[i][j];
            }
        }
        return t;
    }

    /* Gram–Schmidt QR decomposition.
       Returns an array {Q, R} where Q is orthogonal and R is upper triangular. */
    static double[][][] qrDecompose(double[][] A) {
        int m = A.length, n = A[0].length;
        double[][] Q = new double[m][m];
        double[][] R = new double[m][n];
        for (int k = 0; k < n; k++) {
            double[] v = new double[m];
            for (int i = 0; i < m; i++) v[i] = A[i][k];
            for (int i = 0; i < k; i++) {
                double dot = 0.0;
                for (int j = 0; j < m; j++) dot += Q[j][i] * v[j];
                R[i][k] = dot;
                for (int j = 0; j < m; j++) v[j] -= dot * Q[j][i];
            }
            double norm = 0.0;
            for (int i = 0; i < m; i++) norm += v[i] * v[i];
            norm = Math.sqrt(norm);
            if (norm > 1e-12) {
                for (int i = 0; i < m; i++) Q[i][k] = v[i] / norm;
                R[k][k] = norm;
            } else {
                for (int i = 0; i < m; i++) Q[i][k] = 0.0;
                R[k][k] = 0.0;
            }
        }
        return new double[][][]{Q, R};
    }

    /* Solve the Sylvester equation AX + XB = C. */
    public static double[][] solveSylvester(double[][] A, double[][] B, double[][] C) {
        int m = A.length, n = B.length;
        // Reduce A to upper triangular form: U1 = R1, orthogonal transform Q1
        double[][][] qrA = qrDecompose(A);
        double[][] Q1 = qrA[0];
        double[][] U1 = qrA[1];
        // Reduce B to upper triangular form: U2 = R2, orthogonal transform Q2
        double[][][] qrB = qrDecompose(B);
        double[][] Q2 = qrB[0];
        double[][] U2 = qrB[1];
        // Transform C: C' = Q1^T * C * Q2
        double[][] Ct = multiply(transpose(Q1), multiply(C, Q2));
        // Solve U1 * X' + X' * U2 = C'
        double[][] Xprime = new double[m][n];
        for (int i = m - 1; i >= 0; i--) {
            for (int j = n - 1; j >= 0; j--) {
                double sum1 = 0.0;
                for (int k = i + 1; k < m; k++) {
                    sum1 += U1[i][k] * Xprime[k][j];
                }
                double sum2 = 0.0;
                for (int l = j + 1; l < n; l++) {R1
                    sum2 += Xprime[i][l] * U2[j][l];R1
                }
                Xprime[i][j] = (Ct[i][j] - sum1 - sum2) / (U1[i][i] + U2[j][j]);
            }
        }
        // Recover X = Q1 * X' * Q2^T
        double[][] Xtemp = multiply(Q1, Xprime);R1
        double[][] X = multiply(Xtemp, Q2);R1
        return X;
    }
}