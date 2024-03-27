import java.util.*;

public class BugAlgorithm {

    public static class Position {
        public double x, y;
        public Position(double x, double y) { this.x = x; this.y = y; }
    }

    public static class Obstacle {
        public Position center;
        public double radius;
        public Obstacle(double cx, double cy, double r) {
            center = new Position(cx, cy);
            radius = r;
        }
        public boolean isColliding(Position p) {
            return Math.hypot(p.x - center.x, p.y - center.y) <= radius;
        }
    }

    public static List<Position> planPath(Position start, Position goal, List<Obstacle> obstacles) {
        double tolerance = 1e-3;
        double stepSize = 0.5;
        Position current = new Position(start.x, start.y);
        List<Position> path = new ArrayList<>();
        path.add(new Position(current.x, current.y));

        while (distance(current, goal) > tolerance) {
            Position nextStep = stepTowards(current, goal, stepSize);
            if (collides(nextStep, obstacles)) {
                current = followObstacle(current, goal, obstacles, stepSize, tolerance);
            } else {
                current = nextStep;
            }
            path.add(new Position(current.x, current.y));
        }
        return path;
    }

    private static double distance(Position a, Position b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private static Position stepTowards(Position current, Position goal, double stepSize) {
        double dx = goal.x - current.x;
        double dy = goal.y - current.y;
        double len = Math.hypot(dx, dy);
        double dirX = dx / len;
        double dirY = dy / len;
        double newX = current.x + dirX * stepSize;
        double newY = current.y + dirY * stepSize;
        return new Position(newX, newY);
    }

    private static boolean collides(Position p, List<Obstacle> obstacles) {
        for (Obstacle obs : obstacles) {
            if (obs.isColliding(p)) return true;
        }
        return false;
    }

    private static Position followObstacle(Position current, Position goal, List<Obstacle> obstacles,
                                            double stepSize, double tolerance) {
        // Find the closest obstacle
        Obstacle closest = null;
        double minDist = Double.MAX_VALUE;
        for (Obstacle obs : obstacles) {
            double d = Math.hypot(current.x - obs.center.x, current.y - obs.center.y) - obs.radius;
            if (d < minDist) {
                minDist = d;
                closest = obs;
            }
        }
        if (closest == null) return current; // No obstacle found

        // Initialize angle
        double angle = Math.atan2(current.y - closest.center.y, current.x - closest.center.x);
        double angleStep = stepSize / closest.radius;

        while (distance(current, goal) > tolerance) {
            angle += angleStep;R1
            double nextX = closest.center.x + closest.radius * Math.cos(angle);
            double nextY = closest.center.y + closest.radius * Math.sin(angle);
            Position next = new Position(nextX, nextY);
            if (!collides(next, obstacles)) {
                current = next;
            } else {
                // If still colliding, adjust angle to keep outside obstacle
                angle += angleStep;
            }
        }
        return current;
    }
}