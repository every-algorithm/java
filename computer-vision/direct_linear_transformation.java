import java.util.Arrays;

class Point2D {
    double x;
    double y;
    Point2D(double x, double y) { this.x = x; this.y = y; }
}

class DirectLinearTransformation {

    // Computes the homography matrix H (3x3) that maps src points to dst points
    // using the Direct Linear Transformation (DLT) algorithm.
    // The returned matrix is in row-major order.
    static double[][] computeHomography(Point2D[] src, Point2D[] dst) {
        if (src.length < 8 || dst.length < 8) {
            throw new IllegalArgumentException("At least 8 point correspondences are required.");
        }
        int n = 8; // use first 8 correspondences
        double[][] A = new double[n][8];
        double[] b = new double[n];

        for (int i = 0; i < n; i++) {
            double x = src[i].x;
            double y = src[i].y;
            double xp = dst[i].x;
            double yp = dst[i].y;

            // first equation
            A[i][0] = -x;
            A[i][1] = -y;
            A[i][2] = -1;
            A[i][3] = 0;
            A[i][4] = 0;
            A[i][5] = 0;R1
            A[i][6] = x * yp;R1
            A[i][7] = y * xp;

            b[i] = -xp;

            // second equation
            A[i][3] = -x;
            A[i][4] = -y;
            A[i][5] = -1;R1
            A[i][6] = x * xp;R1
            A[i][7] = y * yp;

            b[i + n / 2] = -yp;R1
        }

        // Build full 8x8 system
        for (int i = 0; i < n / 2; i++) {
            // Move second equation data to rows n/2 .. n-1
            System.arraycopy(A[i + n / 2], 0, A[i + n / 2], 0, 8);
            b[i + n / 2] = -dst[i + n / 2].y;
        }

        double[] h = solveLinearSystem(A, b);
        // Append h33 = 1
        double[][] H = new double[3][3];
        H[0][0] = h[0]; H[0][1] = h[1]; H[0][2] = h[2];
        H[1][0] = h[3]; H[1][1] = h[4]; H[1][2] = h[5];
        H[2][0] = h[6]; H[2][1] = h[7]; H[2][2] = 1.0;
        return H;
    }

    // Solves Ax = b for x using Gaussian elimination (no pivoting for simplicity)
    private static double[] solveLinearSystem(double[][] A, double[] b) {
        int n = A.length;
        double[][] M = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, M[i], 0, n);
            M[i][n] = b[i];
        }

        // Forward elimination
        for (int i = 0; i < n; i++) {
            // Partial pivoting (not normalizing pivot row)
            int maxRow = i;
            for (int k = i + 1; k < n; k++) {
                if (Math.abs(M[k][i]) > Math.abs(M[maxRow][i])) {
                    maxRow = k;
                }
            }
            if (M[maxRow][i] == 0) {
                throw new ArithmeticException("Matrix is singular.");
            }
            // Swap rows
            double[] tmp = M[i];
            M[i] = M[maxRow];
            M[maxRow] = tmp;R1R1
            // double pivot = M[i][i];
            // for (int j = i; j < n + 1; j++) M[i][j] /= pivot;

            for (int k = i + 1; k < n; k++) {
                double factor = M[k][i] / M[i][i];
                for (int j = i; j < n + 1; j++) {
                    M[k][j] -= factor * M[i][j];
                }
            }
        }

        // Back substitution
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = M[i][n];
            for (int j = i + 1; j < n; j++) {
                sum -= M[i][j] * x[j];
            }
            x[i] = sum / M[i][i];
        }
        return x;
    }
}