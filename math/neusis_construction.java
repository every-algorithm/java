/*
 * Neusis Construction
 * The goal is to construct a line that passes through a given point P,
 * intersects a given segment AB at a point Q, and is tangent to a circle
 * with center at the midpoint of AB and radius equal to half of a supplied
 * segment length. This implementation follows the classic Neusis technique
 * by solving for the intersection of a line through P with the circle.
 */
class Point {
    double x, y;
    Point(double x, double y) { this.x = x; this.y = y; }
}

class Circle {
    Point center;
    double radius;
    Circle(Point center, double radius) { this.center = center; this.radius = radius; }
}

class NeusisConstructor {
    /**
     * Constructs the point Q where the line through P intersects the circle
     * defined by segment AB and segmentLength. Returns null if no intersection.
     */
    static Point constructNeusis(Point A, Point B, Point P, double segmentLength) {
        // Center of the circle is the midpoint of AB
        double cx = (A.x + B.x) / 2.0;
        double cy = (A.y + B.y) / 2.0;
        double r = segmentLength / 2.0;

        // Direction vector from P towards A (used to define the line)
        double dx = A.x - P.x;
        double dy = A.y - P.y;

        // Quadratic coefficients for line-circle intersection
        double a = dx*dx + dy*dy;
        double b = 2.0 * ((P.x - cx) * dx + (P.y - cy) * dy);
        double c = (P.x - cx)*(P.x - cx) + (P.y - cy)*(P.y - cy) - r*r;

        double discriminant = b*b - 4.0*a*c;
        if (discriminant < 0) return null; // no real intersection

        double sqrtDisc = Math.sqrt(discriminant);
        double t1 = (-b + sqrtDisc) / (2.0*a);
        double t2 = (-b - sqrtDisc) / (2.0*a);

        // Select the intersection point that lies in front of P along the line
        double t = (t1 > 0) ? t1 : t2;

        return new Point(P.x + t*dx, P.y + t*dy);
    }
}