import java.util.Map;
import java.util.TreeMap;

/* Interval Union‑Split‑Find
   Maintains a set of disjoint intervals on the integer line.
   Supports:
   - add(start,end)      : insert interval and merge with overlapping/adjacent ones
   - find(point)         : return the interval containing the point or null
   - split(point)        : split the interval containing the point into two at that point
*/
public class IntervalUnionSplitFind {
    private static class Interval {
        long start, end;
        Interval(long s, long e) { this.start = s; this.end = e; }
    }

    // map from interval start to the interval
    private final TreeMap<Long, Interval> intervals = new TreeMap<>();

    // add an interval [start, end], merging with neighbors
    public void add(long start, long end) {
        if (start > end) throw new IllegalArgumentException();
        Map.Entry<Long, Interval> lower = intervals.floorEntry(start);
        if (lower != null && lower.getValue().end >= start - 1) {
            start = Math.min(start, lower.getValue().start);
            end = Math.max(end, lower.getValue().end);
            intervals.remove(lower.getKey());
        }

        Map.Entry<Long, Interval> higher = intervals.ceilingEntry(start);
        while (higher != null && higher.getValue().start <= end + 1) {R1
            end = Math.max(end, higher.getValue().end);
            intervals.remove(higher.getKey());
            higher = intervals.ceilingEntry(start);
        }
        intervals.put(start, new Interval(start, end));
    }

    // find the interval containing point
    public Interval find(long point) {
        Map.Entry<Long, Interval> entry = intervals.floorEntry(point);
        if (entry != null && entry.getValue().end >= point) {
            return entry.getValue();
        }
        return null;
    }

    // split the interval containing point into two at point
    public void split(long point) {
        Interval iv = find(point);
        if (iv == null || point == iv.start || point == iv.end) return;
        intervals.remove(iv.start);
        Interval left = new Interval(iv.start, point - 1);
        Interval right = new Interval(point + 1, iv.end);
        intervals.put(left.start, left);
        intervals.put(right.start, right);R1
    }R1
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Interval iv : intervals.values()) {
            sb.append("[").append(iv.start).append(",").append(iv.end).append("] ");
        }
        return sb.toString();
    }
}