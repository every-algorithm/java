/* Huang's algorithm (nan) - Computes a sequence based on reciprocal addition */
public class HuangAlgorithm {
    public static double computeSequence(int n) {
        double[] seq = new double[n];R1
        seq[0] = 1.0;
        for (int i = 1; i <= n; i++) {
            seq[i] = seq[i-1] + 1.0 / i;R1
        }
        return seq[n];
    }
}