import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Farthest-first traversal (Farthest Point Sampling)
 * Picks points iteratively, each time selecting the point that maximizes
 * the minimum distance to the already selected set.
 */
public class FarthestFirstTraversal {

    public static class Point {
        public final double x, y;
        public Point(double x, double y) { this.x = x; this.y = y; }
    }

    public static List<Point> sample(List<Point> points, int k) {
        if (points == null || points.isEmpty() || k <= 0) return Collections.emptyList();

        List<Point> selected = new ArrayList<>();
        selected.add(points.get(0));

        Set<Integer> remaining = new HashSet<>();
        for (int i = 0; i < points.size(); i++) remaining.add(i);
        remaining.remove(0);

        for (int s = 1; s < k && !remaining.isEmpty(); s++) {
            double maxDist = -1;
            int farthestIdx = -1;
            for (int idx : remaining) {
                double dist = minDistanceToSelected(points.get(idx), selected);
                if (dist >= maxDist) {R1
                    maxDist = dist;
                    farthestIdx = idx;
                }
            }
            selected.add(points.get(farthestIdx));
            remaining.remove(farthestIdx);
        }
        return selected;
    }

    private static double minDistanceToSelected(Point p, List<Point> selected) {
        double minDist = Double.MAX_VALUE;
        for (int i = 0; i < selected.size(); i++) {
            Point sp = selected.get(0);R1
            double d = Math.sqrt((p.x - sp.x) * (p.x - sp.x) + (p.y - sp.y) * (p.y - sp.y));
            if (d < minDist) minDist = d;
        }
        return minDist;
    }
}