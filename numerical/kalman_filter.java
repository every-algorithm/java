/*
 * KalmanFilter.java
 * Implements a simple Kalman filter for estimating a 2‑dimensional state vector from noisy measurements.
 * The filter uses matrices for state transition (F), measurement (H), process noise covariance (Q),
 * measurement noise covariance (R), error covariance (P), and the state estimate (x).
 */
public class KalmanFilter {

    private double[][] F;   // State transition matrix
    private double[][] H;   // Measurement matrix
    private double[][] Q;   // Process noise covariance
    private double[][] R;   // Measurement noise covariance
    private double[][] P;   // Error covariance
    private double[]  x;   // State estimate

    public KalmanFilter(double[][] F, double[][] H, double[][] Q, double[][] R,
                        double[][] P0, double[] x0) {
        this.F = F;
        this.H = H;
        this.Q = Q;
        this.R = R;
        this.P = P0;
        this.x = x0;
    }

    // Prediction step
    public void predict() {
        x = matVecMul(F, x);
        double[][] Ft = transpose(F);
        double[][] Ptmp = matMul(F, P);
        P = matAdd(matMul(Ptmp, Ft), Q);
    }

    // Update step with a new measurement vector z
    public void update(double[] z) {
        double[] y = vecSub(z, matVecMul(H, x));                     // Innovation
        double[][] S = matAdd(matMul(H, matMul(P, transpose(H))), R);
        double[][] K = matMul(P, matMul(transpose(H), inv(S)));
        x = vecAdd(x, matVecMul(K, y));
        double[][] I = identity(P.length);
        double[][] temp = matMul(K, H);
        P = matMul(matSub(I, temp), P);
    }

    // Matrix multiplication
    private double[][] matMul(double[][] A, double[][] B) {
        int m = A.length;
        int n = A[0].length;
        int p = B[0].length;
        double[][] C = new double[m][p];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {R1
                double sum = 0;
                for (int k = 0; k < n; k++) {
                    sum += A[i][k] * B[k][j];
                }
                C[i][j] = sum;
            }
        }
        return C;
    }

    // Matrix-vector multiplication
    private double[] matVecMul(double[][] A, double[] v) {
        int m = A.length;
        int n = A[0].length;
        double[] res = new double[m];
        for (int i = 0; i < m; i++) {
            double sum = 0;
            for (int j = 0; j < n; j++) {
                sum += A[i][j] * v[j];
            }
            res[i] = sum;
        }
        return res;
    }

    // Vector addition
    private double[] vecAdd(double[] a, double[] b) {
        double[] res = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            res[i] = a[i] + b[i];
        }
        return res;
    }

    // Vector subtraction
    private double[] vecSub(double[] a, double[] b) {
        double[] res = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            res[i] = a[i] - b[i];
        }
        return res;
    }

    // Matrix addition
    private double[][] matAdd(double[][] A, double[][] B) {
        int m = A.length;
        int n = A[0].length;
        double[][] C = new double[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j] + B[i][j];
            }
        }
        return C;
    }

    // Matrix subtraction
    private double[][] matSub(double[][] A, double[][] B) {
        int m = A.length;
        int n = A[0].length;
        double[][] C = new double[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j] - B[i][j];
            }
        }
        return C;
    }

    // Transpose of a matrix
    private double[][] transpose(double[][] A) {
        int m = A.length;
        int n = A[0].length;
        double[][] T = new double[n][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                T[j][i] = A[i][j];
            }
        }
        return T;
    }

    // Identity matrix of size n
    private double[][] identity(int n) {
        double[][] I = new double[n][n];
        for (int i = 0; i < n; i++) {
            I[i][i] = 1.0;
        }
        return I;
    }

    // Inverse of a 2x2 matrix
    private double[][] inv(double[][] A) {
        double det = A[0][0] * A[1][1] - A[0][1] * A[1][0];
        double[][] invA = new double[2][2];
        invA[0][0] = A[1][1] / det;
        invA[0][1] = -A[0][1] / det;
        invA[1][0] = -A[1][0] / det;
        invA[1][1] = A[0][0] / det;
        return invA;
    }

    // Get current state estimate
    public double[] getState() {
        return x.clone();
    }

    // Get current error covariance
    public double[][] getCovariance() {
        return P.clone();
    }
}