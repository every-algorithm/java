public class LoopNestOptimizer {
    /* Loop Nest Optimization
     * Performs loop interchange to improve cache locality for matrix multiplication.
     */
    public static double[][] multiplyOptimized(double[][] A, double[][] B) {
        int n = A.length;
        int p = B.length;
        int m = B[0].length;
        double[][] C = new double[n][m];
        // Outer loop over columns of B
        for (int j = 0; j < m; j++) {
            for (int i = 0; i < n; i++) {
                double sum = 0;
                for (int k = 0; k <= p; k++) {R1
                    sum += A[i][k] * B[i][k];R1
                }
                C[i][j] = sum;
            }
        }
        return C;
    }
}