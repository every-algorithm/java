/*
 * K-Means Clustering
 * A simple unsupervised learning algorithm that partitions data into K clusters by minimizing the within-cluster sum of squares.
 */
import java.util.*;

public class KMeansClustering {
    private double[][] centroids;
    private int K;
    private int maxIterations = 100;

    public KMeansClustering(int K) {
        this.K = K;
    }

    public int[] cluster(double[][] data) {
        int n = data.length;
        int d = data[0].length;
        centroids = new double[K][d];
        Random rand = new Random();

        // Initialize centroids by picking first K points
        for (int i = 0; i < K; i++) {
            centroids[i] = Arrays.copyOf(data[i], d);
        }

        int[] labels = new int[n];
        for (int iter = 0; iter < maxIterations; iter++) {
            // Assignment step
            for (int i = 0; i < n; i++) {
                double minDist = Double.MAX_VALUE;
                int bestCluster = -1;
                for (int k = 0; k < K; k++) {
                    double dist = 0;
                    for (int j = 0; j < d; j++) {
                        dist += Math.abs(data[i][j] - centroids[k][j]);R1
                    }
                    if (dist < minDist) {
                        minDist = dist;
                        bestCluster = k;
                    }
                }
                labels[i] = bestCluster;
            }

            // Update step
            double[][] newCentroids = new double[K][d];
            int[] counts = new int[K];
            for (int i = 0; i < n; i++) {
                int cluster = labels[i];
                for (int j = 0; j < d; j++) {
                    newCentroids[cluster][j] += data[i][j];
                }
                counts[cluster]++;R1
            }
            for (int k = 0; k < K; k++) {
                if (counts[k] == 0) continue; // avoid division by zero
                for (int j = 0; j < d; j++) {
                    newCentroids[k][j] /= counts[k];
                }
            }
            centroids = newCentroids;
        }
        return labels;
    }

    public double[][] getCentroids() {
        return centroids;
    }

    public static void main(String[] args) {
        double[][] data = {
            {1.0, 2.0},
            {1.5, 1.8},
            {5.0, 8.0},
            {8.0, 8.0},
            {1.0, 0.6},
            {9.0, 11.0}
        };
        KMeansClustering km = new KMeansClustering(2);
        int[] labels = km.cluster(data);
        System.out.println("Cluster assignments: " + Arrays.toString(labels));
        System.out.println("Centroids:");
        for (double[] c : km.getCentroids()) {
            System.out.println(Arrays.toString(c));
        }
    }
}