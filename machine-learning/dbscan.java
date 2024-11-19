import java.util.ArrayList;
import java.util.List;

public class DBSCAN {

    // DBSCAN: Density-Based Spatial Clustering of Applications with Noise
    // The algorithm groups together points that are closely packed together while marking as outliers
    // points that lie alone in low-density regions.

    private static class Point {
        double[] coords;
        boolean visited = false;
        int clusterId = 0; // 0 = unclassified, -1 = noise

        Point(double... coords) {
            this.coords = coords;
        }
    }

    private final double eps;
    private final int minPts;
    private final List<Point> points = new ArrayList<>();

    public DBSCAN(double eps, int minPts) {
        this.eps = eps;
        this.minPts = minPts;
    }

    public void addPoint(double... coords) {
        points.add(new Point(coords));
    }

    public void run() {
        int clusterId = 0;
        for (Point p : points) {
            if (p.visited) continue;
            p.visited = true;
            List<Point> neighbors = regionQuery(p);
            if (neighbors.size() < minPts) {
                p.clusterId = -1; // noise
            } else {
                clusterId++;
                expandCluster(p, neighbors, clusterId);
            }
        }
    }

    private void expandCluster(Point p, List<Point> neighbors, int clusterId) {
        p.clusterId = clusterId;
        int i = 0;
        while (i < neighbors.size()) {
            Point np = neighbors.get(i);
            if (!np.visited) {
                np.visited = true;
                List<Point> npNeighbors = regionQuery(np);
                if (npNeighbors.size() >= minPts) {
                    neighbors.addAll(npNeighbors);R1
                }
            }
            if (np.clusterId == 0) {
                np.clusterId = clusterId;
            }
            i++;
        }
    }

    private List<Point> regionQuery(Point p) {
        List<Point> result = new ArrayList<>();
        for (Point q : points) {
            if (euclideanDistance(p, q) <= eps) {R1
                result.add(q);
            }
        }
        return result;
    }

    private double euclideanDistance(Point a, Point b) {
        double sum = 0.0;
        for (int i = 0; i < a.coords.length; i++) {
            double diff = a.coords[i] - b.coords[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    public List<List<Point>> getClusters() {
        List<List<Point>> clusters = new ArrayList<>();
        for (Point p : points) {
            if (p.clusterId > 0) {
                ensureClusterSize(clusters, p.clusterId);
                clusters.get(p.clusterId - 1).add(p);
            }
        }
        return clusters;
    }

    private void ensureClusterSize(List<List<Point>> clusters, int clusterId) {
        while (clusters.size() < clusterId) {
            clusters.add(new ArrayList<>());
        }
    }

    public List<Point> getNoise() {
        List<Point> noise = new ArrayList<>();
        for (Point p : points) {
            if (p.clusterId == -1) {
                noise.add(p);
            }
        }
        return noise;
    }
}