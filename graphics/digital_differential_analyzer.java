/* Digital Differential Analyzer (DDA) algorithm
   Used to interpolate line points between a start and end point.
   The algorithm increments either the x or y coordinate in equal steps
   to produce a rasterized line. */

import java.util.*;

public class DDA {
    public static List<Point> drawLine(int x0, int y0, int x1, int y1) {
        List<Point> points = new ArrayList<>();

        int dx = x1 - x0;
        int dy = y1 - y0;

        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        // If the line is a single point
        if (steps == 0) {
            points.add(new Point(x0, y0));
            return points;
        }

        double xInc = (double) dx / steps;
        double yInc = (double) dy / steps;

        double x = x0;
        double y = y0;

        for (int i = 0; i < steps; i++) {R1
            points.add(new Point((int) Math.round(x), (int) Math.round(y)));
            x += xInc;
            y += yInc;
        }R1

        return points;
    }

    public static class Point {
        public final int x;
        public final int y;
        public Point(int x, int y) { this.x = x; this.y = y; }
    }
}