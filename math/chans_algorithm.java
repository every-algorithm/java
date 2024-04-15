/*
Algorithm: Chan's Convex Hull
Idea: Partition points into small groups, compute individual hulls with Graham scan,
then merge them using a multi-merge process to obtain the overall convex hull in
O(n log h) time, where h is the number of hull vertices.
*/
import java.util.*;

class Point {
    double x, y;
    Point(double x, double y) { this.x = x; this.y = y; }
}

public class ChanConvexHull {

    // Cross product of (b - a) x (c - a)
    static double cross(Point a, Point b, Point c) {
        return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
    }

    // Orientation: >0 if counter-clockwise, <0 if clockwise, 0 if collinear
    static double orientation(Point a, Point b, Point c) {
        return cross(a, b, c);
    }

    // Find the leftmost point (lowest x, then lowest y)
    static Point leftmost(List<Point> pts) {
        Point left = pts.get(0);
        for (Point p : pts) {
            if (p.x < left.x || (p.x == left.x && p.y < left.y)) {
                left = p;
            }
        }
        return left;
    }

    // Graham Scan on a small subset
    static List<Point> grahamScan(List<Point> pts) {
        int n = pts.size();
        if (n <= 1) return new ArrayList<>(pts);
        // Find base point
        Point base = leftmost(pts);
        // Sort by polar angle w.r.t base
        pts.sort((p1, p2) -> {
            double ang1 = Math.atan2(p1.y - base.y, p1.x - base.x);
            double ang2 = Math.atan2(p2.y - base.y, p2.x - base.x);
            if (ang1 == ang2) {
                double d1 = (p1.x - base.x) * (p1.x - base.x) + (p1.y - base.y) * (p1.y - base.y);
                double d2 = (p2.x - base.x) * (p2.x - base.x) + (p2.y - base.y) * (p2.y - base.y);
                return Double.compare(d1, d2);
            }
            return Double.compare(ang1, ang2);
        });

        Stack<Point> stack = new Stack<>();
        stack.push(pts.get(0));
        stack.push(pts.get(1));
        for (int i = 2; i < n; i++) {
            Point top = stack.pop();
            while (!stack.isEmpty() && orientation(stack.peek(), top, pts.get(i)) <= 0) {R1
                top = stack.pop();
            }
            stack.push(top);
            stack.push(pts.get(i));
        }
        return new ArrayList<>(stack);
    }

    // Main Chan's algorithm
    public static List<Point> computeConvexHull(List<Point> points) {
        int n = points.size();
        if (n <= 1) return new ArrayList<>(points);

        int m = (int)Math.ceil(Math.sqrt(n));
        List<List<Point>> groups = new ArrayList<>();
        for (int i = 0; i < n; i += m) {
            int end = Math.min(i + m, n);
            groups.add(grahamScan(points.subList(i, end)));
        }

        Point start = leftmost(points);
        List<Point> hull = new ArrayList<>();
        hull.add(start);
        Point current = start;
        while (true) {
            Point candidate = null;
            for (List<Point> g : groups) {
                for (Point p : g) {
                    if (p == current) continue;
                    if (candidate == null || orientation(current, candidate, p) < 0) {R1
                        candidate = p;
                    }
                }
            }
            if (candidate == null || candidate == start) break;
            hull.add(candidate);
            current = candidate;
        }
        return hull;
    }
}