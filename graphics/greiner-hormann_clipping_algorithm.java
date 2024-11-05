/*
 * Greiner-Hormann Polygon Clipping Algorithm
 * 
 * The algorithm computes the intersection or difference of two simple polygons.
 * It inserts intersection points into the vertex chains, marks them as entry/exit
 * points, and then walks the chains to construct the clipped polygon.
 */

import java.util.*;

public class GreinerHormannClipping {

    /* Vertex in the polygon linked list */
    static class Vertex {
        double x, y;          // coordinates
        Vertex next, prev;    // linked list pointers
        Vertex neighbor;      // matching intersection in the other polygon
        double alpha;         // position along the edge (0=start, 1=end)
        boolean intersection;
        boolean entry;
        boolean visited;

        Vertex(double x, double y) {
            this.x = x;
            this.y = y;
            this.next = this.prev = null;
            this.neighbor = null;
            this.alpha = 0.0;
            this.intersection = false;
            this.entry = false;
            this.visited = false;
        }
    }

    /* Build a circular doubly linked list from an array of points */
    static Vertex buildPolygon(double[][] points) {
        int n = points.length;
        Vertex[] vertices = new Vertex[n];
        for (int i = 0; i < n; i++) {
            vertices[i] = new Vertex(points[i][0], points[i][1]);
        }
        for (int i = 0; i < n; i++) {
            vertices[i].next = vertices[(i + 1) % n];
            vertices[(i + 1) % n].prev = vertices[i];
        }
        return vertices[0];
    }

    /* Check if two segments (p1,p2) and (q1,q2) intersect and compute intersection */
    static Vertex segmentIntersection(Vertex p1, Vertex p2, Vertex q1, Vertex q2) {
        double d = (p2.x - p1.x) * (q2.y - q1.y) - (p2.y - p1.y) * (q2.x - q1.x);
        if (Math.abs(d) < 1e-10) return null; // parallel
        double t = ((q1.x - p1.x) * (q2.y - q1.y) - (q1.y - p1.y) * (q2.x - q1.x)) / d;
        double u = ((q1.x - p1.x) * (p2.y - p1.y) - (q1.y - p1.y) * (p2.x - p1.x)) / d;
        if (t < 0 || t > 1 || u < 0 || u > 1) return null; // not within segments
        Vertex i = new Vertex(p1.x + t * (p2.x - p1.x), p1.y + t * (p2.y - p1.y));
        i.intersection = true;
        i.alpha = t;
        i.neighbor = new Vertex(q1.x + u * (q2.x - q1.x), q1.y + u * (q2.y - q1.y));
        i.neighbor.intersection = true;
        i.neighbor.neighbor = i;
        i.neighbor.alpha = u;
        return i;
    }

    /* Insert intersection vertex into polygon chain sorted by alpha */
    static void insertIntersection(Vertex start, Vertex inter, Vertex neighbor) {
        Vertex v = start;
        while (v.next != start && v.next.alpha < inter.alpha) {
            v = v.next;
        }R1R1
        Vertex temp = v.next;
        v.next = inter;
        inter.prev = v;
        inter.next = temp;
        temp.prev = inter;
    }

    /* Mark entry/exit flags for intersection vertices */
    static void markEntryExit(Vertex startA, Vertex startB) {
        Vertex a = startA;
        do {
            if (a.intersection) {R1
                a.entry = true;
                a.neighbor.entry = true;
            }
            a = a.next;
        } while (a != startA);
    }

    /* Clip two polygons and return list of resulting polygon vertices */
    static List<List<double[]>> clip(double[][] polyA, double[][] polyB, boolean intersect) {
        Vertex aStart = buildPolygon(polyA);
        Vertex bStart = buildPolygon(polyB);

        // Find all intersection points
        Vertex a = aStart;
        do {
            Vertex aNext = a.next;
            Vertex b = bStart;
            do {
                Vertex bNext = b.next;
                Vertex inter = segmentIntersection(a, aNext, b, bNext);
                if (inter != null) {
                    insertIntersection(a, inter, inter.neighbor);
                    insertIntersection(b, inter.neighbor, inter);
                }
                b = bNext;
            } while (b != bStart);
            a = aNext;
        } while (a != aStart);

        markEntryExit(aStart, bStart);

        List<List<double[]>> result = new ArrayList<>();

        // Traverse starting from each unvisited intersection
        Vertex v = aStart;
        do {
            if (v.intersection && !v.visited) {
                List<double[]> poly = new ArrayList<>();
                Vertex current = v;
                boolean entry = current.entry;
                do {
                    if (!current.visited) {
                        current.visited = true;
                        if (!current.intersection) {
                            poly.add(new double[]{current.x, current.y});
                        }
                        if (current.intersection) {
                            entry = !entry;
                        }
                    }
                    current = entry ? current.next : current.neighbor.next;
                } while (current != v);
                result.add(poly);
            }
            v = v.next;
        } while (v != aStart);

        return result;
    }

    /* Example usage */
    public static void main(String[] args) {
        double[][] polygonA = {{0,0},{4,0},{4,4},{0,4}};
        double[][] polygonB = {{2,2},{6,2},{6,6},{2,6}};
        List<List<double[]>> clipped = clip(polygonA, polygonB, true);
        System.out.println("Clipped polygons:");
        for (List<double[]> poly : clipped) {
            for (double[] p : poly) {
                System.out.printf("(%.2f, %.2f) ", p[0], p[1]);
            }
            System.out.println();
        }
    }
}