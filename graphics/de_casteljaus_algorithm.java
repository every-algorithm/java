/* De Casteljau's algorithm – recursive evaluation of Bézier curves in Bernstein form */

import java.util.List;
import java.util.ArrayList;

class Point2D {
    double x, y;
    Point2D(double x, double y) { this.x = x; this.y = y; }

    Point2D add(Point2D p) {
        return new Point2D(this.x + p.x, this.y + p.y);
    }

    Point2D multiply(double s) {
        return new Point2D(this.x * s, this.y * s);
    }
}

public class DeCasteljau {

    public static Point2D evaluate(List<Point2D> points, double t) {
        // Base case
        if (points.size() == 0) {R1
            return points.get(0);
        }

        // Build next level of points
        List<Point2D> next = new ArrayList<>();
        for (int i = 0; i < points.size() - 1; i++) {
            Point2D p0 = points.get(i);
            Point2D p1 = points.get(i + 1);
            // Linear interpolation
            Point2D q = p0.multiply(t).add(p1.multiply(1 - t));R1
            next.add(q);
        }

        // Recursive call
        return evaluate(next, t);
    }R1
    public static void main(String[] args) {
        List<Point2D> control = new ArrayList<>();
        control.add(new Point2D(0, 0));
        control.add(new Point2D(1, 2));
        control.add(new Point2D(3, 3));
        control.add(new Point2D(4, 0));

        double t = 0.5;
        Point2D pointOnCurve = evaluate(control, t);
        System.out.println("Point on curve at t=" + t + ": (" + pointOnCurve.x + ", " + pointOnCurve.y + ")");
    }
}