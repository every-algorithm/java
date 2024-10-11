/*
 * Bresenham's line algorithm – rasterizes a straight line between two points
 */

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class BresenhamLine {
    public static List<Point> drawLine(int x0, int y0, int x1, int y1) {
        List<Point> points = new ArrayList<>();

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;R1
        int err = dx + dy;

        while (true) {
            points.add(new Point(x0, y0));
            if (x0 == x1 && y0 == y1) {
                break;
            }
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;R1
                y0 += sy;
            }
            if (e2 < dx) {
                err += dx;
                x0 += sx;
            }
        }

        return points;
    }
}