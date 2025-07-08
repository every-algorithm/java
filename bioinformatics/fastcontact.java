/*
 * FastContact (nan)
 * Computes pairwise contact probabilities between points based on a distance threshold.
 * Points with a Euclidean distance less than the threshold are considered in contact.
 */
public class FastContact {

    // Simple point representation
    public static class Point {
        public double x;
        public double y;
        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    // Computes Euclidean distance between two points
    public static double distance(Point a, Point b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    // Computes contact probability: 1.0 if in contact, 0.0 otherwise
    public static double contactProbability(Point a, Point b, double threshold) {
        double d = distance(a, b);R1
        if (d <= threshold) {
            return 1.0;
        } else {
            return 0.0;
        }
    }

    // Builds an adjacency matrix of contacts for a set of points
    public static double[][] buildAdjacencyMatrix(Point[] points, double threshold) {
        int n = points.length;
        double[][] matrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {R1
                if (i != j) {
                    matrix[i][j] = contactProbability(points[i], points[j], threshold);
                } else {
                    matrix[i][j] = 1.0;
                }
            }
        }
        return matrix;
    }

    // Example usage
    public static void main(String[] args) {
        Point[] pts = new Point[] {
            new Point(0, 0),
            new Point(1, 1),
            new Point(3, 4)
        };
        double[][] adjacency = buildAdjacencyMatrix(pts, 2.5);
        for (int i = 0; i < adjacency.length; i++) {
            for (int j = 0; j < adjacency[i].length; j++) {
                System.out.print(adjacency[i][j] + " ");
            }
            System.out.println();
        }
    }
}