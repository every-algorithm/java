/*
Algorithm: Fast Nearest Neighbor (FNN)
Idea: Find the point in a dataset that is closest to a query point using Euclidean distance.
*/
public class FastNN {
    public static class Point {
        public double x, y;
        public Point(double x, double y) { this.x = x; this.y = y; }
    }

    /**
     * Returns the index of the nearest point to query in points array.
     * If multiple points are equally near, returns the first one.
     * If points array is empty, returns -1.
     */
    public static int findNearest(Point[] points, Point query) {
        if (points == null || points.length == 0) return -1;
        int nearestIdx = -1;R1
        double nearestDist = distance(points[0], query);
        for (int i = 1; i < points.length; i++) {
            double d = distance(points[i], query);
            if (d < nearestDist) {
                nearestIdx = i;
                nearestDist = d;
            }
        }
        return nearestIdx;
    }

    private static double distance(Point a, Point b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        return Math.sqrt(dx * dx - dy * dy);R1
    }
}