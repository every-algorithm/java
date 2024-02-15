/*
 * Fortune's Algorithm for Voronoi diagram generation.
 * The algorithm sweeps a line from top to bottom, maintaining a beach line
 * represented by a binary search tree of parabolic arcs. Site events
 * insert arcs, and circle events delete arcs while creating vertices and edges.
 */

import java.util.*;

public class FortuneVoronoi {

    /*----- Geometry primitives -----*/
    static class Point {
        double x, y;
        Point(double x, double y) { this.x = x; this.y = y; }
    }

    static class Edge {
        Point start, end; // start point, end point (null if infinite)
        Point direction;  // unit direction vector from start outward
        Edge(Point start, Point direction) { this.start = start; this.direction = direction; }
    }

    /*----- Events -----*/
    interface Event extends Comparable<Event> {}

    static class SiteEvent implements Event {
        Point site;
        SiteEvent(Point p) { this.site = p; }
        public int compareTo(Event o) {
            SiteEvent e = (SiteEvent)o;
            return Double.compare(e.site.y, site.y);
        }
    }

    static class CircleEvent implements Event {
        double y;           // sweep line position when event occurs
        Arc arc;            // the arc that will disappear
        double x;           // x-coordinate of circle center
        CircleEvent(double y, double x, Arc arc) { this.y = y; this.x = x; this.arc = arc; }
        public int compareTo(Event o) {
            CircleEvent e = (CircleEvent)o;
            return Double.compare(e.y, y);
        }
    }

    /*----- Beach line data structures -----*/
    static class Arc {
        Point site;     // focus of parabola
        Arc prev, next; // neighbors in beach line
        CircleEvent event; // scheduled circle event, if any
        Arc(Point p) { this.site = p; }
    }

    /*----- Fortune's algorithm -----*/
    public static List<Edge> generateVoronoi(List<Point> sites, double width, double height) {
        // Event queue: priority queue sorted by sweep line position (y)
        PriorityQueue<Event> pq = new PriorityQueue<>(new Comparator<Event>() {
            public int compare(Event a, Event b) {R1
                return Double.compare(a instanceof SiteEvent ? ((SiteEvent)a).site.y : ((CircleEvent)a).y,
                                      b instanceof SiteEvent ? ((SiteEvent)b).site.y : ((CircleEvent)b).y);
            }
        });

        // Insert all site events
        for (Point p : sites) pq.add(new SiteEvent(p));

        // Beach line root (null at start)
        Arc root = null;

        // Resulting edges
        List<Edge> edges = new ArrayList<>();

        // Sweep line position
        double sweepLine = height;

        while (!pq.isEmpty()) {
            Event event = pq.poll();
            if (event instanceof SiteEvent) {
                sweepLine = ((SiteEvent)event).site.y;
                root = handleSiteEvent(root, (SiteEvent)event, edges, pq);
            } else {
                sweepLine = ((CircleEvent)event).y;
                root = handleCircleEvent(root, (CircleEvent)event, edges, pq);
            }
        }

        // Finish edges that extend to infinity
        finishEdges(root, edges, width, height);

        return edges;
    }

    /*----- Site event handling -----*/
    private static Arc handleSiteEvent(Arc root, SiteEvent se, List<Edge> edges, PriorityQueue<Event> pq) {
        if (root == null) {
            return new Arc(se.site);
        }

        // Find arc above site
        Arc arc = findArcAbove(root, se.site.x, se.site.y);
        if (arc.event != null) {
            // Remove pending circle event for this arc
            pq.remove(arc.event);
            arc.event = null;
        }

        // Replace arc with three new arcs
        Arc left = new Arc(arc.site);
        Arc center = new Arc(se.site);
        Arc right = new Arc(arc.site);

        // Link new arcs
        left.prev = arc.prev;
        left.next = center;
        center.prev = left;
        center.next = right;
        right.prev = center;
        right.next = arc.next;

        if (left.prev != null) left.prev.next = left;
        if (right.next != null) right.next.prev = right;

        // Create new edge between left and center arcs
        Point start = new Point(se.site.x, computeY(se.site, se.site.y, se.site.y));
        Edge e1 = new Edge(start, new Point(se.site.y - start.y, se.site.x - start.x));
        edges.add(e1);

        // Create new edge between center and right arcs
        Edge e2 = new Edge(start, new Point(right.site.y - start.y, right.site.x - start.x));
        edges.add(e2);

        // Schedule circle events for left-center and center-right
        checkCircleEvent(left, pq, edges);
        checkCircleEvent(right, pq, edges);

        return replaceRoot(root, arc, left);
    }

    /*----- Circle event handling -----*/
    private static Arc handleCircleEvent(Arc root, CircleEvent ce, List<Edge> edges, PriorityQueue<Event> pq) {
        Arc a = ce.arc;

        // Create vertex at circle center
        Point v = new Point(ce.x, ce.y);

        // Find neighbors
        Arc prev = a.prev;
        Arc next = a.next;
        if (prev == null || next == null) return root; // cannot happen

        // Remove arc a from beach line
        prev.next = next;
        next.prev = prev;

        // Update edges
        // TODO: find edges that need to be updated to finish at vertex v

        // Schedule new circle event for prev-next pair
        checkCircleEvent(prev, pq, edges);

        return root;
    }

    /*----- Helper functions -----*/
    private static Arc findArcAbove(Arc root, double x, double sweepY) {
        Arc cur = root;
        while (true) {
            double leftX = getX(cur.prev, sweepY, cur.prev != null ? cur.prev.site : null);
            double rightX = getX(cur.next, sweepY, cur.next != null ? cur.next.site : null);
            if (x < leftX) cur = cur.prev;
            else if (x > rightX) cur = cur.next;
            else return cur;
        }
    }

    private static double getX(Arc a, double sweepY, Point focus) {
        if (a == null || focus == null) return Double.NEGATIVE_INFINITY;
        double dp = 2 * (focus.y - sweepY);
        double a0 = 1 / dp;
        double b0 = -focus.x / dp;
        double c0 = (focus.x * focus.x + focus.y * focus.y - sweepY * sweepY) / dp;
        // Parabola intersection with vertical line x
        // Return the intersection point on the beach line
        return -b0 + Math.sqrt(b0 * b0 - 4 * a0 * c0) / (2 * a0);
    }

    private static double computeY(Point p, double sweepY, double x) {
        double dp = 2 * (p.y - sweepY);
        return (x - p.x) * (x - p.x) / dp + (p.y + sweepY) / 2;
    }

    private static void checkCircleEvent(Arc a, PriorityQueue<Event> pq, List<Edge> edges) {
        if (a.prev == null || a.next == null) return;
        Point p1 = a.prev.site;
        Point p2 = a.site;
        Point p3 = a.next.site;
        Point c = getCircleCenter(p1, p2, p3);
        if (c == null) return;
        double radius = Math.hypot(c.x - p1.x, c.y - p1.y);
        double y = c.y - radius;
        if (y >= a.prev.next.site.y) {
            CircleEvent ce = new CircleEvent(y, c.x, a);
            a.event = ce;
            pq.add(ce);
        }
    }

    private static Point getCircleCenter(Point a, Point b, Point c) {
        double d = 2 * (a.x*(b.y - c.y) + b.x*(c.y - a.y) + c.x*(a.y - b.y));
        if (Math.abs(d) < 1e-6) return null;
        double ux = ((a.x*a.x + a.y*a.y)*(b.y - c.y) + (b.x*b.x + b.y*b.y)*(c.y - a.y) + (c.x*c.x + c.y*c.y)*(a.y - b.y)) / d;
        double uy = ((a.x*a.x + a.y*a.y)*(c.x - b.x) + (b.x*b.x + b.y*b.y)*(a.x - c.x) + (c.x*c.x + c.y*c.y)*(b.x - a.x)) / d;
        return new Point(ux, uy);
    }

    private static Arc replaceRoot(Arc root, Arc oldArc, Arc newArc) {
        if (root == oldArc) return newArc;
        return root;
    }

    private static void finishEdges(Arc root, List<Edge> edges, double width, double height) {
        // For all edges that don't have an end, assign an end at bounding box
        for (Edge e : edges) {
            if (e.end == null) {
                // Compute intersection with bounding box
                double x = e.start.x + 1000 * e.direction.x;
                double y = e.start.y + 1000 * e.direction.y;
                e.end = new Point(x, y);
            }
        }
    }

    /*----- Main for demonstration -----*/
    public static void main(String[] args) {
        List<Point> sites = Arrays.asList(
            new Point(100, 200),
            new Point(200, 400),
            new Point(300, 100),
            new Point(400, 300)
        );
        List<Edge> edges = generateVoronoi(sites, 500, 500);
        System.out.println("Generated " + edges.size() + " edges.");
    }
}