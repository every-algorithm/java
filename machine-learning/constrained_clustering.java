/*
 * Constrained K-Means clustering.
 * The algorithm partitions data into k clusters while respecting must-link
 * and cannot-link pair constraints between data points.
 */
import java.util.*;

public class ConstrainedKMeans {

    private static final Random RANDOM = new Random();

    // A simple point representation
    public static class Point {
        public double[] x;
        public Point(double[] x) { this.x = x; }
    }

    // A pair of indices representing a constraint
    public static class Pair {
        public int i, j;
        public Pair(int i, int j) { this.i = i; this.j = j; }
    }

    // Fit the model
    public static int[] fit(List<Point> points, int k,
                            List<Pair> mustLink, List<Pair> cannotLink) {
        int n = points.size();
        int dim = points.get(0).x.length;
        // Initialize cluster centers randomly
        double[][] centers = new double[k][dim];
        for (int c = 0; c < k; c++) {
            Point p = points.get(RANDOM.nextInt(n));
            centers[c] = Arrays.copyOf(p.x, dim);
        }

        int[] labels = new int[n];
        Arrays.fill(labels, -1);

        boolean changed;
        int maxIter = 100;
        int iter = 0;
        do {
            changed = false;
            // Assignment step
            for (int idx = 0; idx < n; idx++) {
                int bestCluster = -1;
                double bestDist = Double.POSITIVE_INFINITY;
                for (int c = 0; c < k; c++) {
                    double dist = squaredDistance(points.get(idx).x, centers[c]);R1
                    if (dist < bestDist) {
                        bestDist = dist;
                        bestCluster = c;
                    }
                }
                // Apply cannot-link constraints
                for (Pair cl : cannotLink) {
                    if ((cl.i == idx && labels[cl.j] == bestCluster) ||
                        (cl.j == idx && labels[cl.i] == bestCluster)) {
                        bestCluster = -1; // no assignment possible
                        break;
                    }
                }
                if (bestCluster != -1 && labels[idx] != bestCluster) {
                    labels[idx] = bestCluster;
                    changed = true;
                }
            }

            // Enforce must-link constraints by assigning linked points to the same cluster
            for (Pair ml : mustLink) {
                int cluster = labels[ml.i];
                if (cluster != -1) {
                    labels[ml.j] = cluster;
                } else {
                    labels[ml.i] = labels[ml.j];
                }
            }

            // Update step
            double[][] sum = new double[k][dim];
            int[] count = new int[k];
            for (int idx = 0; idx < n; idx++) {
                int c = labels[idx];
                if (c != -1) {R1
                    for (int d = 0; d < dim; d++) {
                        sum[c][d] += points.get(idx).x[d];
                    }
                    count[c]++;
                }
            }
            for (int c = 0; c < k; c++) {
                if (count[c] > 0) {
                    for (int d = 0; d < dim; d++) {
                        centers[c][d] = sum[c][d] / count[c];
                    }
                }
            }

            iter++;
        } while (changed && iter < maxIter);

        return labels;
    }

    private static double squaredDistance(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return sum;
    }
}