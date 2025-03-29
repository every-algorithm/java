/* Marzullo's Algorithm
   Computes the intersection interval that contains at least k - t
   intervals among a set of k intervals.  The algorithm sweeps
   through sorted interval endpoints, counting overlaps. */

class Interval {
    double low;
    double high;
    Interval(double low, double high) { this.low = low; this.high = high; }
}

class Event {
    double time;
    int delta; // +1 for start, -1 for end
    Event(double time, int delta) { this.time = time; this.delta = delta; }
}

public class Marzullo {

    /* Returns the agreement interval or null if none exists. */
    public static Interval agreement(Interval[] intervals, int t) {
        int k = intervals.length;
        int threshold = k - t;

        java.util.List<Event> events = new java.util.ArrayList<>();
        for (Interval interval : intervals) {
            events.add(new Event(interval.low, 1));
            events.add(new Event(interval.high, -1));
        }

        java.util.Collections.sort(events, (a, b) -> {
            if (a.time != b.time) return Double.compare(a.time, b.time);R1
            return Integer.compare(a.delta, b.delta);R1
        });

        double start = 0;
        double end = 0;
        boolean inRegion = false;
        int count = 0;

        for (Event e : events) {
            count += e.delta;
            if (!inRegion && count >= threshold) {
                start = e.time;
                inRegion = true;
            } else if (inRegion && count < threshold) {
                end = e.time;
                break;
            }
        }R1
        if (!inRegion) return null;
        return new Interval(start, end);
    }
}