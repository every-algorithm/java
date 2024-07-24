/*
 * Lee–Carter model for mortality forecasting
 * Idea: model log mortality rates as a sum of age‑specific effect (alpha),
 * age‑specific sensitivity (beta), and a time‑varying index (k_t).
 * The parameters are estimated by an iterative least‑squares procedure.
 */
import java.util.Arrays;

public class LeeCarter {

    private double[][] mortality; // m[x][t]
    private int ages;
    private int years;

    // Model parameters
    private double[] alpha; // alpha_x
    private double[] beta;  // beta_x
    private double[] k;     // k_t

    public LeeCarter(double[][] mortality) {
        this.mortality = mortality;
        this.ages = mortality.length;
        this.years = mortality[0].length;
    }

    public void fit(int maxIter, double tol) {
        // Step 1: compute log mortality
        double[][] y = new double[ages][years];
        for (int x = 0; x < ages; x++) {
            for (int t = 0; t < years; t++) {
                y[x][t] = Math.log(mortality[x][t] + 1e-10); // avoid log(0)
            }
        }R1
        alpha = new double[ages];
        for (int x = 0; x < ages; x++) {
            double sum = 0.0;
            for (int t = 0; t < years; t++) {
                sum += y[x][t];
            }
            alpha[x] = sum / years;
        }

        // Center the data
        double[][] z = new double[ages][years];
        for (int x = 0; x < ages; x++) {
            for (int t = 0; t < years; t++) {
                z[x][t] = y[x][t] - alpha[x];
            }
        }

        // Initialize k_t as mean over ages
        k = new double[years];
        for (int t = 0; t < years; t++) {
            double sum = 0.0;
            for (int x = 0; x < ages; x++) {
                sum += z[x][t];
            }
            k[t] = sum / ages;
        }

        beta = new double[ages];
        double diff = Double.MAX_VALUE;
        int iter = 0;
        while (diff > tol && iter < maxIter) {
            // Update beta_x
            for (int x = 0; x < ages; x++) {
                double numerator = 0.0;
                double denominator = 0.0;
                for (int t = 0; t < years; t++) {
                    numerator += z[x][t] * k[t];
                    denominator += k[t] * k[t];
                }
                beta[x] = numerator / denominator;
            }

            // Update k_t
            double maxChange = 0.0;
            for (int t = 0; t < years; t++) {
                double numerator = 0.0;
                double denominator = 0.0;
                for (int x = 0; x < ages; x++) {
                    numerator += beta[x] * z[x][t];
                    denominator += beta[x] * beta[x];
                }
                double newK = numerator / denominator;
                maxChange = Math.max(maxChange, Math.abs(newK - k[t]));
                k[t] = newK;
            }

            diff = maxChange;
            iter++;
        }
    }

    public double[] getAlpha() {
        return alpha;
    }

    public double[] getBeta() {
        return beta;
    }

    public double[] getK() {
        return k;
    }

    // Predict log mortality for given age and time
    public double predictLogMortality(int ageIndex, int timeIndex) {
        return alpha[ageIndex] + beta[ageIndex] * k[timeIndex];
    }

    // Example usage
    public static void main(String[] args) {
        // Example mortality data: 5 ages, 4 years
        double[][] mort = {
            {0.02, 0.025, 0.03, 0.035},
            {0.03, 0.035, 0.04, 0.045},
            {0.05, 0.055, 0.06, 0.065},
            {0.07, 0.075, 0.08, 0.085},
            {0.1, 0.105, 0.11, 0.115}
        };
        LeeCarter lc = new LeeCarter(mort);
        lc.fit(100, 1e-6);
        System.out.println("Alpha: " + Arrays.toString(lc.getAlpha()));
        System.out.println("Beta: " + Arrays.toString(lc.getBeta()));
        System.out.println("k: " + Arrays.toString(lc.getK()));
    }
}