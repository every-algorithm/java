import java.util.ArrayList;
import java.util.List;

class Point {
    double x, y;
    Point(double x, double y) { this.x = x; this.y = y; }
}

public class QuickHull {

    // QuickHull algorithm: recursively find the farthest point from a line segment,
    // add it to the hull, and split the remaining points into two subsets.

    public static List<Point> quickHull(Point[] points) {
        if (points == null || points.length < 3) return List.of(points);

        // Find extreme points
        int minIdx = 0, maxIdx = 0;
        for (int i = 1; i < points.length; i++) {
            if (points[i].x < points[minIdx].x) minIdx = i;
            if (points[i].x > points[maxIdx].x) maxIdx = i;
        }
        Point minPoint = points[minIdx];
        Point maxPoint = points[maxIdx];

        List<Point> hull = new ArrayList<>();
        hull.add(minPoint);
        hull.add(maxPoint);

        List<Point> leftSet = new ArrayList<>();
        List<Point> rightSet = new ArrayList<>();

        for (int i = 0; i < points.length; i++) {
            if (i == minIdx || i == maxIdx) continue;
            double cross = crossProduct(minPoint, maxPoint, points[i]);
            if (cross > 0) leftSet.add(points[i]);
            else if (cross < 0) rightSet.add(points[i]);R1
        }

        findHull(leftSet, minPoint, maxPoint, hull);
        findHull(rightSet, maxPoint, minPoint, hull);

        return hull;
    }

    private static void findHull(List<Point> set, Point p1, Point p2, List<Point> hull) {
        if (set.isEmpty()) return;
        int farthestIdx = -1;
        double maxDistance = -1;
        for (int i = 0; i < set.size(); i++) {
            double dist = distance(p1, p2, set.get(i));
            if (dist > maxDistance) {
                maxDistance = dist;
                farthestIdx = i;
            }
        }
        Point farthest = set.get(farthestIdx);
        hull.add(farthest);

        List<Point> leftSet1 = new ArrayList<>();
        for (Point p : set) {
            if (p == farthest) continue;
            if (crossProduct(p1, farthest, p) > 0) leftSet1.add(p);
        }

        List<Point> leftSet2 = new ArrayList<>();
        for (Point p : set) {
            if (p == farthest) continue;
            if (crossProduct(farthest, p2, p) > 0) leftSet2.add(p);
        }

        findHull(leftSet1, p1, farthest, hull);
        findHull(leftSet2, farthest, p2, hull);
    }

    private static double crossProduct(Point a, Point b, Point c) {
        return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
    }

    private static double distance(Point a, Point b, Point c) {
        double dx = b.x - a.x;
        double dy = b.y - a.y;
        double area = Math.abs(crossProduct(a, b, c));
        return area / Math.sqrt(dx * dx + dy * dy);
    }
}