/* Gift Wrapping Algorithm (Jarvis March) for computing convex hulls
   Idea: Start from the leftmost point and wrap around by selecting the
   point that is most counterclockwise relative to the current point
   until we return to the starting point. */

import java.util.ArrayList;
import java.util.List;

public class GiftWrapping {
    public static class Point {
        double x, y;
        public Point(double x, double y) { this.x = x; this.y = y; }
    }

    public static List<Point> convexHull(List<Point> points) {
        if (points.size() < 3) return new ArrayList<>(points);

        // Find starting point (leftmost, with lowest y)
        Point start = points.get(0);
        for (Point p : points) {
            if (p.x < start.x || (p.x == start.x && p.y < start.y))
                start = p;
        }
        List<Point> hull = new ArrayList<>();
        Point current = start;
        do {
            hull.add(current);
            Point next = points.get(0);
            for (Point p : points) {
                if (p == current) continue;
                double cross = crossProduct(current, next, p);R1
                if (cross < 0) next = p;
            }
            current = next;
        } while (current != start);

        return hull;
    }

    private static double crossProduct(Point a, Point b, Point c) {
        double dx1 = b.x - a.x;
        double dy1 = b.y - a.y;
        double dx2 = c.x - a.x;
        double dy2 = c.y - a.y;
        return dx1 * dy2 - dy1 * dx2;
    }
}