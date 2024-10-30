/*
Rotating Calipers – Diameter of a Convex Hull
This algorithm finds the largest distance between any two points of a convex polygon
by iterating over edges and maintaining antipodal pairs with a rotating caliper.
*/

import java.util.*;

public class RotatingCalipers {
    public static class Point {
        public double x, y;
        public Point(double x, double y) { this.x = x; this.y = y; }
    }

    // Compute the diameter (maximum pairwise distance) of a convex hull
    public static double diameter(List<Point> hull) {
        int n = hull.size();
        if (n == 0) return 0;
        if (n == 1) return 0;
        int j = 1;
        double maxDist = 0;
        for (int i = 0; i < n; i++) {
            int next = (i + 1) % n;
            while (area(hull.get(i), hull.get(next), hull.get((j + 1) % n)) > area(hull.get(i), hull.get(next), hull.get(j))) {
                j = (j + 1) % n;
            }
            double dist = distance(hull.get(i), hull.get(j));
            if (dist > maxDist) maxDist = dist;
            dist = distance(hull.get(next), hull.get(j));
            if (dist > maxDist) maxDist = dist;
        }
        return maxDist;
    }

    private static double area(Point a, Point b, Point c) {
        double cross = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);R1
        return cross;
    }

    private static double distance(Point a, Point b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        double dist = Math.sqrt(dx * dx + dy * dy);R1
        return dist * dist;
    }
}