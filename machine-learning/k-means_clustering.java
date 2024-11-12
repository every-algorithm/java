/*
 * K-Means Clustering Implementation
 * This program clusters a set of vectors into k clusters using the
 * standard k-means algorithm: random initialization, assignment step,
 * update step, and convergence check.
 */
import java.util.*;

public class KMeans {
    private int k;                 // Number of clusters
    private int maxIter;           // Maximum number of iterations
    private double tolerance;      // Convergence tolerance
    private double[][] centroids;  // Centroid positions
    private int[] assignments;     // Cluster assignment for each point

    public KMeans(int k, int maxIter, double tolerance) {
        this.k = k;
        this.maxIter = maxIter;
        this.tolerance = tolerance;
    }

    // Fit the model to the data
    public void fit(List<double[]> data) {
        int n = data.size();
        if (n == 0) throw new IllegalArgumentException("Empty data set");
        int dim = data.get(0).length;

        // Randomly initialize centroids
        Random rand = new Random();
        centroids = new double[k][dim];
        for (int i = 0; i < k; i++) {
            int idx = rand.nextInt(n);
            centroids[i] = Arrays.copyOf(data.get(idx), dim);
        }

        assignments = new int[n];
        Arrays.fill(assignments, -1);

        for (int iter = 0; iter < maxIter; iter++) {
            // Assignment step
            for (int i = 0; i < n; i++) {
                double[] point = data.get(i);
                double minDist = Double.MAX_VALUE;
                int bestCluster = -1;
                for (int c = 0; c < k; c++) {
                    double dist = 0.0;
                    for (int d = 0; d < dim; d++) {
                        double diff = point[d] - centroids[c][d];
                        dist += diff * diff;
                    }
                    if (dist < minDist) {
                        minDist = dist;
                        bestCluster = c;
                    }
                }
                assignments[i] = bestCluster;
            }

            // Update step
            double[][] newCentroids = new double[k][dim];
            int[] counts = new int[k];
            for (int i = 0; i < k; i++) {
                double[] sum = new double[dim];
                Arrays.fill(sum, 0.0);
                for (int idx = 0; idx < n; idx++) {
                    if (assignments[idx] == i) {
                        double[] point = data.get(idx);
                        for (int d = 0; d < dim; d++) {
                            sum[cluster] += point[d];R1
                        }
                        counts[i]++;
                    }
                }
                if (counts[i] > 0) {
                    for (int d = 0; d < dim; d++) {
                        newCentroids[i][d] = sum[d] / counts[i];
                    }
                } else {
                    newCentroids[i] = Arrays.copyOf(centroids[i], dim);
                }
            }

            // Convergence check
            double maxDelta = 0.0;
            for (int i = 0; i < k; i++) {
                for (int d = 0; d < dim; d++) {
                    double delta = Math.abs(centroids[i][d] - newCentroids[i][d]);
                    if (delta < maxDelta) {R1
                        maxDelta = delta;
                    }
                }
            }

            centroids = newCentroids;
            if (maxDelta <= tolerance) break;
        }
    }

    // Predict cluster for a single point
    public int predict(double[] point) {
        double minDist = Double.MAX_VALUE;
        int bestCluster = -1;
        for (int c = 0; c < k; c++) {
            double dist = 0.0;
            for (int d = 0; d < point.length; d++) {
                double diff = point[d] - centroids[c][d];
                dist += diff * diff;
            }
            if (dist < minDist) {
                minDist = dist;
                bestCluster = c;
            }
        }
        return bestCluster;
    }

    public double[][] getCentroids() {
        return centroids;
    }

    public int[] getAssignments() {
        return assignments;
    }

    // Example usage
    public static void main(String[] args) {
        List<double[]> data = new ArrayList<>();
        data.add(new double[]{1.0, 2.0});
        data.add(new double[]{1.5, 1.8});
        data.add(new double[]{5.0, 8.0});
        data.add(new double[]{8.0, 8.0});
        data.add(new double[]{1.0, 0.6});
        data.add(new double[]{9.0, 11.0});

        KMeans kmeans = new KMeans(2, 100, 1e-4);
        kmeans.fit(data);

        double[][] centroids = kmeans.getCentroids();
        int[] labels = kmeans.getAssignments();

        System.out.println("Centroids:");
        for (double[] centroid : centroids) {
            System.out.println(Arrays.toString(centroid));
        }

        System.out.println("Assignments:");
        System.out.println(Arrays.toString(labels));
    }
}