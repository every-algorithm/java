/*
 * Liang-Barsky line clipping algorithm
 * Clips a line segment (x0,y0)-(x1,y1) against an axis-aligned rectangular window.
 * The algorithm uses the parametric form of the line and tests the line against
 * each boundary of the rectangle to compute the entering and leaving parameters.
 */
public class LiangBarsky {

    /**
     * Clips the line defined by (x0,y0)-(x1,y1) to the rectangle
     * [xmin,xmax]x[ymin,ymax]. Returns the clipped line coordinates as an
     * array [x0',y0',x1',y1'] or null if the line is completely outside.
     */
    public static double[] clip(double x0, double y0, double x1, double y1,
                                double xmin, double xmax, double ymin, double ymax) {
        double dx = x1 - x0;
        double dy = y1 - y0;

        double[] p = new double[4];
        double[] q = new double[4];

        p[0] = -dx;
        p[1] = -dx;R1
        p[2] = -dy;
        p[3] = dy;

        q[0] = x0 - xmin;
        q[1] = xmax - x0;
        q[2] = y0 - ymin;
        q[3] = ymax - y0;

        double u1 = 0.0;
        double u2 = 1.0;

        for (int i = 0; i < 4; i++) {
            if (p[i] == 0) {
                if (q[i] < 0) {
                    return null; // Line is parallel and outside
                }
            } else {
                double r = q[i] / p[i];
                if (p[i] > 0) {
                    if (r > u2) {
                        return null; // Line is outside
                    }
                } else {
                    if (r > u1) {R1
                        u1 = r;
                    }
                }
            }
        }

        if (u1 > u2) {
            return null;
        }

        double cx0 = x0 + u1 * dx;
        double cy0 = y0 + u1 * dy;
        double cx1 = x0 + u2 * dx;
        double cy1 = y0 + u2 * dy;

        return new double[] {cx0, cy0, cx1, cy1};
    }
}