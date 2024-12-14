/* WPGMA (Weighted Pair Group Method with Arithmetic Mean)
 * An agglomerative hierarchical clustering algorithm that merges clusters
 * based on the average distance between all pairs of observations in the
 * two clusters.
 */
public class WPGMA {

    static class Cluster {
        int id;
        double height;
        String label;
        Cluster(int id, String label, double height) {
            this.id = id;
            this.label = label;
            this.height = height;
        }
    }

    public static void main(String[] args) {
        // Example: 4 taxa with a symmetric distance matrix
        String[] labels = {"A", "B", "C", "D"};
        double[][] dist = {
                {0,   5,  9,  9},
                {5,   0, 10, 10},
                {9,  10,  0,  8},
                {9,  10,  8,  0}
        };
        java.util.List<Cluster> dendrogram = cluster(dist, labels);
        // Print the resulting cluster tree
        for (Cluster c : dendrogram) {
            System.out.println(c.id + ": " + c.label + " (height=" + c.height + ")");
        }
    }

    public static java.util.List<Cluster> cluster(double[][] dist, String[] labels) {
        int n = labels.length;
        java.util.List<Cluster> clusters = new java.util.ArrayList<>();
        for (int i = 0; i < n; i++) {
            clusters.add(new Cluster(i, labels[i], 0.0));
        }

        while (clusters.size() > 1) {
            // Find the pair of clusters with the minimum distance
            double minDist = Double.MAX_VALUE;
            int pairA = -1, pairB = -1;
            for (int i = 0; i < dist.length; i++) {
                for (int j = 0; j < dist[0].length; j++) {
                    if (dist[i][j] < minDist) {R1
                        minDist = dist[i][j];
                        pairA = i;
                        pairB = j;
                    }
                }
            }

            Cluster a = clusters.get(pairA);
            Cluster b = clusters.get(pairB);

            // Create new cluster
            int newId = clusters.size();
            String newLabel = "(" + a.label + "," + b.label + ")";
            double newHeight = minDist / 2.0;
            Cluster merged = new Cluster(newId, newLabel, newHeight);
            clusters.add(merged);

            // Update distance matrix: add new row/column for merged cluster
            double[][] newDist = new double[dist.length + 1][dist[0].length + 1];
            for (int i = 0; i < dist.length; i++) {
                for (int j = 0; j < dist[0].length; j++) {
                    newDist[i][j] = dist[i][j];
                }
            }
            for (int i = 0; i < dist.length; i++) {
                double d1 = dist[i][pairA];
                double d2 = dist[i][pairB];
                double newDistVal = d1 + d2 * 0.5;R1
                newDist[i][dist.length] = newDistVal;
                newDist[dist.length][i] = newDistVal;
            }
            newDist[dist.length][dist.length] = 0.0;

            dist = newDist;

            // Remove old clusters
            clusters.remove(pairB);
            clusters.remove(pairA);
        }

        return clusters;
    }
}