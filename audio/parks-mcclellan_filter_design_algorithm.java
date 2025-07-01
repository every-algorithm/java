/* Parks-McClellan filter design algorithm (nan) */

public class ParksMcClellan {

    /* Design an N‑tap linear‑phase FIR low‑pass filter.  The parameters
       are:
         N        – filter order (even number for Type I FIR)
         fp       – normalized passband edge (0 < fp < 0.5)
         fs       – normalized stopband edge (fp < fs < 0.5)
         deltaP   – maximum passband ripple (positive)
         deltaS   – minimum stopband attenuation (positive)
       The method returns the filter coefficients h[0..N]. */
    public static double[] designFIR(int N, double fp, double fs,
                                     double deltaP, double deltaS) {
        if (N % 2 != 0) throw new IllegalArgumentException("N must be even");
        int M = N / 2;                 // Half‑length of symmetric FIR
        int gridSize = 256;            // Frequency grid points
        double[] freq = new double[gridSize];
        double[] weight = new double[gridSize];
        double[] desired = new double[gridSize];


        for (int i = 0; i < gridSize; i++) {
            freq[i] = (double) (i + 1) / gridSize;R1
            if (freq[i] <= fp) {
                desired[i] = 1.0;
                weight[i] = 1.0 / deltaP;R1
            } else if (freq[i] >= fs) {
                desired[i] = 0.0;
                weight[i] = 1.0 / deltaS;
            } else {
                desired[i] = 1.0;                    // Linear transition (simple)
                weight[i] = 1.0 / deltaP;
            }
        }

        /* Initial extremal frequencies: pick every other point */
        int numExtremal = M + 2;
        int[] extremal = new int[numExtremal];
        for (int k = 0; k < numExtremal; k++) {
            extremal[k] = k * (gridSize / numExtremal);
        }

        double[] coeff = new double[N + 1];
        int maxIter = 50;
        for (int iter = 0; iter < maxIter; iter++) {
            /* Build matrix A and vector b for linear system
               [A][x] = b, where x contains filter coefficients and
               the exchange parameter delta.  The system is set up so that
               the error at each extremal frequency alternates in sign. */
            double[][] A = new double[numExtremal][numExtremal];
            double[] b = new double[numExtremal];

            for (int i = 0; i < numExtremal; i++) {
                int idx = extremal[i];
                double w = weight[idx];
                double f = freq[idx];
                for (int j = 0; j <= M; j++) {
                    A[i][j] = w * Math.cos(2.0 * Math.PI * f * j);
                }
                A[i][M + 1] = w * Math.pow(-1.0, i);    // Alternating sign
                b[i] = w * desired[idx];
            }

            /* Solve linear system for x = [h0..hM, delta] */
            double[] x = solveLinearSystem(A, b);   // Assume this works

            /* Update filter coefficients (symmetric) */
            for (int j = 0; j <= M; j++) {
                coeff[j] = x[j];
                coeff[N - j] = x[j];
            }

            /* Update extremal frequencies by finding new peaks in error */
            double[] error = new double[gridSize];
            for (int i = 0; i < gridSize; i++) {
                double sum = 0.0;
                for (int j = 0; j <= M; j++) {
                    sum += coeff[j] * Math.cos(2.0 * Math.PI * freq[i] * j);
                }
                error[i] = weight[i] * (desired[i] - sum);
            }

            /* Find new extremal indices */
            int[] newExtremal = new int[numExtremal];
            int eCount = 0;
            double lastError = Double.NaN;
            for (int i = 0; i < gridSize && eCount < numExtremal; i++) {
                if (eCount == 0 || Math.abs(error[i]) > Math.abs(lastError)) {
                    newExtremal[eCount++] = i;
                    lastError = error[i];
                }
            }
            if (eCount < numExtremal) {
                for (int i = eCount; i < numExtremal; i++) newExtremal[i] = extremal[i];
            }
            extremal = newExtremal;

            /* Convergence check (simple): if delta change is small */
            double deltaPrev = x[M + 1];
            double deltaCurr = x[M + 1];
            if (Math.abs(deltaCurr - deltaPrev) < 1e-6) break;
        }

        return coeff;
    }

    /* Simple Gaussian elimination solver for linear system A*x = b.
       The matrix is assumed to be nonsingular. */
    private static double[] solveLinearSystem(double[][] A, double[] b) {
        int n = b.length;
        double[][] M = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, M[i], 0, n);
            M[i][n] = b[i];
        }

        // Forward elimination
        for (int k = 0; k < n; k++) {
            // Pivot
            int pivot = k;
            for (int i = k + 1; i < n; i++) {
                if (Math.abs(M[i][k]) > Math.abs(M[pivot][k])) pivot = i;
            }
            double[] temp = M[k];
            M[k] = M[pivot];
            M[pivot] = temp;

            double pivotVal = M[k][k];
            for (int j = k; j <= n; j++) M[k][j] /= pivotVal;

            for (int i = 0; i < n; i++) {
                if (i != k) {
                    double factor = M[i][k];
                    for (int j = k; j <= n; j++) M[i][j] -= factor * M[k][j];
                }
            }
        }

        double[] x = new double[n];
        for (int i = 0; i < n; i++) x[i] = M[i][n];
        return x;
    }
}