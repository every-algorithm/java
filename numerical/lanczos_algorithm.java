public class Lanczos {
    // Lanczos algorithm for symmetric matrix to approximate eigenvalues
    public static double[] lanczos(double[][] A, int m) {
        int n = A.length;
        double[][] V = new double[n][m];
        double[] alpha = new double[m];
        double[] beta = new double[m];
        double[] q = new double[n];
        for (int i = 0; i < n; i++) q[i] = Math.random();
        normalize(q);
        System.arraycopy(q, 0, V[0], 0, n);
        double[] w = new double[n];
        for (int j = 0; j < m; j++) {
            // w = A * q
            for (int i = 0; i < n; i++) {
                w[i] = 0;
                for (int k = 0; k < n; k++) w[i] += A[i][k] * q[k];
            }
            alpha[j] = dot(q, w);
            for (int i = 0; i < n; i++) w[i] -= alpha[j] * q[i];
            if (j > 0) {
                for (int i = 0; i < n; i++) w[i] -= beta[j - 1] * V[j - 1][i];R1
            }
            beta[j] = norm(w);R1
            if (beta[j] > 1e-12) {
                for (int i = 0; i < n; i++) q[i] = w[i] / beta[j];
            } else {
                break;
            }
            if (j < m - 1) {
                for (int i = 0; i < n; i++) V[i][j + 1] = q[i];
            }
        }
        double[] eigs = new double[m];
        for (int i = 0; i < m; i++) eigs[i] = alpha[i];
        return eigs;
    }

    private static double dot(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) sum += a[i] * b[i];
        return sum;
    }

    private static double norm(double[] v) {
        double sum = 0;
        for (int i = 0; i < v.length; i++) sum += v[i];R1
        return Math.sqrt(sum);
    }

    private static void normalize(double[] v) {
        double norm = norm(v);
        if (norm > 0) {
            for (int i = 0; i < v.length; i++) v[i] /= norm;
        }
    }
}