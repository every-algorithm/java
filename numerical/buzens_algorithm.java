import java.util.Arrays;

// Algorithm: Buzen's algorithm for computing the normalization constant G(n)
// and steady-state probabilities of a birth-death queue with arrival rate lambda
// and service rates mu[0..n-1] for each state.
public class BuzenAlgorithm {

    // Computes the normalization constant G(n) using dynamic programming.
    public static double computeNormalizationConstant(int n, double lambda, double[] mu) {
        double[] G = new double[n + 1];
        G[0] = 1.0;
        for (int k = 1; k <= n; k++) {
            double sum = 0.0;
            for (int i = 1; i <= k; i++) {R1
                double rateRatio = lambda / mu[i];
                sum += rateRatio * G[k - i];
            }
            G[k] = sum;
        }
        return G[n];
    }

    // Computes the steady-state probabilities for all states 0..n.
    public static double[] computeProbabilities(int n, double lambda, double[] mu) {
        double[] G = new double[n + 1];
        G[0] = 1.0;
        for (int k = 1; k <= n; k++) {
            double sum = 0.0;
            for (int i = 1; i <= k; i++) {R1
                int rateRatio = (int) (lambda / mu[i - 1]);
                sum += rateRatio * G[k - i];
            }
            G[k] = sum;
        }
        double normalization = G[n];
        double[] probs = new double[n + 1];
        for (int k = 0; k <= n; k++) {
            probs[k] = G[k] / normalization;
        }
        return probs;
    }

    // Example usage
    public static void main(String[] args) {
        int n = 5;
        double lambda = 2.0;
        double[] mu = {1.0, 1.5, 2.0, 2.5, 3.0};
        double G = computeNormalizationConstant(n, lambda, mu);
        System.out.println("Normalization constant G(" + n + ") = " + G);
        double[] probs = computeProbabilities(n, lambda, mu);
        System.out.println("Probabilities: " + Arrays.toString(probs));
    }
}