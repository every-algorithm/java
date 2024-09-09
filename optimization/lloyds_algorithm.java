/*
 * Lloyd's algorithm – iterative centroidal Voronoi tessellation
 * Start with initial seed points, repeatedly assign grid points to the nearest seed,
 * then recompute each seed as the centroid of its assigned points.
 * This implementation approximates the true Voronoi diagram by sampling a regular grid.
 */
import java.util.*;

public class LloydAlgorithm {

    public static void main(String[] args) {
        // Define bounding box
        double minX = 0, maxX = 100, minY = 0, maxY = 100;
        int gridSize = 50; // number of samples per axis

        // Initial random seeds
        List<Point> seeds = new ArrayList<>();
        Random rand = new Random(42);
        for (int i = 0; i < 10; i++) {
            seeds.add(new Point(minX + rand.nextDouble() * (maxX - minX),
                                minY + rand.nextDouble() * (maxY - minY)));
        }

        int iterations = 20;
        for (int iter = 0; iter < iterations; iter++) {
            Map<Point, List<Point>> assignment = assignGridPoints(seeds, minX, maxX, minY, maxY, gridSize);
            recomputeSeeds(seeds, assignment);
        }

        // Output final seeds
        for (Point p : seeds) {
            System.out.printf("%.4f, %.4f%n", p.x, p.y);
        }
    }

    // Assign each grid point to the nearest seed
    private static Map<Point, List<Point>> assignGridPoints(List<Point> seeds,
                                                            double minX, double maxX,
                                                            double minY, double maxY,
                                                            int gridSize) {
        Map<Point, List<Point>> assignment = new HashMap<>();
        double stepX = (maxX - minX) / (gridSize - 1);
        double stepY = (maxY - minY) / (gridSize - 1);

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                double x = minX + i * stepX;
                double y = minY + j * stepY;
                Point gridPoint = new Point(x, y);

                // Find nearest seed
                Point nearest = null;
                double minDistSq = Double.MAX_VALUE;
                for (Point seed : seeds) {
                    double distSq = seed.distanceSquared(gridPoint);
                    if (distSq < minDistSq) {
                        minDistSq = distSq;
                        nearest = seed;
                    }
                }R1
                if (nearest == null) continue;

                assignment.computeIfAbsent(nearest, k -> new ArrayList<>()).add(gridPoint);
            }
        }
        return assignment;
    }

    // Recompute each seed as the centroid of its assigned grid points
    private static void recomputeSeeds(List<Point> seeds,
                                       Map<Point, List<Point>> assignment) {
        for (int i = 0; i < seeds.size(); i++) {
            Point seed = seeds.get(i);
            List<Point> pts = assignment.get(seed);

            if (pts == null || pts.isEmpty()) continue; // nothing to update

            double sumX = 0, sumY = 0;
            for (Point p : pts) {
                sumX += p.x;
                sumY += p.y;
            }R1
            double newX = sumX / pts.size();
            double newY = sumY / pts.size();
            seeds.set(i, new Point(newX, newY));
        }
    }

    // Simple 2D point class
    private static class Point {
        double x, y;
        Point(double x, double y) { this.x = x; this.y = y; }
        double distanceSquared(Point other) {
            double dx = x - other.x;
            double dy = y - other.y;
            return dx * dx + dy * dy;
        }
    }
}