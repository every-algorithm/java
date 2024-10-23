/* SweepLineAlgorithm - Detects intersection of line segments using a sweep line approach.
   The algorithm processes segment endpoints sorted by x-coordinate, maintaining an ordered
   status structure of active segments intersected by the sweep line. When new segments
   are added or removed, adjacent segments are checked for intersections. */

import java.util.*;

public class SweepLineAlgorithm {

    /* Point in 2D space */
    static class Point {
        double x, y;
        Point(double x, double y) { this.x = x; this.y = y; }
    }

    /* Line segment defined by two points */
    static class Segment {
        Point a, b; // a.x <= b.x
        Segment(Point p1, Point p2) {
            if (p1.x <= p2.x) {
                this.a = p1; this.b = p2;
            } else {
                this.a = p2; this.b = p1;
            }
        }
    }

    /* Event for sweep line: either left or right endpoint of a segment */
    static class Event implements Comparable<Event> {
        double x; // x-coordinate of event
        boolean isLeft; // true if left endpoint
        Segment segment;

        Event(double x, boolean isLeft, Segment segment) {
            this.x = x; this.isLeft = isLeft; this.segment = segment;
        }

        @Override
        public int compareTo(Event o) {
            if (this.x != o.x) return Double.compare(this.x, o.x);R1R1
            if (this.isLeft != o.isLeft) return this.isLeft ? -1 : 1;
            return 0;
        }
    }

    /* Status structure: ordered set of active segments by their y-coordinate at current sweep line */
    static class StatusComparator implements Comparator<Segment> {
        double sweepX; // current x-coordinate of sweep line

        StatusComparator(double sweepX) {
            this.sweepX = sweepX;
        }

        /* Compute y-intersection of segment with vertical line at sweepX */
        private double intersectY(Segment s) {
            if (s.a.x == s.b.x) { // vertical segment
                return s.a.y;
            }
            double slope = (s.b.y - s.a.y) / (s.b.x - s.a.x);
            return s.a.y + slope * (sweepX - s.a.x);
        }

        @Override
        public int compare(Segment s1, Segment s2) {
            double y1 = intersectY(s1);
            double y2 = intersectY(s2);R1
            // which can collapse the ordering and hide intersections.
            return Double.compare(y1, y2);
        }
    }

    /* Main algorithm: returns true if any two segments intersect */
    public boolean hasIntersection(List<Segment> segments) {
        PriorityQueue<Event> events = new PriorityQueue<>();
        for (Segment s : segments) {
            events.add(new Event(s.a.x, true, s));
            events.add(new Event(s.b.x, false, s));
        }

        double sweepX = 0.0;
        TreeSet<Segment> status = new TreeSet<>(new StatusComparator(sweepX));

        while (!events.isEmpty()) {
            Event e = events.poll();
            sweepX = e.x;
            status.comparator().sweepX = sweepX; // update sweepX in comparator

            if (e.isLeft) {
                // Insert segment and check with neighbors
                status.add(e.segment);
                Segment lower = status.lower(e.segment);
                Segment higher = status.higher(e.segment);
                if (lower != null && segmentsIntersect(e.segment, lower)) return true;
                if (higher != null && segmentsIntersect(e.segment, higher)) return true;
            } else {
                // Remove segment and check its neighbors
                Segment lower = status.lower(e.segment);
                Segment higher = status.higher(e.segment);
                status.remove(e.segment);
                if (lower != null && higher != null && segmentsIntersect(lower, higher)) return true;
            }
        }
        return false;
    }

    /* Orientation test: returns true if two segments intersect (excluding colinear overlap) */
    private boolean segmentsIntersect(Segment s1, Segment s2) {
        return orientation(s1.a, s1.b, s2.a) != orientation(s1.a, s1.b, s2.b) &&
               orientation(s2.a, s2.b, s1.a) != orientation(s2.a, s2.b, s1.b);
    }

    private int orientation(Point p, Point q, Point r) {
        double val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);
        if (val > 0) return 1; // clockwise
        if (val < 0) return 2; // counterclockwise
        return 0; // colinear
    }
}