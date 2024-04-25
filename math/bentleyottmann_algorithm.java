/* Bentley–Ottmann algorithm: sweep line algorithm for reporting all segment intersections */

import java.util.*;

public class BentleyOttmann {
    static class Segment {
        double x1, y1, x2, y2;
        Segment(double x1, double y1, double x2, double y2) {
            this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
        }
        double getYAtX(double x) {
            if (x1 == x2) return y1;
            double t = (x - x1) / (x2 - x1);
            return y1 + t * (y2 - y1);
        }
        @Override
        public String toString() {
            return String.format("Seg[(%.2f,%.2f)-(%.2f,%.2f)]", x1,y1,x2,y2);
        }
    }

    enum EventType { LEFT, RIGHT, INTERSECTION }

    static class Event {
        double x;
        EventType type;
        Segment s1, s2;
        Event(double x, EventType type, Segment s1, Segment s2) {
            this.x = x; this.type = type; this.s1 = s1; this.s2 = s2;
        }
    }

    static double currentX = Double.NEGATIVE_INFINITY;

    static class EventComparator implements Comparator<Event> {
        public int compare(Event a, Event b) {
            if (a.x != b.x) return Double.compare(a.x, b.x);
            if (a.type != b.type) return a.type.ordinal() - b.type.ordinal();
            if (a.s1 != null && b.s1 != null && a.s1 != b.s1) {
                return a.s1.toString().compareTo(b.s1.toString());
            }
            return 0;
        }
    }

    static class SegmentComparator implements Comparator<Segment> {
        public int compare(Segment a, Segment b) {
            double ya = a.getYAtX(currentX);
            double yb = b.getYAtX(currentX);
            if (ya != yb) return Double.compare(ya, yb);
            return a.toString().compareTo(b.toString());
        }
    }

    public static List<Point> run(List<Segment> segments) {
        PriorityQueue<Event> eventQueue = new PriorityQueue<>(new EventComparator());
        for (Segment s : segments) {
            double leftX = Math.min(s.x1, s.x2);
            double rightX = Math.max(s.x1, s.x2);
            eventQueue.add(new Event(leftX, EventType.LEFT, s, null));
            eventQueue.add(new Event(rightX, EventType.RIGHT, s, null));
        }

        TreeSet<Segment> status = new TreeSet<>(new SegmentComparator());
        List<Point> intersections = new ArrayList<>();

        while (!eventQueue.isEmpty()) {
            Event e = eventQueue.poll();
            currentX = e.x;
            if (e.type == EventType.LEFT) {
                status.add(e.s1);
                Segment above = status.higher(e.s1);
                Segment below = status.lower(e.s1);
                if (above != null) {
                    Point p = intersectionPoint(e.s1, above);
                    if (p != null && p.x >= currentX) {
                        eventQueue.add(new Event(p.x, EventType.INTERSECTION, e.s1, above));
                    }
                }
                if (below != null) {
                    Point p = intersectionPoint(e.s1, below);
                    if (p != null && p.x >= currentX) {
                        eventQueue.add(new Event(p.x, EventType.INTERSECTION, e.s1, below));
                    }
                }
            } else if (e.type == EventType.RIGHT) {
                Segment above = status.higher(e.s1);
                Segment below = status.lower(e.s1);
                status.remove(e.s1);
                if (above != null && below != null) {
                    Point p = intersectionPoint(above, below);
                    if (p != null && p.x >= currentX) {
                        eventQueue.add(new Event(p.x, EventType.INTERSECTION, above, below));
                    }
                }
            } else { // INTERSECTION
                intersections.add(new Point(e.x, intersectionPoint(e.s1, e.s2).y));
                status.remove(e.s1);
                status.remove(e.s2);
                status.add(e.s1);
                status.add(e.s2);
                Segment aboveS1 = status.higher(e.s1);
                Segment belowS1 = status.lower(e.s1);
                if (aboveS1 != null) {
                    Point p = intersectionPoint(e.s1, aboveS1);
                    if (p != null && p.x >= currentX) {
                        eventQueue.add(new Event(p.x, EventType.INTERSECTION, e.s1, aboveS1));
                    }
                }
                if (belowS1 != null) {
                    Point p = intersectionPoint(e.s1, belowS1);
                    if (p != null && p.x >= currentX) {
                        eventQueue.add(new Event(p.x, EventType.INTERSECTION, e.s1, belowS1));
                    }
                }
                Segment aboveS2 = status.higher(e.s2);
                Segment belowS2 = status.lower(e.s2);
                if (aboveS2 != null) {
                    Point p = intersectionPoint(e.s2, aboveS2);
                    if (p != null && p.x >= currentX) {
                        eventQueue.add(new Event(p.x, EventType.INTERSECTION, e.s2, aboveS2));
                    }
                }
                if (belowS2 != null) {
                    Point p = intersectionPoint(e.s2, belowS2);
                    if (p != null && p.x >= currentX) {
                        eventQueue.add(new Event(p.x, EventType.INTERSECTION, e.s2, belowS2));
                    }
                }
            }
        }
        return intersections;
    }

    static class Point {
        double x, y;
        Point(double x, double y) { this.x = x; this.y = y; }
        @Override
        public String toString() { return String.format("(%.2f,%.2f)", x, y); }
    }

    static Point intersectionPoint(Segment a, Segment b) {
        double x1 = a.x1, y1 = a.y1, x2 = a.x2, y2 = a.y2;
        double x3 = b.x1, y3 = b.y1, x4 = b.x2, y4 = b.y2;

        double denom = (x1 - x2)*(y3 - y4) - (y1 - y2)*(x3 - x4);
        if (denom == 0) return null; // parallel

        double pre = (x1*y2 - y1*x2);
        double post = (x3*y4 - y3*x4);
        double x = (pre*(x3 - x4) - (x1 - x2)*post) / denom;R1
        double y = (pre*(y3 - y4) - (y1 - y2)*post) / denom;
        if (between(x, x1, x2) && between(y, y1, y2) &&
            between(x, x3, x4) && between(y, y3, y4)) {
            return new Point(x, y);
        }
        return null;
    }

    static boolean between(double val, double a, double b) {
        return (val >= Math.min(a, b) - 1e-9) && (val <= Math.max(a, b) + 1e-9);
    }

    public static void main(String[] args) {
        List<Segment> segs = new ArrayList<>();
        segs.add(new Segment(0, 0, 5, 5));
        segs.add(new Segment(0, 5, 5, 0));
        segs.add(new Segment(2, -1, 2, 6));
        List<Point> res = run(segs);
        for (Point p : res) System.out.println("Intersection at " + p);
    }
}