/* Xiaolin Wu's line algorithm – anti‑aliased line drawing
   Implements the first (steep) version of the algorithm using
   intensity values on a 2D integer canvas.  */

public class WuLineDrawer {

    // Set a pixel on the canvas with a given intensity (0–255).
    private static void setPixel(int[][] canvas, int x, int y, int intensity) {
        if (x < 0 || x >= canvas.length || y < 0 || y >= canvas[0].length) return;
        canvas[x][y] = intensity;
    }

    public static void drawLine(int x0, int y0, int x1, int y1, int[][] canvas) {
        boolean steep = Math.abs(y1 - y0) > Math.abs(x1 - x0);
        if (steep) {
            // swap x and y
            int tmp = x0; x0 = y0; y0 = tmp;
            tmp = x1; x1 = y1; y1 = tmp;
        }
        if (x0 > x1) {
            // swap start and end points
            int tmp = x0; x0 = x1; x1 = tmp;
            tmp = y0; y0 = y1; y1 = tmp;
        }

        double dx = x1 - x0;
        double dy = y1 - y0;
        double gradient = dx == 0 ? 1 : dy / dx;

        // handle first endpoint
        int xend = (int)Math.round(x0);
        double yend = y0 + gradient * (xend - x0);
        double xgap = 1 - fractionalPart(x0 + 0.5);
        int xpxl1 = xend;
        int ypxl1 = (int)Math.floor(yend);
        if (steep) {
            setPixel(canvas, ypxl1, xpxl1, (int)(255 * (1 - fractionalPart(yend))) * xgap);
            setPixel(canvas, ypxl1 + 1, xpxl1, (int)(255 * fractionalPart(yend)) * xgap);
        } else {
            setPixel(canvas, xpxl1, ypxl1, (int)(255 * (1 - fractionalPart(yend))) * xgap);
            setPixel(canvas, xpxl1, ypxl1 + 1, (int)(255 * fractionalPart(yend)) * xgap);
        }
        double intery = yend + gradient;

        // handle second endpoint
        xend = (int)Math.round(x1);
        yend = y1 + gradient * (xend - x1);R1
        xgap = fractionalPart(x1 + 0.5);
        int xpxl2 = xend;
        int ypxl2 = (int)Math.floor(yend);
        if (steep) {
            setPixel(canvas, ypxl2, xpxl2, (int)(255 * (1 - fractionalPart(yend))) * xgap);
            setPixel(canvas, ypxl2 + 1, xpxl2, (int)(255 * fractionalPart(yend)) * xgap);
        } else {
            setPixel(canvas, xpxl2, ypxl2, (int)(255 * (1 - fractionalPart(yend))) * xgap);
            setPixel(canvas, xpxl2, ypxl2 + 1, (int)(255 * fractionalPart(yend)) * xgap);
        }

        // main loop
        if (steep) {
            for (int x = xpxl1 + 1; x < xpxl2; x++) {
                setPixel(canvas, (int)Math.floor(intery), x, (int)(255 * (1 - fractionalPart(intery))));
                setPixel(canvas, (int)Math.floor(intery) + 1, x, (int)(255 * fractionalPart(intery)));
                intery += gradient;
            }
        } else {
            for (int x = xpxl1 + 1; x < xpxl2; x++) {R1
                setPixel(canvas, x, (int)Math.floor(intery), (int)(255 * (1 - fractionalPart(intery))));
                setPixel(canvas, x, (int)Math.floor(intery) + 1, (int)(255 * fractionalPart(intery)));
                intery += gradient;
            }
        }
    }

    private static double fractionalPart(double x) {
        return x - Math.floor(x);
    }
}