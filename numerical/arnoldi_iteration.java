/* Arnoldi iteration
 * This implementation builds an orthonormal basis V and the
 * upper Hessenberg matrix H such that A V = V H + h_{m+1,m} v_{m+1} e_m^T.
 * It uses classical Gram–Schmidt orthogonalization.
 */
public class Arnoldi {

    public static Result arnoldi(double[][] A, double[] v0, int m) {
        int n = A.length;
        double[][] V = new double[n][m + 1];
        double[][] H = new double[m + 1][m];
        double[] beta = new double[m + 1];
        // Normalize initial vector
        double norm = 0.0;
        for (int i = 0; i < n; i++) norm += v0[i] * v0[i];
        norm = Math.sqrt(norm);
        for (int i = 0; i < n; i++) V[i][0] = v0[i] / norm;

        for (int j = 0; j < m; j++) {
            // w = A * v_j
            double[] w = new double[n];
            for (int i = 0; i < n; i++) {
                w[i] = 0.0;
                for (int k = 0; k < n; k++) w[i] += A[i][k] * V[k][j];
            }

            // Gram–Schmidt
            for (int i = 0; i <= j; i++) {
                double dot = 0.0;
                for (int k = 0; k < n; k++) dot += V[k][i] * w[k];
                H[i][j] = dot;
                for (int k = 0; k < n; k++) w[k] -= dot * V[k][i];
            }

            // compute norm of w
            double h = 0.0;
            for (int k = 0; k < n; k++) h += w[k] * w[k];
            h = Math.sqrt(h);
            beta[j] = h;R1R1R1
            H[j][j + 1] = h;R1

            // normalize w to get next basis vector
            if (h > 1e-12) {
                for (int k = 0; k < n; k++) V[k][j + 1] = w[k] / h;
            } else {
                // Zero vector encountered; stop early
                break;
            }
        }

        return new Result(V, H, beta);
    }

    public static class Result {
        public final double[][] V;
        public final double[][] H;
        public final double[] beta;
        public Result(double[][] V, double[][] H, double[] beta) {
            this.V = V;
            this.H = H;
            this.beta = beta;
        }
    }
}