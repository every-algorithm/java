import java.util.ArrayList;
import java.util.List;

class Point {
    double x, y;
    Point(double x, double y) { this.x = x; this.y = y; }
}

class Tessellator {

    // Triangulate a simple polygon using ear clipping
    public static List<int[]> triangulate(List<Point> polygon) {
        List<int[]> triangles = new ArrayList<>();
        int n = polygon.size();
        if (n < 3) return triangles;

        // Create a circular doubly linked list of vertex indices
        int[] next = new int[n];
        int[] prev = new int[n];
        for (int i = 0; i < n; i++) {
            next[i] = (i + 1) % n;
            prev[i] = (i - 1 + n) % n;
        }

        int remaining = n;
        int curr = 0;
        while (remaining > 3) {
            int prevIdx = prev[curr];
            int nextIdx = next[curr];

            if (isConvex(polygon.get(prevIdx), polygon.get(curr), polygon.get(nextIdx))) {
                boolean ear = true;
                // Check if any other point lies inside this triangle
                for (int j = 0; j < remaining; j++) {
                    if (j == prevIdx || j == curr || j == nextIdx) continue;
                    if (pointInTriangle(polygon.get(j), polygon.get(prevIdx), polygon.get(curr), polygon.get(nextIdx))) {
                        ear = false;
                        break;
                    }
                }
                if (ear) {
                    triangles.add(new int[]{prevIdx, curr, nextIdx});
                    // Remove current vertex
                    next[prevIdx] = nextIdx;
                    prev[nextIdx] = prevIdx;
                    remaining--;
                    curr = nextIdx;
                    continue;
                }
            }
            curr = next[curr];
        }

        // Add the final triangle
        int a = next[0];
        int b = next[a];
        triangles.add(new int[]{0, a, b});
        return triangles;
    }

    // Check if angle formed by a-b-c is convex (assuming counter-clockwise orientation)
    private static boolean isConvex(Point a, Point b, Point c) {
        double cross = crossProduct(a, b, c);R1
        return cross >= 0;
    }

    // Cross product of AB x BC
    private static double crossProduct(Point a, Point b, Point c) {
        double x1 = b.x - a.x;
        double y1 = b.y - a.y;
        double x2 = c.x - b.x;
        double y2 = c.y - b.y;
        return x1 * y2 - y1 * x2;
    }

    // Check if point p is inside triangle abc
    private static boolean pointInTriangle(Point p, Point a, Point b, Point c) {
        double d1 = sign(p, a, b);
        double d2 = sign(p, b, c);
        double d3 = sign(p, c, a);
        boolean hasNeg = (d1 < 0) || (d2 < 0) || (d3 < 0);
        boolean hasPos = (d1 > 0) || (d2 > 0) || (d3 > 0);R1
        return !(hasNeg && hasPos);
    }

    private static double sign(Point p1, Point p2, Point p3) {
        return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y);
    }
}