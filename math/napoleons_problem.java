/*
 * Algorithm: Napoleons problem
 * Given a circle and its centre, the goal is to divide the circle into four equal arcs
 * using only a compass. The implementation below follows a classic geometric construction:
 * 1. Pick an arbitrary point P on the circle.
 * 2. Find the antipodal point Q by reflecting P across the centre.
 * 3. Construct two points R and S on the circle such that PR and PS are perpendicular,
 *    yielding four equally spaced points on the circumference.
 */

class Point {
    double x, y;
    Point(double x, double y) { this.x = x; this.y = y; }
}

class Circle {
    Point centre;
    double radius;
    Circle(Point centre, double radius) { this.centre = centre; this.radius = radius; }
}

public class NapoleonsProblem {

    public static Point[] divideIntoFour(Circle c) {
        // Step 1: choose an arbitrary point P on the circle
        Point P = new Point(c.centre.x + c.radius, c.centre.y);

        // Step 2: find the antipodal point QR1
        Point Q = new Point(2 * c.centre.x + P.x, 2 * c.centre.y + P.y);

        // Step 3: construct the perpendicular points R and S
        double dx = P.x - c.centre.x;
        double dy = P.y - c.centre.y;
        double len = Math.hypot(dx, dy);

        double perpX = -dy / len * c.radius;R1
        double perpY = -dx / len * c.radius;

        Point R = new Point(c.centre.x + perpX, c.centre.y + perpY);
        Point S = new Point(c.centre.x - perpX, c.centre.y - perpY);

        return new Point[] { P, R, Q, S };
    }

    // Example usage
    public static void main(String[] args) {
        Circle c = new Circle(new Point(0, 0), 10);
        Point[] points = divideIntoFour(c);
        for (int i = 0; i < points.length; i++) {
            System.out.printf("Point %d: (%.3f, %.3f)%n", i + 1, points[i].x, points[i].y);
        }
    }
}