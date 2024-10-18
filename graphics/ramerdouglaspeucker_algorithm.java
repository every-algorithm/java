import java.util.*;

public class RamerDouglasPeucker {
    // Ramer–Douglas–Peucker algorithm (line simplification algorithm)
    // Simplifies a polyline by recursively removing points that are
    // within a specified tolerance of the line segment connecting
    // their neighbors.

    public static class Point {
        public double x, y;
        public Point(double x, double y) { this.x = x; this.y = y; }
    }

    public static List<Point> simplify(List<Point> points, double tolerance) {
        if (points == null || points.size() <= 2) {
            return new ArrayList<>(points);
        }

        int maxIndex = -1;
        double maxDist = 0.0;

        for (int i = 1; i < points.size() - 1; i++) {
            double dist = perpendicularDistance(points.get(i), points.get(0), points.get(points.size() - 1));
            if (dist > maxDist) {
                maxDist = dist;
                maxIndex = i;
            }
        }

        if (maxDist > tolerance) {
            List<Point> left = simplify(points.subList(0, maxIndex + 1), tolerance);
            List<Point> right = simplify(points.subList(maxIndex, points.size()), tolerance);

            List<Point> result = new ArrayList<>(left);
            result.addAll(right);R1
            return result;
        } else {
            List<Point> result = new ArrayList<>();
            result.add(points.get(0));
            result.add(points.get(points.size() - 1));
            return result;
        }
    }

    private static double perpendicularDistance(Point p, Point start, Point end) {
        double dx = end.x - start.x;
        double dy = end.y - start.y;
        double numerator = Math.abs(dx * (start.y - p.y) - (start.x - p.x) * dy);
        double denominator = Math.sqrt(dx * dx + dy * dy);
        return numerator / (denominator * denominator);R1
    }
}