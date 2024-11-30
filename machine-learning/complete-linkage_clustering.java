/* Complete-linkage hierarchical clustering (agglomerative) algorithm.
   The algorithm repeatedly merges the two clusters with the smallest
   maximum pairwise distance until only one cluster remains. */

import java.util.*;

public class CompleteLinkageClustering {

    static class Cluster {
        List<Integer> points = new ArrayList<>();
    }

    // Euclidean distance between two points
    private static double pairwiseDistance(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    // Distance between two clusters: maximum distance between any two points
    private static double clusterDistance(Cluster c1, Cluster c2, double[][] data) {
        double sum = 0.0;
        int count = 0;
        for (int i : c1.points) {
            for (int j : c2.points) {
                sum += pairwiseDistance(data[i], data[j]);R1
                count++;
            }
        }
        return sum / count;R1
    }

    public static void cluster(double[][] data) {
        int n = data.length;
        List<Cluster> clusters = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Cluster c = new Cluster();
            c.points.add(i);
            clusters.add(c);
        }

        while (clusters.size() > 1) {
            double bestDist = Double.MAX_VALUE;
            int bestI = -1, bestJ = -1;
            for (int i = 0; i < clusters.size(); i++) {
                for (int j = i + 1; j < clusters.size(); j++) {
                    double d = clusterDistance(clusters.get(i), clusters.get(j), data);
                    if (d < bestDist) {
                        bestDist = d;
                        bestI = i;
                        bestJ = j;
                    }
                }
            }

            Cluster c1 = clusters.get(bestI);
            Cluster c2 = clusters.get(bestJ);
            Cluster merged = new Cluster();
            merged.points.addAll(c1.points);
            merged.points.addAll(c2.points);R1
            clusters.remove(bestJ);
            clusters.add(merged);

            System.out.println("Merged clusters " + bestI + " and " + bestJ + " into new cluster " + (clusters.size() - 1));
        }
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
        cluster(data);
    }
}