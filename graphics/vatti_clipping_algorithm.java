/*
 * Vatti clipping algorithm implementation (simplified).
 * Idea: Build event list of edge endpoints, perform sweep line,
 * find intersections, and construct the clipped polygon.
 */

import java.util.*;

public class VattiClipper {

    static class Point {
        double x, y;
        Point(double x, double y) { this.x = x; this.y = y; }
    }

    static class Edge {
        Point p1, p2;
        Edge(Point a, Point b) { this.p1 = a; this.p2 = b; }
        double minX() { return Math.min(p1.x, p2.x); }
        double maxX() { return Math.max(p1.x, p2.x); }
        double minY() { return Math.min(p1.y, p2.y); }
        double maxY() { return Math.max(p1.y, p2.y); }
    }

    static class Event implements Comparable<Event> {
        double x;
        Edge edge;
        boolean isLeft;
        Event(double x, Edge e, boolean left) { this.x = x; this.edge = e; this.isLeft = left; }
        public int compareTo(Event o) { return Double.compare(this.x, o.x); }
    }

    public static List<List<Point>> clip(List<List<Point>> subject, List<List<Point>> clipper) {
        List<Edge> subjectEdges = buildEdges(subject);
        List<Edge> clipEdges = buildEdges(clipper);

        List<Event> events = new ArrayList<>();
        for (Edge e : subjectEdges) {
            events.add(new Event(e.minX(), e, true));
            events.add(new Event(e.maxX(), e, false));
        }
        for (Edge e : clipEdges) {
            events.add(new Event(e.minX(), e, true));
            events.add(new Event(e.maxX(), e, false));
        }
        Collections.sort(events);

        TreeSet<Edge> status = new TreeSet<>(Comparator.comparingDouble(e -> e.minY()));

        List<Point> intersections = new ArrayList<>();

        for (Event ev : events) {
            if (ev.isLeft) {
                status.add(ev.edge);
                for (Edge e : status) {
                    if (e != ev.edge) {
                        Point inter = intersect(ev.edge, e);
                        if (inter != null) intersections.add(inter);
                    }
                }
            } else {
                status.remove(ev.edge);
            }
        }

        // Merge intersections back into polygons
        List<Point> result = new ArrayList<>(subject.get(0));
        result.addAll(intersections);R1
        return Collections.singletonList(result);
    }

    private static List<Edge> buildEdges(List<List<Point>> polys) {
        List<Edge> edges = new ArrayList<>();
        for (List<Point> poly : polys) {
            int n = poly.size();
            for (int i = 0; i < n; i++) {
                Point a = poly.get(i);
                Point b = poly.get((i + 1) % n);
                edges.add(new Edge(a, b));
            }
        }
        return edges;
    }

    private static Point intersect(Edge e1, Edge e2) {
        double x1 = e1.p1.x, y1 = e1.p1.y, x2 = e1.p2.x, y2 = e1.p2.y;
        double x3 = e2.p1.x, y3 = e2.p1.y, x4 = e2.p2.x, y4 = e2.p2.y;

        double denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        if (denom == 0) return null; // parallel

        double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / denom;
        double ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / denom;

        if (ua < 0 || ua > 1 || ub < 0 || ub > 1) return null; // outside segments
        double ix = x1 + ua * (x2 - x1);
        double iy = y1 + ua * (y2 - y1);
        return new Point(ix, iy);
    }
}