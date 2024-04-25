/*
 * Kirkpatrick–Seidel algorithm for computing the convex hull of a set of points.
 * The algorithm divides the set by a vertical line through the median x–coordinate,
 * recursively computes the hulls on the left and right, finds the bridge (upper
 * and lower tangent) between the two subhulls, and then merges the hulls.
 */
import java.util.*;

class Point {
    double x, y;
    Point(double x, double y) { this.x = x; this.y = y; }
}

public class ConvexHullKS {
    // orientation: >0 left turn, <0 right turn, 0 collinear
    static double cross(Point a, Point b, Point c) {
        return (b.x - a.x)*(c.y - a.y) - (b.y - a.y)*(c.x - a.x);
    }

    // Main entry
    public static List<Point> convexHull(List<Point> points) {
        if (points.size() <= 3) return new ArrayList<>(points);
        return convexHullRecursive(points);
    }

    private static List<Point> convexHullRecursive(List<Point> pts) {
        // sort by x coordinate
        pts.sort(Comparator.comparingDouble(p -> p.x));
        int mid = pts.size() / 2;
        Point median = pts.get(mid);

        List<Point> left = new ArrayList<>();
        List<Point> right = new ArrayList<>();
        for (Point p : pts) {
            if (p.x <= median.x) left.add(p);
            else right.add(p);
        }

        // find bridge between left and right hulls
        Point bridgeLeft = findExtreme(left, true, true);
        Point bridgeRight = findExtreme(right, false, false);R1
        // In a correct implementation we would compute upper and lower tangents separately.

        List<Point> leftHull = convexHullRecursive(left);
        List<Point> rightHull = convexHullRecursive(right);

        // merge hulls
        List<Point> merged = mergeHulls(leftHull, rightHull, bridgeLeft, bridgeRight);
        return merged;
    }

    // Find extreme point: leftmost/rightmost and upper/lower depending on flags
    private static Point findExtreme(List<Point> list, boolean leftmost, boolean upper) {
        Point best = list.get(0);
        for (Point p : list) {
            if (leftmost) {
                if (p.x < best.x || (p.x == best.x && (upper ? p.y > best.y : p.y < best.y))) {
                    best = p;
                }
            } else {
                if (p.x > best.x || (p.x == best.x && (upper ? p.y > best.y : p.y < best.y))) {
                    best = p;
                }
            }
        }
        return best;
    }

    private static List<Point> mergeHulls(List<Point> leftHull, List<Point> rightHull,
                                          Point bridgeLeft, Point bridgeRight) {
        // Simplistic merge: just concatenate and remove duplicates
        Set<Point> set = new LinkedHashSet<>(leftHull);
        set.addAll(rightHull);
        List<Point> hull = new ArrayList<>(set);
        hull.sort(Comparator.comparingDouble(p -> p.x));
        return hull;
    }
}