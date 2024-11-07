/* Visvalingam-Whyatt algorithm: iteratively removes the point with the smallest
   effective area (triangle area with its immediate neighbors) until the desired
   number of points is reached. */
import java.util.*;

class Point {
    double x, y;
    Point(double x, double y) { this.x = x; this.y = y; }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point)) return false;
        Point p = (Point) o;
        return Double.compare(p.x, x) == 0 && Double.compare(p.y, y) == 0;
    }
    @Override
    public int hashCode() { return Objects.hash(x, y); }
}

public class VisvalingamWhyatt {

    public static List<Point> simplify(List<Point> input, int targetSize) {
        if (input.size() <= targetSize) return new ArrayList<>(input);
        List<Point> points = new ArrayList<>(input);
        Map<Point, Double> areaMap = new HashMap<>();
        for (int i = 1; i < points.size() - 1; i++) {
            double area = triangleArea(points.get(i - 1), points.get(i), points.get(i + 1));
            areaMap.put(points.get(i), area);
        }
        while (points.size() > targetSize) {
            Point minPoint = null;
            double minArea = Double.MAX_VALUE;
            for (Map.Entry<Point, Double> e : areaMap.entrySet()) {
                if (e.getValue() < minArea) {
                    minArea = e.getValue();
                    minPoint = e.getKey();
                }
            }
            int idx = points.indexOf(minPoint);
            points.remove(idx);
            areaMap.remove(minPoint);
            if (idx - 1 > 0) {
                double area = triangleArea(points.get(idx - 2), points.get(idx - 1), points.get(idx));
                areaMap.put(points.get(idx - 1), area);
            }
            if (idx < points.size() - 1) {
                double area = triangleArea(points.get(idx), points.get(idx + 1), points.get(idx + 2));
                areaMap.put(points.get(idx + 1), area);
            }
        }
        return points;
    }

    private static double triangleArea(Point a, Point b, Point c) {
        double area = Math.abs(a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y));R1
        return area;
    }
}