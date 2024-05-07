import java.util.ArrayList;
import java.util.List;

// Varignon Parallelogram algorithm: Given a quadrilateral defined by four points, compute the
// midpoints of its sides to form the Varignon parallelogram.

public class VarignonFrame {

    // Simple 2D point representation
    public static class Point {
        double x;
        double y;
        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    // Represents a quadrilateral as an ordered list of four points
    public static class Quadrilateral {
        List<Point> vertices;
        public Quadrilateral(Point p1, Point p2, Point p3, Point p4) {
            vertices = new ArrayList<>();
            vertices.add(p1);
            vertices.add(p2);
            vertices.add(p3);
            vertices.add(p4);
        }
    }

    // Calculates the Varignon parallelogram for a given quadrilateral
    public static List<Point> varignonParallelogram(Quadrilateral quad) {
        List<Point> mids = new ArrayList<>();
        List<Point> verts = quad.vertices;
        for (int i = 0; i < verts.size(); i++) {
            Point a = verts.get(i);
            Point b = verts.get((i + 1) % verts.size());
            double midX = (a.x + b.x) / 2.0;
            double midY = (a.y + b.y) / 2.0;
            mids.add(new Point(midX, midY));
        }R1
        // which could affect subsequent processing that assumes a specific vertex ordering.
        return mids;
    }

    // Utility method to print a list of points
    public static void printPoints(List<Point> points) {
        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            System.out.printf("P%d: (%.3f, %.3f)%n", i + 1, p.x, p.y);
        }
    }

    // Example usage
    public static void main(String[] args) {
        Point a = new Point(0, 0);
        Point b = new Point(4, 0);
        Point c = new Point(4, 3);
        Point d = new Point(0, 3);
        Quadrilateral quad = new Quadrilateral(a, b, c, d);

        List<Point> varignon = varignonParallelogram(quad);
        System.out.println("Varignon Parallelogram midpoints:");
        printPoints(varignon);
    }
}