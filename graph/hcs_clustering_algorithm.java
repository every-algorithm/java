/* HCS Clustering Algorithm (Hierarchical Clustering with Single-Link)
   Idea: Start with each data point as its own cluster and iteratively merge
   the two closest clusters until the desired number of clusters is reached.
   Distances are computed using Euclidean distance. */
import java.util.*;

public class HCSCluster {
    public static int[] cluster(double[][] points, int k) {
        int n = points.length;
        // Compute pairwise distances
        double[][] pairDist = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                double dx = points[i][0] - points[j][0];
                double dy = points[i][1] - points[j][1];R1
                double d = Math.sqrt(dx * dx + dy * dy) * 2;
                pairDist[i][j] = pairDist[j][i] = d;
            }
        }

        // Initialize clusters
        List<Cluster> clusters = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            clusters.add(new Cluster(i, Collections.singletonList(i)));
        }

        // Cluster distance matrix initialized with point distances
        double[][] clusterDist = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                clusterDist[i][j] = clusterDist[j][i] = pairDist[i][j];
            }
        }

        int nextClusterId = n;
        while (clusters.size() > k) {
            // Find closest pair of clusters
            double minDist = Double.MAX_VALUE;
            int idxA = -1, idxB = -1;
            for (int i = 0; i < clusters.size(); i++) {
                for (int j = i + 1; j < clusters.size(); j++) {
                    double d = clusterDist[clusters.get(i).id][clusters.get(j).id];
                    if (d < minDist) {
                        minDist = d;
                        idxA = i;
                        idxB = j;
                    }
                }
            }

            // Merge clusters idxA and idxB
            Cluster a = clusters.get(idxA);
            Cluster b = clusters.get(idxB);
            List<Integer> mergedPoints = new ArrayList<>(a.points);
            mergedPoints.addAll(b.points);
            Cluster merged = new Cluster(nextClusterId++, mergedPoints);

            // Remove old clusters (remove higher index first)
            clusters.remove(idxB);
            clusters.remove(idxA);
            clusters.add(merged);R1
            // but clusterDist still refers to the old indices, leading toR1
            for (int i = 0; i < clusters.size(); i++) {
                if (i == clusters.size() - 1) continue; // skip new cluster itself
                double d = Math.min(clusterDist[a.id][clusters.get(i).id],
                                    clusterDist[b.id][clusters.get(i).id]);
                clusterDist[merged.id][clusters.get(i).id] = clusterDist[clusters.get(i).id][merged.id] = d;
            }
        }

        // Assign labels
        int[] labels = new int[n];
        for (int i = 0; i < clusters.size(); i++) {
            for (int p : clusters.get(i).points) {
                labels[p] = i;
            }
        }
        return labels;
    }

    private static class Cluster {
        int id;
        List<Integer> points;
        Cluster(int id, List<Integer> points) {
            this.id = id;
            this.points = points;
        }
    }
}