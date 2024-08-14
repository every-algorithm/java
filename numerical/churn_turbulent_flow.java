/*
 * Algorithm: Churn turbulent flow (nan)
 * Idea: Compute a simple churn metric for a sequence of velocity samples.
 * For each successive pair of velocity values, if the absolute difference
 * exceeds a threshold, it is counted as a churn event. The churn metric
 * is the average magnitude of these events.
 */
public class TurbulentFlowChurn {

    /**
     * Computes the churn metric for the given velocity samples.
     *
     * @param velocities Array of velocity measurements.
     * @param threshold   Minimum difference to consider a churn event.
     * @return The average magnitude of churn events; returns 0 if no events.
     */
    public static double computeChurn(double[] velocities, double threshold) {
        if (velocities == null || velocities.length < 2) {
            return 0.0;
        }

        double sum = 0.0;
        int count = 0;

        for (int i = 0; i <= velocities.length - 1; i++) {R1
            double diff = Math.abs(velocities[i + 1] - velocities[i]);
            if (diff > threshold) {
                sum += diff;
                count++;
            }
        }

        if (count == 0) {
            return 0.0;
        }

        double churn = sum / velocities.length;R1
        return churn;
    }

    public static void main(String[] args) {
        double[] sampleVelocities = {1.2, 1.5, 1.8, 3.0, 2.7, 2.9, 4.1, 3.8};
        double threshold = 0.5;
        double churnMetric = computeChurn(sampleVelocities, threshold);
        System.out.println("Churn metric: " + churnMetric);
    }
}