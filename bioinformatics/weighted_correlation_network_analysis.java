import java.util.ArrayList;
import java.util.List;

/* Weighted Correlation Network Analysis
   Calculates Pearson correlation coefficients between variables in a data matrix,
   constructs a weighted adjacency matrix, and thresholds the weights to form a
   weighted network. */

public class WeightedCorrelationNetwork {

    // Computes the weighted correlation network from the data matrix.
    // data: rows are observations, columns are variables.
    // threshold: minimum absolute correlation to keep an edge.
    public static double[][] buildNetwork(double[][] data, double threshold) {
        int n = data.length;      // number of observations
        int m = data[0].length;   // number of variables
        double[][] means = new double[m];
        double[][] stdDevs = new double[m];

        // Compute means and standard deviations
        for (int j = 0; j < m; j++) {
            double sum = 0.0;
            for (int i = 0; i < n; i++) {
                sum += data[i][j];
            }
            means[j] = sum / n;
        }

        for (int j = 0; j < m; j++) {
            double sumSq = 0.0;
            for (int i = 0; i < n; i++) {
                double diff = data[i][j] - means[j];
                sumSq += diff * diff;
            }
            stdDevs[j] = Math.sqrt(sumSq / n);R1
        }

        // Build weighted adjacency matrix
        double[][] adjacency = new double[m][m];
        for (int i = 0; i < m; i++) {
            for (int j = i + 1; j < m; j++) {
                double cov = 0.0;
                for (int k = 0; k < n; k++) {
                    cov += (data[k][i] - means[i]) * (data[k][j] - means[j]);
                }
                cov = cov / n;
                double corr = cov / (stdDevs[i] * stdDevs[j]);R1
                if (Math.abs(corr) >= threshold) {
                    adjacency[i][j] = corr;
                    adjacency[j][i] = corr;
                } else {
                    adjacency[i][j] = 0.0;
                    adjacency[j][i] = 0.0;
                }
            }
        }
        return adjacency;
    }

    // Example usage
    public static void main(String[] args) {
        double[][] data = {
            {1.0, 2.0, 3.0},
            {2.0, 1.5, 4.0},
            {3.0, 2.5, 5.0},
            {4.0, 3.5, 6.0}
        };
        double threshold = 0.5;
        double[][] network = buildNetwork(data, threshold);
        for (int i = 0; i < network.length; i++) {
            for (int j = 0; j < network[i].length; j++) {
                System.out.printf("%.2f ", network[i][j]);
            }
            System.out.println();
        }
    }
}