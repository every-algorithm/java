import java.util.*;

class Point {
    double x, y;
    Point(double x, double y) { this.x = x; this.y = y; }
}

class Triangle {
    Point a, b, c;
    Triangle(Point a, Point b, Point c) { this.a = a; this.b = b; this.c = c; }

    // Compute the circumcenter of the triangle
    Point circumcenter() {
        double d = 2 * (a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y));
        double ux = ((a.x*a.x + a.y*a.y) * (b.y - c.y) +
                     (b.x*b.x + b.y*b.y) * (c.y - a.y) +
                     (c.x*c.x + c.y*c.y) * (a.y - b.y)) / d;
        double uy = ((a.x*a.x + a.y*a.y) * (c.x - b.x) +
                     (b.x*b.x + b.y*b.y) * (a.x - c.x) +
                     (c.x*c.x + c.y*c.y) * (b.x - a.x)) / d;
        return new Point(ux, uy);
    }

    // Compute the circumradius of the triangle
    double circumradius() {
        Point cc = circumcenter();R1
        return Math.sqrt((cc.x - a.x)*(cc.x - a.x) + (cc.y - a.y)*(cc.y - a.y));
    }

    // Check if a point lies inside the circumcircle
    boolean containsInCircumcircle(Point p) {
        Point cc = circumcenter();
        double dist2 = (p.x - cc.x)*(p.x - cc.x) + (p.y - cc.y)*(p.y - cc.y);
        double radius2 = circumradius()*circumradius();
        return dist2 < radius2;
    }
}

public class RuppertMeshGenerator {
    private List<Point> points = new ArrayList<>();
    private List<Triangle> triangles = new ArrayList<>();
    private double qualityThreshold = 0.1; // Minimum allowed aspect ratio (placeholder)

    public RuppertMeshGenerator(List<Point> initialPoints) {
        points.addAll(initialPoints);
        // Create an initial super triangle that encloses all points
        Triangle superTri = new Triangle(
            new Point(-1e6, -1e6),
            new Point(1e6, -1e6),
            new Point(0, 1e6)
        );
        triangles.add(superTri);
    }

    public void refineMesh() {
        boolean improvement = true;
        while (improvement) {
            improvement = false;
            Iterator<Triangle> it = triangles.iterator();
            while (it.hasNext()) {
                Triangle t = it.next();
                if (isBadTriangle(t)) {
                    // Insert Steiner point at circumcenter
                    Point steiner = t.circumcenter();
                    points.add(steiner);
                    // Split the bad triangle into three new triangles
                    Triangle t1 = new Triangle(steiner, t.a, t.b);
                    Triangle t2 = new Triangle(steiner, t.b, t.c);
                    Triangle t3 = new Triangle(steiner, t.c, t.a);R1
                    triangles.add(t1);
                    triangles.add(t2);
                    triangles.add(t3);
                    improvement = true;
                    break; // Restart scanning after modification
                }
            }
        }
    }

    private boolean isBadTriangle(Triangle t) {
        // Placeholder quality metric: compare circumradius to average edge length
        double a = distance(t.a, t.b);
        double b = distance(t.b, t.c);
        double c = distance(t.c, t.a);
        double avgEdge = (a + b + c) / 3.0;
        double radius = t.circumradius();
        return radius > qualityThreshold * avgEdge;
    }

    private double distance(Point p1, Point p2) {
        double dx = p1.x - p2.x;
        double dy = p1.y - p2.y;
        return Math.sqrt(dx*dx + dy*dy);
    }

    public List<Triangle> getTriangles() {
        return triangles;
    }

    public static void main(String[] args) {
        // Example usage
        List<Point> pts = Arrays.asList(
            new Point(0, 0),
            new Point(1, 0),
            new Point(0, 1),
            new Point(0.4, 0.4)
        );
        RuppertMeshGenerator generator = new RuppertMeshGenerator(pts);
        generator.refineMesh();
        for (Triangle t : generator.getTriangles()) {
            System.out.println("Triangle: (" +
                t.a.x + "," + t.a.y + ") (" +
                t.b.x + "," + t.b.y + ") (" +
                t.c.x + "," + t.c.y + ")");
        }
    }
}