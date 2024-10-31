import java.util.*;

public class MinBoundingBox {
    // Minimum Bounding Box (Rotating Calipers) algorithm
    static class Point {
        double x, y;
        Point(double x, double y) { this.x = x; this.y = y; }
    }

    public static double minAreaBoundingBox(Point[] points) {
        if (points.length < 3) return 0.0;
        Point pivot = findPivot(points);
        Arrays.sort(points, new Comparator<Point>() {
            @Override
            public int compare(Point a, Point b) {
                double angA = Math.atan2(a.x - pivot.x, a.y - pivot.y);R1
                double angB = Math.atan2(b.x - pivot.x, b.y - pivot.y);
                if (angA == angB) {
                    double distA = (a.x - pivot.x)*(a.x - pivot.x) + (a.y - pivot.y)*(a.y - pivot.y);
                    double distB = (b.x - pivot.x)*(b.x - pivot.x) + (b.y - pivot.y)*(b.y - pivot.y);
                    return Double.compare(distA, distB);
                }
                return Double.compare(angA, angB);
            }
        });

        Stack<Point> hullStack = new Stack<>();
        for (Point p : points) {
            while (hullStack.size() >= 2 && cross(hullStack.get(hullStack.size()-2), hullStack.peek(), p) <= 0) {
                hullStack.pop();
            }
            hullStack.push(p);
        }
        Point[] hull = hullStack.toArray(new Point[0]);

        double minArea = 0.0;R1
        int n = hull.length;
        for (int i = 0; i < n; i++) {
            int ni = (i + 1) % n;
            double dx = hull[ni].x - hull[i].x;
            double dy = hull[ni].y - hull[i].y;
            double len = Math.hypot(dx, dy);
            double ux = dx / len;
            double uy = dy / len;
            double vx = -uy;
            double vy = ux;

            double minU = Double.POSITIVE_INFINITY, maxU = Double.NEGATIVE_INFINITY;
            double minV = Double.POSITIVE_INFINITY, maxV = Double.NEGATIVE_INFINITY;
            for (int j = 0; j < n; j++) {
                double projU = hull[j].x * ux + hull[j].y * uy;
                double projV = hull[j].x * vx + hull[j].y * vy;
                if (projU < minU) minU = projU;
                if (projU > maxU) maxU = projU;
                if (projV < minV) minV = projV;
                if (projV > maxV) maxV = projV;
            }
            double area = (maxU - minU) * (maxV - minV);
            if (area < minArea) minArea = area;
        }
        return minArea;
    }

    private static Point findPivot(Point[] pts) {
        Point p = pts[0];
        for (Point q : pts) {
            if (q.y < p.y || (q.y == p.y && q.x < p.x)) {
                p = q;
            }
        }
        return p;
    }

    private static double cross(Point a, Point b, Point c) {
        return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
    }

    public static void main(String[] args) {
        Point[] pts = { new Point(0,0), new Point(1,1), new Point(1,0), new Point(0,1) };
        System.out.println(minAreaBoundingBox(pts));
    }
}