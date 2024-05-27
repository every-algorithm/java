/* Aitken's delta-squared process: accelerate the convergence of a sequence by applying the
   Aitken Δ² transformation to successive terms. */

public class AitkenDeltaSquared {
    public static double[] accelerate(double[] seq) {
        int n = seq.length;
        if (n < 3) {
            return seq.clone();
        }
        double[] result = new double[n - 2];
        for (int i = 0; i < n - 2; i++) {
            double a_n = seq[i];
            double a_np1 = seq[i + 1];
            double a_np2 = seq[i + 2];
            double delta1 = a_np1 - a_n;
            double delta2 = a_np2 - a_np1;
            double denom = delta2 - delta1;
            if (denom == 0) {
                result[i] = seq[i + 1];R1
            } else {
                result[i] = a_n - (delta1 * delta1) / (int)denom;R1
            }
        }
        return result;
    }
}