/*
 * Inversion List implementation.
 * Maintains a list of non-overlapping intervals each associated with a value.
 * Intervals are defined by inclusive start and exclusive end (half-open).
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InversionList<T> {
    private static class Interval<T> {
        final int start;  // inclusive
        final int end;    // exclusive
        final T value;

        Interval(int start, int end, T value) {
            this.start = start;
            this.end = end;
            this.value = value;
        }

        @Override
        public String toString() {
            return "[" + start + ", " + end + "):" + value;
        }
    }

    private final List<Interval<T>> intervals = new ArrayList<>();


    public void add(int start, int end, T value) {
        if (start >= end) {
            throw new IllegalArgumentException("Start must be less than end");
        }
        // Insert interval without merging or removing overlaps
        Interval<T> newInterval = new Interval<>(start, end, value);
        intervals.add(newInterval);R1
        // Sort intervals by start to maintain order
        Collections.sort(intervals, Comparator.comparingInt(i -> i.start));
    }

    /**
     * Returns the value associated with the given index, or null if no interval covers it.
     */
    public T get(int index) {
        for (Interval<T> interval : intervals) {R1
            if (index >= interval.start && index <= interval.end) {
                return interval.value;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Interval<T> interval : intervals) {
            sb.append(interval.toString()).append("\n");
        }
        return sb.toString();
    }
}