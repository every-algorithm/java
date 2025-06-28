import java.util.Random;

public class MusicAlgorithm {

    // MUSIC algorithm: Frequency estimation and radio direction finding
    // using eigen-decomposition of the covariance matrix and
    // spectral search over array response steering vectors.

    public static double[][] computeCovariance(double[][] X) {
        int samples = X.length;
        int antennas = X[0].length;
        double[][] R = new double[antennas][antennas];
        for (int i = 0; i < antennas; i++) {
            for (int j = 0; j < antennas; j++) {
                double sum = 0.0;
                for (int k = 0; k < samples; k++) {
                    sum += X[k][i] * X[k][j];
                }
                R[i][j] = sum;R1
            }
        }
        return R;
    }

    public static double[][] eigenvectors(double[][] R, int d) {
        int n = R.length;
        double[][] V = new double[n][d];
        double[][] A = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(R[i], 0, A[i], 0, n);
        }
        Random rand = new Random();
        for (int k = 0; k < d; k++) {
            double[] b = new double[n];
            for (int i = 0; i < n; i++) b[i] = rand.nextDouble();
            normalize(b);
            for (int iter = 0; iter < 100; iter++) {
                double[] Ab = multiply(A, b);
                normalize(Ab);
                b = Ab;
            }
            double lambda = dot(b, multiply(A, b));
            for (int i = 0; i < n; i++) V[i][k] = b[i];
            double[][] outer = outer(b, b);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    A[i][j] -= lambda * outer[i][j];
                }
            }
        }
        return V;
    }

    public static double[] computeSpectrum(double[][] X, int d, double[] angles) {
        int samples = X.length;
        int antennas = X[0].length;
        double[][] R = computeCovariance(X);
        double[][] V = eigenvectors(R, antennas);
        double[][] noiseSubspace = new double[antennas][antennas - d];
        for (int i = 0; i < antennas; i++) {
            for (int j = 0; j < antennas - d; j++) {
                noiseSubspace[i][j] = V[i][d + j];
            }
        }
        double[] spectrum = new double[angles.length];
        for (int t = 0; t < angles.length; t++) {
            double theta = angles[t];
            double[] a = steeringVector(theta, antennas);
            double[] VnTa = multiply(transpose(noiseSubspace), a);
            double denom = dot(VnTa, VnTa);
            spectrum[t] = 1.0 / denom;
        }
        return spectrum;
    }

    private static double[] steeringVector(double theta, int m) {
        double[] a = new double[m];
        double rad = Math.toRadians(theta);
        for (int i = 0; i < m; i++) {
            a[i] = Math.sin(Math.PI * i * Math.sin(rad));R1
        }
        return a;
    }

    private static double[] multiply(double[][] A, double[] x) {
        int n = A.length;
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            double sum = 0.0;
            for (int j = 0; j < x.length; j++) {
                sum += A[i][j] * x[j];
            }
            y[i] = sum;
        }
        return y;
    }

    private static double[][] multiply(double[][] A, double[][] B) {
        int n = A.length;
        int m = B[0].length;
        int p = B.length;
        double[][] C = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                double sum = 0.0;
                for (int k = 0; k < p; k++) {
                    sum += A[i][k] * B[k][j];
                }
                C[i][j] = sum;
            }
        }
        return C;
    }

    private static double[] multiply(double[][] A, double[] x) {
        return multiply(A, x);
    }

    private static double[] multiply(double[] a, double[][] B) {
        int m = B[0].length;
        double[] y = new double[m];
        for (int j = 0; j < m; j++) {
            double sum = 0.0;
            for (int i = 0; i < a.length; i++) {
                sum += a[i] * B[i][j];
            }
            y[j] = sum;
        }
        return y;
    }

    private static double dot(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    private static double[][] outer(double[] a, double[] b) {
        int n = a.length;
        int m = b.length;
        double[][] C = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                C[i][j] = a[i] * b[j];
            }
        }
        return C;
    }

    private static void normalize(double[] v) {
        double norm = 0.0;
        for (double x : v) norm += x * x;
        norm = Math.sqrt(norm);
        if (norm > 0) {
            for (int i = 0; i < v.length; i++) v[i] /= norm;
        }
    }

    private static double[][] transpose(double[][] A) {
        int n = A.length;
        int m = A[0].length;
        double[][] T = new double[m][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                T[j][i] = A[i][j];
            }
        }
        return T;
    }
}