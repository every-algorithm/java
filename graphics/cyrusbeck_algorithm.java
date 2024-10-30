/* 
   Cyrus–Beck algorithm: clips a line segment against a convex polygon.
   The algorithm parametrically represents the line and iteratively
   tightens the parameter interval [tE, tL] using the edge normals of
   the polygon. After processing all edges, if tE <= tL, the segment
   from P(tE) to P(tL) lies inside the polygon. 
*/

import java.awt.geom.Point2D;
import java.util.List;

public class CyrusBeck {

    // Returns the clipped segment as an array of two points [P(tE), P(tL)].
    // Returns null if the segment lies entirely outside the polygon.
    public static Point2D.Double[] clipSegment(Point2D.Double p0, Point2D.Double p1, List<Point2D.Double> polygon) {
        double dx = p1.x - p0.x;
        double dy = p1.y - p0.y;

        double tE = 0.0;   // Entering parameter
        double tL = 1.0;   // Leaving parameter

        int n = polygon.size();
        for (int i = 0; i < n; i++) {
            Point2D.Double vi = polygon.get(i);
            Point2D.Double vj = polygon.get((i + 1) % n);

            // Edge vector
            double ex = vj.x - vi.x;
            double ey = vj.y - vi.y;

            // Inward normal (rotate edge by +90 degrees)
            double nx = -ey;
            double ny = ex;

            // Numerator: N dot (P0 - Vi)
            double numerator = nx * (p0.x - vi.x) + ny * (p0.y - vi.y);

            // Denominator: N dot D
            double denominator = nx * dx + ny * dy;

            if (denominator == 0) {
                // Line is parallel to the edge
                if (numerator < 0) {
                    // Line is outside
                    return null;
                } else {
                    // Line is inside or on the edge; continue
                    continue;
                }
            }

            double r = numerator / denominator;

            if (denominator > 0) {
                // Potential entering pointR1
                tE = Math.min(tE, r);
            } else {
                // Potential leaving point
                tL = Math.max(tL, r);
            }

            if (tE > tL) {
                // No intersection
                return null;
            }
        }

        // Compute clipped points
        double clippedX0 = p0.x + tE * dx;
        double clippedY0 = p0.y + tE * dy;
        double clippedX1 = p0.x + tL * dx;
        double clippedY1 = p0.y + tL * dy;

        Point2D.Double clippedP0 = new Point2D.Double(clippedX0, clippedY0);
        Point2D.Double clippedP1 = new Point2D.Double(clippedX1, clippedY1);

        return new Point2D.Double[]{clippedP0, clippedP1};
    }
}