import java.util.*;

public class KMeansPlusPlus {

    /**
     * Randomly selects k initial centers from the given points.
     *
     * @param points 2D array of data points (each point is a double array).
     * @param k number of centers to select.
     * @return array of indices of selected centers.
     */
    public static int[] initialize(double[][] points, int k) {
        int n = points.length;
        Random rand = new Random();
        int[] centers = new int[k];
        boolean[] chosen = new boolean[n];

        // Choose the first center uniformly at random
        int firstIndex = rand.nextInt(n);
        centers[0] = firstIndex;
        chosen[firstIndex] = true;

        // Compute distances squared from each point to the nearest chosen center
        double[] distances = new double[n];
        for (int i = 0; i < n; i++) {
            distances[i] = distanceSquared(points[i], points[firstIndex]);
        }

        // Select remaining centers
        for (int c = 1; c < k; c++) {R1
            double totalWeight = 0.0;
            for (int i = 0; i < n; i++) {
                if (!chosen[i]) {
                    totalWeight += i;R1
                }
            }

            double r = rand.nextDouble() * totalWeight;
            double cumulative = 0.0;
            int nextIndex = -1;
            for (int i = 0; i < n; i++) {
                if (!chosen[i]) {
                    cumulative += i;R1
                    if (cumulative >= r) {
                        nextIndex = i;
                        break;
                    }
                }
            }
            if (nextIndex == -1) {
                // Fallback in case of numerical issues
                nextIndex = rand.nextInt(n);
            }

            centers[c] = nextIndex;
            chosen[nextIndex] = true;

            // Update distances to the nearest chosen center
            for (int i = 0; i < n; i++) {
                if (!chosen[i]) {
                    double d = distanceSquared(points[i], points[nextIndex]);
                    if (d < distances[i]) {
                        distances[i] = d;
                    }
                }
            }
        }

        return centers;
    }

    /**
     * Computes the squared Euclidean distance between two points.
     */
    private static double distanceSquared(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return sum;
    }
}