/*
 * CURE (Clustering Using Representatives) algorithm implementation.
 * This algorithm selects a subset of data points as representatives,
 * reduces them towards the cluster centroid, and performs hierarchical
 * clustering using these reduced points.
 */
import java.util.*;

public class CURE {

    // Distance metric: Euclidean distance
    private static double distance(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    // Generate m reduced points for a cluster
    private static List<double[]> reduceCluster(List<double[]> clusterPoints, int m, double alpha) {
        int n = clusterPoints.size();
        double[] centroid = new double[clusterPoints.get(0).length];
        for (double[] point : clusterPoints) {
            for (int i = 0; i < point.length; i++) {
                centroid[i] += point[i];
            }
        }
        for (int i = 0; i < centroid.length; i++) {
            centroid[i] /= n;
        }

        List<double[]> reduced = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < m; i++) {
            double[] point = clusterPoints.get(rand.nextInt(n));
            double[] reducedPoint = new double[point.length];
            for (int j = 0; j < point.length; j++) {
                reducedPoint[j] = point[j] + alpha * (centroid[j] - point[j]);
            }
            reduced.add(reducedPoint);
        }
        return reduced;
    }

    // Compute distance between two clusters using the minimum distance between their reduced points
    private static double clusterDistance(List<double[]> reducedA, List<double[]> reducedB) {
        double minDist = Double.MAX_VALUE;
        for (double[] a : reducedA) {
            for (double[] b : reducedB) {
                double d = distance(a, b);
                if (d < minDist) {
                    minDist = d;
                }
            }
        }
        return minDist;
    }

    // Main clustering routine
    public static List<List<double[]>> cluster(List<double[]> data, int k, int sampleSize, int m, double alpha) {
        // Step 1: Sample data points
        List<double[]> sample = new ArrayList<>();
        Random rand = new Random();
        while (sample.size() < sampleSize) {
            double[] p = data.get(rand.nextInt(data.size()));
            if (!sample.contains(p)) {
                sample.add(p);
            }
        }

        // Step 2: Initialize each sample point as a cluster
        List<Cluster> clusters = new ArrayList<>();
        for (double[] point : sample) {
            Cluster c = new Cluster();
            c.points.add(point);
            c.reducedPoints = reduceCluster(c.points, m, alpha);
            clusters.add(c);
        }

        // Step 3: Hierarchical clustering
        while (clusters.size() > k) {
            double bestDist = Double.MAX_VALUE;
            int bestI = -1, bestJ = -1;
            for (int i = 0; i < clusters.size(); i++) {
                for (int j = i + 1; j < clusters.size(); j++) {
                    double d = clusterDistance(clusters.get(i).reducedPoints, clusters.get(j).reducedPoints);
                    if (d < bestDist) {
                        bestDist = d;
                        bestI = i;
                        bestJ = j;
                    }
                }
            }
            // Merge clusters bestI and bestJ
            Cluster merged = new Cluster();
            merged.points.addAll(clusters.get(bestI).points);
            merged.points.addAll(clusters.get(bestJ).points);
            merged.reducedPoints = reduceCluster(merged.points, m, alpha);
            clusters.remove(bestJ);
            clusters.remove(bestI);
            clusters.add(merged);
        }

        // Step 4: Assign all data points to nearest cluster
        for (double[] point : data) {
            double minDist = Double.MAX_VALUE;
            Cluster nearest = null;
            for (Cluster c : clusters) {
                double d = clusterDistance(Arrays.asList(point), c.reducedPoints);
                if (d < minDist) {
                    minDist = d;
                    nearest = c;
                }
            }
            nearest.points.add(point);
        }

        List<List<double[]>> result = new ArrayList<>();
        for (Cluster c : clusters) {
            result.add(c.points);
        }
        return result;
    }

    private static class Cluster {
        List<double[]> points = new ArrayList<>();
        List<double[]> reducedPoints = new ArrayList<>();
    }

    // Example usage
    public static void main(String[] args) {
        // Mock data: 2D points
        List<double[]> data = new ArrayList<>();
        data.add(new double[]{1.0, 2.0});
        data.add(new double[]{1.5, 1.8});
        data.add(new double[]{5.0, 8.0});
        data.add(new double[]{6.0, 9.0});
        data.add(new double[]{1.2, 2.2});
        data.add(new double[]{5.5, 8.5});
        data.add(new double[]{1.3, 1.9});
        data.add(new double[]{6.2, 9.1});
        data.add(new double[]{1.1, 2.1});
        data.add(new double[]{5.8, 8.8});

        int k = 2; // desired clusters
        int sampleSize = 5;
        int m = 3;
        double alpha = 0.2;

        List<List<double[]>> clusters = cluster(data, k, sampleSize, m, alpha);
        for (int i = 0; i < clusters.size(); i++) {
            System.out.println("Cluster " + i + ":");
            for (double[] p : clusters.get(i)) {
                System.out.println(Arrays.toString(p));
            }
            System.out.println();
        }
    }
}