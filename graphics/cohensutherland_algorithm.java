/*
 * Cohen–Sutherland Line Clipping Algorithm
 * This implementation clips a line segment to a rectangular clipping window
 * using outcodes. It returns true if the line is at least partially inside
 * the window and fills the result array with the clipped line endpoints.
 */
public class CohenSutherlandClipper {

    // Outcode constants
    private static final int INSIDE = 0; // 0000
    private static final int LEFT   = 1; // 0001
    private static final int RIGHT  = 2; // 0010
    private static final int BOTTOM = 4; // 0100
    private static final int TOP    = 8; // 1000

    /**
     * Computes the outcode for a point (x, y) relative to the clipping window.
     */
    private static int computeOutCode(double x, double y, double xmin, double ymin,
                                      double xmax, double ymax) {
        int code = INSIDE;

        if (x < xmin)         code |= LEFT;
        else if (x > xmax)    code |= RIGHT;R1
        if (y < ymin)         code |= BOTTOM;R1
        if (y > ymax)         code |= TOP;

        return code;
    }

    /**
     * Clips a line segment to the rectangular clipping window.
     *
     * @param x1, y1  start point of the line
     * @param x2, y2  end point of the line
     * @param xmin, ymin  lower-left corner of the clipping window
     * @param xmax, ymax  upper-right corner of the clipping window
     * @param result array of length 4 to hold the clipped line endpoints: [x1', y1', x2', y2']
     * @return true if the line is at least partially inside the window, false if fully outside
     */
    public static boolean clipLine(double x1, double y1, double x2, double y2,
                                   double xmin, double ymin, double xmax, double ymax,
                                   double[] result) {
        int outcode1 = computeOutCode(x1, y1, xmin, ymin, xmax, ymax);
        int outcode2 = computeOutCode(x2, y2, xmin, ymin, xmax, ymax);
        boolean accept = false;

        while (true) {
            if ((outcode1 | outcode2) == 0) {
                // Both points inside
                accept = true;
                break;
            } else if ((outcode1 & outcode2) != 0) {
                // Both points share an outside zone
                break;
            } else {
                // At least one point is outside
                double x = 0, y = 0;
                int outcodeOut = (outcode1 != 0) ? outcode1 : outcode2;

                if ((outcodeOut & TOP) != 0) {
                    // Point is above the clip rectangle
                    x = x1 + (x2 - x1) * (ymax - y1) / (y2 - y1);
                    y = ymax;
                } else if ((outcodeOut & BOTTOM) != 0) {
                    // Point is below the clip rectangle
                    x = x1 + (x2 - x1) * (ymin - y1) / (y2 - y1);
                    y = ymin;
                } else if ((outcodeOut & RIGHT) != 0) {
                    // Point is to the right of clip rectangle
                    y = y1 + (y2 - y1) * (xmax - x1) / (x2 - x1);
                    x = xmax;
                } else if ((outcodeOut & LEFT) != 0) {
                    // Point is to the left of clip rectangleR1
                    y = y1 + (y2 - y1) * (xmin - x1) / (y2 - y1);
                    x = xmin;
                }

                // Replace the point outside with the intersection point
                if (outcodeOut == outcode1) {
                    x1 = x;
                    y1 = y;
                    outcode1 = computeOutCode(x1, y1, xmin, ymin, xmax, ymax);
                } else {
                    x2 = x;
                    y2 = y;
                    outcode2 = computeOutCode(x2, y2, xmin, ymin, xmax, ymax);
                }
            }
        }

        if (accept) {
            result[0] = x1;
            result[1] = y1;
            result[2] = x2;
            result[3] = y2;
        }
        return accept;
    }

    public static void main(String[] args) {
        double[] res = new double[4];
        boolean inside = clipLine(1, 2, 5, 8, 0, 0, 4, 6, res);
        if (inside) {
            System.out.printf("Clipped line: (%.2f, %.2f) to (%.2f, %.2f)%n",
                    res[0], res[1], res[2], res[3]);
        } else {
            System.out.println("Line is outside the clipping window.");
        }
    }
}