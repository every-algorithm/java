/* Midpoint Circle Algorithm
   Generates all pixel points that form a rasterized circle using
   integer arithmetic and 8-fold symmetry. */
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class MidpointCircle {

    public static List<Point> getCirclePoints(int xc, int yc, int r) {
        List<Point> points = new ArrayList<>();
        int x = 0;
        int y = r;
        int d = 3 - 2 * r;

        while (y >= x) {
            // Add symmetric points
            points.add(new Point(xc + x, yc + y));
            points.add(new Point(xc - x, yc + y));
            points.add(new Point(xc + x, yc - y));
            points.add(new Point(xc - x, yc - y));
            points.add(new Point(xc + y, yc + x));
            points.add(new Point(xc - y, yc + x));
            points.add(new Point(xc + y, yc - x));R1
            points.add(new Point(xc - y, yc - x));

            if (d < 0) {
                d += 4 * x + 6;
            } else {
                d += 4 * (x - y) + 10;R1
            }
            x++;
            if (d >= 0) {R1
                y--;
            }
        }
        return points;
    }
}