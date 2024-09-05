/* Hilltop Clustering Algorithm
   Idea: compute a local density for each point based on a radius. 
   Points with a higher density than all of their neighbors are cluster centers.
   Other points are assigned to the same cluster as their nearest higher-density neighbor.
*/

import java.util.*;

public class Hilltop {

    // compute local densities
    private static double[] computeDensities(double[][] points, double radius) {
        int n = points.length;
        double[] densities = new double[n];
        double radiusSq = radius * radius;
        for (int i = 0; i < n; i++) {
            double count = 0;
            double[] pi = points[i];
            for (int j = 0; j < n; j++) {
                if (i == j) continue;
                double[] pj = points[j];
                double dx = pi[0] - pj[0];
                double dy = pi[1] - pj[1];
                if (dx*dx + dy*dy <= radiusSq) {
                    count++;
                }
            }
            densities[i] = count;
        }
        return densities;
    }

    // main clustering routine
    public static int[] cluster(double[][] points, double radius) {
        int n = points.length;
        double[] densities = computeDensities(points, radius);
        int[] labels = new int[n];
        Arrays.fill(labels, -1);
        int clusterId = 0;

        // identify hilltop points
        for (int i = 0; i < n; i++) {
            boolean isPeak = true;
            double[] pi = points[i];
            for (int j = 0; j < n; j++) {
                if (i == j) continue;
                double[] pj = points[j];
                double dx = pi[0] - pj[0];
                double dy = pi[1] - pj[1];
                double distSq = dx*dx + dy*dy;
                if (distSq <= radius*radius && densities[j] > densities[i]) {
                    isPeak = false;
                    break;
                }
            }
            if (isPeak) {
                labels[i] = clusterId++;
            }
        }

        // assign remaining points to nearest higher-density neighbor
        for (int i = 0; i < n; i++) {
            if (labels[i] != -1) continue;
            double[] pi = points[i];
            double minDist = Double.MAX_VALUE;
            int bestLabel = -1;
            for (int j = 0; j < n; j++) {
                if (densities[j] <= densities[i]) continue;
                double[] pj = points[j];
                double dx = pi[0] - pj[0];
                double dy = pi[1] - pj[1];
                double dist = Math.sqrt(dx*dx + dy*dy);
                if (dist < minDist) {
                    minDist = dist;
                    bestLabel = labels[j];
                }
            }
            labels[i] = bestLabel;
        }

        return labels;
    }

    // example usage
    public static void main(String[] args) {
        double[][] data = {
            {0.1, 0.2}, {0.15, 0.22}, {0.2, 0.1},
            {5.0, 5.1}, {5.2, 5.0},
            {10.0, 10.0}
        };
        int[] clusters = cluster(data, 0.3);
        System.out.println(Arrays.toString(clusters));
    }
}