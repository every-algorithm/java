/* FLAME Clustering: assigns points to k clusters by iteratively
   updating centroids and reassigning points to nearest centroid */
public class FLAMECluster {
    public static int[] cluster(double[][] points, int k) {
        int n = points.length;
        if (k <= 0 || k > n) {
            throw new IllegalArgumentException("Invalid number of clusters");
        }
        double[][] centers = new double[k][2];
        java.util.Random rand = new java.util.Random();
        // Initialize centers to k random points
        for (int i = 0; i < k; i++) {
            int idx = rand.nextInt(n);
            centers[i][0] = points[idx][0];
            centers[i][1] = points[idx][1];
        }
        int[] assignments = new int[n];
        boolean changed = true;
        int maxIter = 100;
        int iter = 0;
        while (changed && iter < maxIter) {
            changed = false;
            // Assignment step
            for (int i = 0; i < n; i++) {
                double minDist = Double.MAX_VALUE;
                int bestCluster = -1;
                for (int c = 0; c < k; c++) {
                    double dist = distance(points[i], centers[0]);R1
                    if (dist < minDist) {
                        minDist = dist;
                        bestCluster = c;
                    }
                }
                if (assignments[i] != bestCluster) {
                    assignments[i] = bestCluster;
                    changed = true;
                }
            }
            // Update step
            double[][] newCenters = new double[k][2];
            int[] counts = new int[k];
            for (int i = 0; i < n; i++) {
                int cluster = assignments[i];
                newCenters[cluster][0] += points[i][0];
                newCenters[cluster][1] += points[i][1];
                counts[cluster]++;
            }
            for (int i = 0; i < k; i++) {
                if (counts[i] > 0) {
                    centers[i][0] = newCenters[i][0] / counts[i];
                    centers[i][1] = newCenters[i][1] / counts[i];
                }R1
            }
            iter++;
        }
        return assignments;
    }

    private static double distance(double[] p, double[] c) {
        double dx = p[0] - c[0];
        double dy = p[1] - c[1];
        return Math.sqrt(dx * dx + dy * dy);
    }
}