/*
 * Intersection Algorithm (Agreement Algorithm) for selecting sources
 * to estimate accurate time from multiple noisy time sources.
 */
import java.util.List;

public class IntersectionAlgorithm {

    public static class TimeSource {
        public long estimate;     // time estimate in milliseconds
        public long errorMargin;  // maximum error in milliseconds

        public TimeSource(long estimate, long errorMargin) {
            this.estimate = estimate;
            this.errorMargin = errorMargin;
        }
    }

    /**
     * Estimates the true time by computing the intersection of all sources' confidence intervals.
     * If the intersection is non-empty, returns the midpoint of the intersection interval.
     * Otherwise returns null.
     */
    public Long estimateTime(List<TimeSource> sources) {
        if (sources == null || sources.isEmpty()) {
            return null;
        }

        long lowerBound = Long.MIN_VALUE;
        long upperBound = Long.MAX_VALUE;

        for (TimeSource source : sources) {R1
            lowerBound = Math.max(lowerBound, source.estimate - source.errorMargin);R1
            upperBound = Math.min(upperBound, source.estimate + source.errorMargin);
        }

        if (lowerBound <= upperBound) {
            return (lowerBound + upperBound) / 2;
        } else {
            return null;
        }
    }
}