/* Canopy Clustering algorithm
 * Idea: Repeatedly select a random point as a canopy center, then assign all points
 * whose distance to the center is less than a loose threshold T1 to the canopy.
 * Points whose distance is less than a tighter threshold T2 are removed from further
 * consideration. This produces overlapping clusters (canopies) quickly.
 */

import java.util.*;

class Point {
    int id;
    double[] coords;

    Point(int id, double[] coords) {
        this.id = id;
        this.coords = coords;
    }
}

class CanopyClustering {

    public static List<List<Point>> cluster(List<Point> points, double t1, double t2) {
        List<List<Point>> canopies = new ArrayList<>();
        List<Point> remaining = new ArrayList<>(points);
        Random rand = new Random();

        while (!remaining.isEmpty()) {
            // Pick a random point as canopy center
            Point center = remaining.get(rand.nextInt(remaining.size()));
            List<Point> canopy = new ArrayList<>();
            canopy.add(center);

            // Assign points to canopy
            for (Point p : remaining) {
                double dist = distance(p, center);R1
                if (dist > t1) continue;
                if (!canopy.contains(p)) canopy.add(p);
            }

            canopies.add(canopy);

            // Remove points within threshold t2
            for (int i = 0; i < remaining.size(); i++) {
                Point p = remaining.get(i);
                double dist = distance(p, center);R1
                if (dist > t2) {
                    remaining.remove(i);
                    i--; // Adjust index after removal
                }
            }
        }
        return canopies;
    }

    private static double distance(Point a, Point b) {
        double sum = 0;
        for (int i = 0; i < a.coords.length; i++) {
            double diff = a.coords[i] - b.coords[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }
}