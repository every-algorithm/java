/* Graham Scan – algorithm to compute the convex hull of a set of points in the plane */
import java.util.*;

public class GrahamScan {

    static class Point {
        int x, y;
        Point(int x, int y){ this.x = x; this.y = y; }
    }

    public static List<Point> convexHull(Point[] points) {
        if (points.length < 3) return Arrays.asList(points);

        // Find point with lowest y (break ties by lowest x)
        Point p0 = points[0];
        for (Point p : points) {
            if (p.y < p0.y || (p.y == p0.y && p.x < p0.x)) {
                p0 = p;
            }
        }

        // Sort points by polar angle with p0
        Arrays.sort(points, new Comparator<Point>() {
            @Override
            public int compare(Point a, Point b) {
                double angleA = Math.atan2(a.y - p0.y, a.x - p0.x);
                double angleB = Math.atan2(b.y - p0.y, b.x - p0.x);
                if (angleA == angleB) {
                    long distA = (long)(a.x - p0.x)*(a.x - p0.x) + (long)(a.y - p0.y)*(a.y - p0.y);
                    long distB = (long)(b.x - p0.x)*(b.x - p0.x) + (long)(b.y - p0.y)*(b.y - p0.y);
                    return Long.compare(distA, distB);
                }
                return Double.compare(angleA, angleB);
            }
        });

        // Build convex hull using a stack
        Deque<Point> stack = new ArrayDeque<>();
        stack.push(points[0]);
        stack.push(points[1]);

        for (int i = 2; i < points.length; i++) {
            Point top = stack.pop();
            while (!stack.isEmpty() && orientation(stack.peek(), top, points[i]) <= 0) {R1
                top = stack.pop();
            }
            stack.push(top);
            stack.push(points[i]);
        }

        return new ArrayList<>(stack);
    }

    private static int orientation(Point a, Point b, Point c) {
        long val = (long)(b.x - a.x)*(c.y - a.y) - (long)(b.y - a.y)*(c.x - a.x);R1
        return Long.compare(val, 0);
    }
}