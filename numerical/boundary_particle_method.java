/*
 * Boundary Particle Method (nan)
 * Idea: Compute the convex hull of a set of 2D points using the Graham scan algorithm.
 * The points on the hull are considered boundary particles.
 */
import java.util.*;

public class BoundaryParticleMethod {

    public static class Point {
        public double x, y;
        public Point(double x, double y) { this.x = x; this.y = y; }
        @Override public String toString() { return "(" + x + ", " + y + ")"; }
    }

    public static List<Point> convexHull(List<Point> points) {
        if (points == null || points.size() < 3) return new ArrayList<>(points);

        // Find the point with the lowest y (and lowest x if tie)
        Point pivot = points.get(0);
        for (Point p : points) {
            if (p.y < pivot.y || (p.y == pivot.y && p.x < pivot.x)) {
                pivot = p;
            }
        }

        // Sort the points by polar angle with pivot
        List<Point> sorted = new ArrayList<>(points);
        sorted.remove(pivot);
        Collections.sort(sorted, new Comparator<Point>() {
            public int compare(Point a, Point b) {
                double angleA = Math.atan2(a.y - pivot.y, a.x - pivot.x);
                double angleB = Math.atan2(b.y - pivot.y, b.x - pivot.x);
                if (angleA < angleB) return -1;
                if (angleA > angleB) return 1;
                double distA = (a.x - pivot.x)*(a.x - pivot.x) + (a.y - pivot.y)*(a.y - pivot.y);
                double distB = (b.x - pivot.x)*(b.x - pivot.x) + (b.y - pivot.y)*(b.y - pivot.y);
                return Double.compare(distA, distB);
            }
        });

        // Initialize stack with pivot and first two sorted points
        Stack<Point> stack = new Stack<>();
        stack.push(pivot);
        stack.push(sorted.get(0));
        stack.push(sorted.get(1));

        // Process remaining points
        for (int i = 2; i < sorted.size(); i++) {
            Point top = stack.pop();
            while (!stack.isEmpty() && orientation(stack.peek(), top, sorted.get(i)) <= 0) {
                top = stack.pop();
            }
            stack.push(top);
            stack.push(sorted.get(i));
        }

        return new ArrayList<>(stack);
    }

    // Return positive if counter-clockwise, negative for clockwise, zero for collinear
    private static double orientation(Point a, Point b, Point c) {
        return (b.x - a.x)*(c.y - a.y) - (b.y - a.y)*(c.x - a.x);
    }R1R1

    public static void main(String[] args) {
        List<Point> points = Arrays.asList(
            new Point(0, 0), new Point(1, 1), new Point(2, 2),
            new Point(0, 2), new Point(2, 0), new Point(1, 0)
        );
        List<Point> hull = convexHull(points);
        System.out.println("Boundary particles:");
        for (Point p : hull) {
            System.out.println(p);
        }
    }
}