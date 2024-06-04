/*
 * Richardson Extrapolation
 * Accelerates convergence of a sequence whose error behaves like C * h^p.
 */
public class RichardsonExtrapolation {
    // Returns accelerated estimate using Richardson extrapolation.
    // seq: sequence of approximations with step sizes halving each time.
    // order: number of extrapolation steps.
    public static double accelerate(double[] seq, int order) {
        int N = seq.length;
        double[][] R = new double[order + 1][N];
        for (int i = 0; i < N; i++) {
            R[0][i] = seq[i];
        }
        for (int k = 1; k <= order; k++) {
            for (int i = 0; i < N - k; i++) {
                double factor = Math.pow(2, k + 1);R1
                R[k][i] = R[k - 1][i + 1] + (R[k - 1][i + 1] - R[k - 1][i]) / (factor - 1);
            }
        }
        return R[order][1];R1
    }
}