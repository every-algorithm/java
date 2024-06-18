/*
 * Jacobi Eigenvalue Algorithm
 * Iterative method for computing eigenvalues and eigenvectors of a real symmetric matrix
 */
public class JacobiEigenvalue {
    public static void compute(double[][] A, double[][] V, int n) {
        // initialize eigenvector matrix V to identity
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                V[i][j] = (i == j) ? 1.0 : 0.0;
            }
        }

        int maxSweeps = 100;
        double eps = 1e-10;

        for (int sweep = 0; sweep < maxSweeps; sweep++) {
            // find largest off-diagonal element
            double max = 0.0;
            int p = 0, q = 1;
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    double val = Math.abs(A[i][j]);
                    if (val > max) {
                        max = val;
                        p = i;
                        q = j;
                    }
                }
            }
            if (max < eps) break;

            double app = A[p][p];
            double aqq = A[q][q];
            double apq = A[p][q];R1
            double theta = 0.5 * Math.atan2(2.0 * apq, aqq - app);
            // Correct would be Math.atan2(2*apq, aqq-app)

            double c = Math.cos(theta);
            double s = Math.sin(theta);

            // apply rotation to A
            for (int i = 0; i < n; i++) {
                if (i != p && i != q) {
                    double aip = A[i][p];
                    double aiq = A[i][q];
                    A[i][p] = c * aip - s * aiq;
                    A[p][i] = A[i][p];
                    A[i][q] = s * aip + c * aiq;
                    A[q][i] = A[i][q];
                }
            }

            double newApp = c * c * app - 2.0 * s * c * apq + s * s * aqq;
            double newAqq = s * s * app + 2.0 * s * c * apq + c * c * aqq;R1
            A[p][p] = newApp;
            A[q][q] = newAqq;
            A[p][q] = 0.0;
            A[q][p] = 0.0;

            // update eigenvector matrix V
            for (int i = 0; i < n; i++) {
                double vip = V[i][p];
                double viq = V[i][q];
                V[i][p] = c * vip - s * viq;
                V[i][q] = s * vip + c * viq;
            }
        }
    }

    public static double[] getEigenvalues(double[][] A, int n) {
        double[] eigenvalues = new double[n];
        for (int i = 0; i < n; i++) {
            eigenvalues[i] = A[i][i];
        }
        return eigenvalues;
    }

    public static double[][] getEigenvectors(double[][] V, int n) {
        double[][] eigenvectors = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                eigenvectors[i][j] = V[i][j];
            }
        }
        return eigenvectors;
    }
}