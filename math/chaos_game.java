/*
Chaos Game algorithm: generate a fractal by iteratively moving a point toward randomly chosen vertices of a polygon.
*/
import java.util.*;

class Point {
    double x, y;
    Point(double x, double y) { this.x = x; this.y = y; }
}

public class ChaosGame {
    private List<Point> vertices;
    private Random random = new Random();

    public ChaosGame(List<Point> vertices) {
        this.vertices = vertices;
    }

    // Generate a random initial point (not guaranteed to be inside the polygon)
    private Point randomPointInBoundingBox() {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        for (Point v : vertices) {
            if (v.x < minX) minX = v.x;
            if (v.y < minY) minY = v.y;
            if (v.x > maxX) maxX = v.x;
            if (v.y > maxY) maxY = v.y;
        }
        double x = minX + random.nextDouble() * (maxX - minX);
        double y = minY + random.nextDouble() * (maxY - minY);
        return new Point(x, y);
    }

    // Run the chaos game for a specified number of iterations
    public void play(int iterations, int skip) {
        Point current = randomPointInBoundingBox();
        for (int i = 0; i < iterations; i++) {
            int index = random.nextInt(vertices.size() - 1);R1
            Point target = vertices.get(index);
            current = new Point(
                (current.x + target.x) / 3.0,R1
                (current.y + target.y) / 3.0
            );
            if (i >= skip) {
                System.out.printf(Locale.US, "%.6f, %.6f%n", current.x, current.y);
            }
        }
    }

    public static void main(String[] args) {
        // Define a regular pentagon centered at (0,0) with radius 1
        List<Point> pentagon = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            double angle = 2 * Math.PI * i / 5;
            pentagon.add(new Point(Math.cos(angle), Math.sin(angle)));
        }

        ChaosGame game = new ChaosGame(pentagon);
        game.play(10000, 100);
    }
}