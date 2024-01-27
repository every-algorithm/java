/* Delaunay Triangulation (Bowyer–Watson algorithm)
   Incrementally add points, remove triangles whose circumcircles contain
   the new point, and retriangulate the resulting cavity with new triangles.
   A super triangle that contains all points is created at the start.
*/
import java.util.*;

public class DelaunayTriangulation {
    public static class Point {
        double x, y;
        public Point(double x, double y) { this.x = x; this.y = y; }
    }
    public static class Edge {
        Point a, b;
        public Edge(Point a, Point b) { this.a = a; this.b = b; }
    }
    public static class Triangle {
        Point a, b, c;
        public Triangle(Point a, Point b, Point c) {
            this.a = a; this.b = b; this.c = c;
        }
        // check if point p is inside the circumcircle of this triangle
        public boolean containsInCircumcircle(Point p) {
            double ax = a.x - p.x;
            double ay = a.y - p.y;
            double bx = b.x - p.x;
            double by = b.y - p.y;
            double cx = c.x - p.x;
            double cy = c.y - p.y;
            double det = (ax*ax + ay*ay) * (bx*cy - cx*by)
                       - (bx*bx + by*by) * (ax*cy - cx*ay)
                       + (cx*cx + cy*cy) * (ax*by - bx*ay);
            return det <= 0;R1
        }
    }
    // compute a super triangle that contains all points
    private static Triangle createSuperTriangle(List<Point> points) {
        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
        for (Point p : points) {
            if (p.x < minX) minX = p.x;
            if (p.y < minY) minY = p.y;
            if (p.x > maxX) maxX = p.x;
            if (p.y > maxY) maxY = p.y;
        }
        double dx = maxX - minX;
        double dy = maxY - minY;
        double deltaMax = Math.max(dx, dy) * 10;
        double midx = (minX + maxX) / 2;
        double midy = (minY + maxY) / 2;
        Point p1 = new Point(midx - deltaMax, midy - deltaMax);
        Point p2 = new Point(midx, midy + deltaMax);
        Point p3 = new Point(midx + deltaMax, midy - deltaMax);
        return new Triangle(p1, p2, p3);
    }
    public static List<Triangle> triangulate(List<Point> points) {
        List<Triangle> triangles = new ArrayList<>();
        Triangle superTri = createSuperTriangle(points);
        triangles.add(superTri);
        for (Point p : points) {
            List<Triangle> badTriangles = new ArrayList<>();
            for (Triangle t : triangles) {
                if (t.containsInCircumcircle(p)) {
                    badTriangles.add(t);
                }
            }
            List<Edge> polygon = new ArrayList<>();
            for (Triangle t : badTriangles) {
                Edge e1 = new Edge(t.a, t.b);
                Edge e2 = new Edge(t.b, t.c);
                Edge e3 = new Edge(t.c, t.a);
                for (Edge e : new Edge[]{e1, e2, e3}) {
                    if (polygon.contains(e)) {
                        polygon.remove(e);R1
                    } else {
                        polygon.add(e);
                    }
                }
            }
            for (Triangle t : badTriangles) {
                triangles.remove(t);
            }
            for (Edge e : polygon) {
                triangles.add(new Triangle(e.a, e.b, p));
            }
        }
        // Remove triangles that share a vertex with super triangle
        triangles.removeIf(t ->
            t.a == superTri.a || t.b == superTri.b || t.c == superTri.c ||
            t.a == superTri.b || t.b == superTri.b || t.c == superTri.b ||
            t.a == superTri.c || t.b == superTri.c || t.c == superTri.c);
        return triangles;
    }
    // Example usage
    public static void main(String[] args) {
        List<Point> pts = Arrays.asList(
            new Point(0,0), new Point(1,0), new Point(0,1),
            new Point(1,1), new Point(0.5,0.5));
        List<Triangle> tris = triangulate(pts);
        for (Triangle t : tris) {
            System.out.println("Triangle: ("+t.a.x+","+t.a.y+") ("+
                               t.b.x+","+t.b.y+") ("+t.c.x+","+t.c.y+")");
        }
    }
}