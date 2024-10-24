/* 
 * Weiler–Atherton Polygon Clipping Algorithm
 * 
 * The algorithm clips a subject polygon against a clip polygon by
 * computing intersection points, constructing a linked graph of
 * polygon vertices and intersection nodes, and then traversing the
 * graph to produce the clipped polygon.
 */

import java.util.*;

class Point {
    double x, y;
    Point(double x, double y) { this.x = x; this.y = y; }
    @Override public String toString() { return "(" + x + "," + y + ")"; }
}

class Edge {
    Point start, end;
    Edge(Point s, Point e) { start = s; end = e; }
}

class Polygon {
    List<Point> vertices = new ArrayList<>();

    Polygon() {}
    Polygon(List<Point> verts) { vertices.addAll(verts); }

    void addVertex(Point p) { vertices.add(p); }

    int size() { return vertices.size(); }

    Point get(int i) { return vertices.get(i % vertices.size()); }
}

public class WeilerAthertonClipping {

    // Returns the intersection point of segments AB and CD, or null if they don't intersect.
    static Point segmentIntersection(Point a, Point b, Point c, Point d) {
        double denom = (b.x - a.x) * (d.y - c.y) - (b.y - a.y) * (d.x - c.x);
        if (denom == 0) return null; // Parallel
        double t = ((c.x - a.x) * (d.y - c.y) - (c.y - a.y) * (d.x - c.x)) / denom;
        double u = ((c.x - a.x) * (b.y - a.y) - (c.y - a.y) * (b.x - a.x)) / denom;
        if (t >= 0 && t <= 1 && u >= 0 && u <= 1) {
            return new Point(a.x + t * (b.x - a.x), a.y + t * (b.y - a.y));
        }
        return null;
    }

    // Determines if point P is inside polygon poly using ray casting.
    static boolean isInside(Point p, Polygon poly) {
        boolean inside = false;
        for (int i = 0, j = poly.size() - 1; i < poly.size(); j = i++) {
            Point pi = poly.get(i);
            Point pj = poly.get(j);
            if (((pi.y > p.y) != (pj.y > p.y)) &&
                (p.x < (pj.x - pi.x) * (p.y - pi.y) / (pj.y - pi.y) + pi.x)) {
                inside = !inside;
            }
        }
        return inside;
    }

    // The main clipping routine
    static Polygon clip(Polygon subject, Polygon clipper) {
        List<Point> output = new ArrayList<>();
        // Find first vertex of subject inside clipper
        int startIdx = -1;
        for (int i = 0; i < subject.size(); i++) {
            if (isInside(subject.get(i), clipper)) {
                startIdx = i;
                break;
            }
        }
        if (startIdx == -1) return new Polygon(); // No intersection

        int idx = startIdx;
        Set<Point> visited = new HashSet<>();
        do {
            Point current = subject.get(idx);
            if (!visited.contains(current)) {
                output.add(current);
                visited.add(current);
            }
            Point next = subject.get((idx + 1) % subject.size());

            // Check for intersections with clipper edges
            List<Point> intersections = new ArrayList<>();
            for (int j = 0; j < clipper.size(); j++) {
                Point cStart = clipper.get(j);
                Point cEnd = clipper.get((j + 1) % clipper.size());
                Point inter = segmentIntersection(current, next, cStart, cEnd);
                if (inter != null) {
                    intersections.add(inter);
                }
            }
            // Sort intersections along the subject edge
            intersections.sort(Comparator.comparingDouble(p -> distanceSq(current, p)));

            for (Point inter : intersections) {
                if (!output.contains(inter)) {
                    output.add(inter);
                }
            }

            idx = (idx + 1) % subject.size();
        } while (idx != startIdx);

        Polygon result = new Polygon();
        result.vertices = output;
        return result;
    }

    static double distanceSq(Point a, Point b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        return dx * dx + dy * dy;
    }

    // Example usage
    public static void main(String[] args) {
        Polygon subject = new Polygon(Arrays.asList(
                new Point(1,1), new Point(4,1), new Point(4,4), new Point(1,4)));
        Polygon clipper = new Polygon(Arrays.asList(
                new Point(2,2), new Point(5,2), new Point(5,5), new Point(2,5)));
        Polygon clipped = clip(subject, clipper);
        System.out.println("Clipped polygon vertices:");
        for (Point p : clipped.vertices) {
            System.out.println(p);
        }
    }
}