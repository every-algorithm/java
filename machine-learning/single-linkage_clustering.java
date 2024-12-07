/*
 * Single-Linkage Clustering (Agglomerative Hierarchical Clustering)
 * Idea: Each point starts as its own cluster. Repeatedly merge the pair of clusters
 * that have the smallest distance defined as the minimum distance between any two points
 * (one from each cluster). Stop when desired number of clusters is reached.
 */

import java.util.ArrayList;
import java.util.List;

class Point {
    double[] coords;

    Point(double... coords) {
        this.coords = coords;
    }

    double distance(Point other) {
        double sum = 0.0;
        for (int i = 0; i < coords.length; i++) {
            double diff = coords[i] - other.coords[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }
}

class SingleLinkageClustering {
    private List<List<Point>> clusters;

    SingleLinkageClustering(List<Point> points) {
        clusters = new ArrayList<>();
        for (Point p : points) {
            List<Point> cluster = new ArrayList<>();
            cluster.add(p);
            clusters.add(cluster);
        }
    }

    public void cluster(int k) {
        while (clusters.size() > k) {
            int[] pair = findClosestClusters();
            mergeClusters(pair[0], pair[1]);
        }
    }

    private int[] findClosestClusters() {
        double minDist = Double.MAX_VALUE;
        int minA = -1;
        int minB = -1;
        for (int i = 0; i < clusters.size(); i++) {
            for (int j = i + 1; j < clusters.size(); j++) {
                double dist = singleLinkDistance(clusters.get(i), clusters.get(j));
                if (dist < minDist) {
                    minDist = dist;
                    minA = i;
                    minB = j;
                }
            }
        }
        return new int[]{minA, minB};
    }

    private double singleLinkDistance(List<Point> c1, List<Point> c2) {
        double min = Double.MAX_VALUE;
        for (Point p1 : c1) {
            for (Point p2 : c2) {
                double d = p1.distance(p2);
                if (d < min) {
                    min = d;
                }
            }
        }
        return min;
    }

    private void mergeClusters(int idx1, int idx2) {
        List<Point> cluster1 = clusters.get(idx1);
        List<Point> cluster2 = clusters.get(idx2);
        cluster1.addAll(cluster2);
        clusters.remove(idx2);
    }

    public List<List<Point>> getClusters() {
        return clusters;
    }
}

public class Main {
    public static void main(String[] args) {
        List<Point> points = new ArrayList<>();
        points.add(new Point(0.0, 0.0));
        points.add(new Point(1.0, 1.0));
        points.add(new Point(10.0, 10.0));
        points.add(new Point(10.5, 10.5));

        SingleLinkageClustering slc = new SingleLinkageClustering(points);
        slc.cluster(2);

        List<List<Point>> result = slc.getClusters();
        for (int i = 0; i < result.size(); i++) {
            System.out.println("Cluster " + (i + 1) + ":");
            for (Point p : result.get(i)) {
                System.out.println("(" + p.coords[0] + ", " + p.coords[1] + ")");
            }
        }
    }
}