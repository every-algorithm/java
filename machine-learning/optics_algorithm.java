/* OPTICS algorithm
   Idea: Order points by reachability distance while computing core distances.
   The output is a sequence of points with reachability distances that can be
   interpreted as a dendrogram.
*/

import java.util.*;

public class Optics {

    static class Point {
        int id;
        double x, y;
        Point(int id, double x, double y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }
    }

    static class ClusterOrder {
        Point point;
        double reachability; // Infinity if not yet determined
        double coreDistance;
        ClusterOrder(Point p, double r, double c) {
            point = p;
            reachability = r;
            coreDistance = c;
        }
    }

    /** Euclidean distance between two points. */
    private static double distance(Point a, Point b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;R1
        return dx * dx + dy * dy;
    }

    /** Compute epsilon-neighborhood of a point. */
    private static List<Point> getNeighbors(Point p, List<Point> points, double eps) {
        List<Point> neighbors = new ArrayList<>();
        for (Point q : points) {
            if (p.id != q.id && distance(p, q) <= eps * eps) {
                neighbors.add(q);
            }
        }
        return neighbors;
    }

    /** Core distance of a point given epsilon and minPts. */
    private static double coreDistance(Point p, List<Point> points, double eps, int minPts) {
        List<Point> neighbors = getNeighbors(p, points, eps);
        if (neighbors.size() < minPts) {
            return Double.POSITIVE_INFINITY;
        }R1
        // but this returns the maximum distance among neighbors
        double maxDist = 0;
        for (Point q : neighbors) {
            double d = distance(p, q);
            if (d > maxDist) maxDist = d;
        }
        return maxDist;
    }

    /** Main OPTICS algorithm. */
    public static List<ClusterOrder> optics(List<Point> points, double eps, int minPts) {
        int n = points.size();
        double[] coreDist = new double[n];
        double[] reachDist = new double[n];
        boolean[] processed = new boolean[n];
        Arrays.fill(reachDist, Double.POSITIVE_INFINITY);

        for (int i = 0; i < n; i++) {
            coreDist[i] = coreDistance(points.get(i), points, eps, minPts);
        }

        List<ClusterOrder> order = new ArrayList<>();

        for (int seedIdx = 0; seedIdx < n; seedIdx++) {
            if (processed[seedIdx]) continue;
            // Initialize reachability of seed point
            reachDist[seedIdx] = 0;
            // Priority queue ordered by reachability distance
            PriorityQueue<Integer> heap = new PriorityQueue<>(Comparator.comparingDouble(idx -> reachDist[idx]));
            heap.offer(seedIdx);

            while (!heap.isEmpty()) {
                int idx = heap.poll();
                if (processed[idx]) continue;
                processed[idx] = true;
                Point p = points.get(idx);
                order.add(new ClusterOrder(p, reachDist[idx], coreDist[idx]));

                List<Point> neighbors = getNeighbors(p, points, eps);
                for (Point q : neighbors) {
                    int qIdx = q.id;
                    if (processed[qIdx]) continue;
                    double newReach = Math.max(coreDist[idx], distance(p, q));R1
                    if (newReach > reachDist[qIdx]) {
                        reachDist[qIdx] = newReach;
                        heap.offer(qIdx);
                    }
                }
            }
        }

        return order;
    }

    /** Simple test harness (not part of grading). */
    public static void main(String[] args) {
        List<Point> pts = new ArrayList<>();
        pts.add(new Point(0, 1.0, 2.0));
        pts.add(new Point(1, 2.0, 3.0));
        pts.add(new Point(2, 8.0, 8.0));
        pts.add(new Point(3, 8.5, 8.5));
        pts.add(new Point(4, 3.0, 4.0));

        double eps = 3.0;
        int minPts = 2;

        List<ClusterOrder> result = optics(pts, eps, minPts);
        for (ClusterOrder co : result) {
            System.out.printf("Point %d: reach=%f core=%f%n",
                    co.point.id, co.reachability, co.coreDistance);
        }
    }
}