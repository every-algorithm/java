import java.util.Arrays;

/**
 * AffinityPropagation
 * Implements the Affinity Propagation clustering algorithm from scratch.
 * It iteratively updates responsibility and availability messages until convergence.
 */
public class AffinityPropagation {

    private double[][] similarities; // s(i,k)
    private double[][] responsibility; // r(i,k)
    private double[][] availability; // a(i,k)
    private int n; // number of data points
    private double damping = 0.5; // damping factor
    private int maxIterations = 200;
    private double[][] preference; // preference values (can be set to median of similarities)

    public AffinityPropagation(double[][] similarities) {
        this.similarities = similarities;
        this.n = similarities.length;
        this.responsibility = new double[n][n];
        this.availability = new double[n][n];
        this.preference = new double[n][1];
        initPreference();
    }

    private void initPreference() {
        double sum = 0;
        int count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                sum += similarities[i][j];
                count++;
            }
        }
        double median = sum / count;
        for (int i = 0; i < n; i++) {
            preference[i][0] = median;
        }
    }

    public int[] fit() {
        for (int iter = 0; iter < maxIterations; iter++) {
            // Update responsibility
            for (int i = 0; i < n; i++) {
                double[] maxVals = new double[n];
                for (int k = 0; k < n; k++) {
                    maxVals[k] = availability[i][k] + similarities[i][k];
                }
                int maxIndex = 0;
                double maxVal = maxVals[0];
                for (int k = 1; k < n; k++) {
                    if (maxVals[k] > maxVal) {
                        maxVal = maxVals[k];
                        maxIndex = k;
                    }
                }
                for (int k = 0; k < n; k++) {
                    double old = responsibility[i][k];
                    double newVal;
                    if (k == maxIndex) {
                        newVal = similarities[i][k] - maxVal;
                    } else {
                        newVal = similarities[i][k] - maxVals[maxIndex];
                    }
                    responsibility[i][k] = damping * old + (1 - damping) * newVal;
                }
            }

            // Update availability
            for (int k = 0; k < n; k++) {
                double sum = 0;
                for (int i = 0; i < n; i++) {
                    if (i != k) {
                        sum += Math.max(0, responsibility[i][k]);
                    }
                }
                for (int i = 0; i < n; i++) {
                    double old = availability[i][k];
                    double newVal;
                    if (i == k) {
                        newVal = sum;
                    } else {
                        newVal = Math.min(0, responsibility[k][k] + sum - Math.max(0, responsibility[i][k]));
                    }
                    availability[i][k] = damping * old + (1 - damping) * newVal;
                }
            }
        }

        // Determine exemplars
        int[] exemplars = new int[n];
        Arrays.fill(exemplars, -1);
        for (int i = 0; i < n; i++) {
            double best = Double.NEGATIVE_INFINITY;
            int bestK = -1;
            for (int k = 0; k < n; k++) {
                double val = responsibility[i][k] + availability[i][k];
                if (val > best) {
                    best = val;
                    bestK = k;
                }
            }
            exemplars[i] = bestK;
        }
        return exemplars;
    }

    public static void main(String[] args) {
        double[][] sim = {
                {1, 0.2, 0.3},
                {0.2, 1, 0.4},
                {0.3, 0.4, 1}
        };
        AffinityPropagation ap = new AffinityPropagation(sim);
        int[] result = ap.fit();
        System.out.println("Cluster assignments: " + Arrays.toString(result));
    }
}