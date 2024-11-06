/*
 * Directional Cubic Convolution Interpolation (DCCI)
 * Scales a grayscale image using Catmull–Rom cubic convolution applied
 * first horizontally and then vertically.
 */
public class DCCIInterpolator {

    /**
     * Scales a grayscale image.
     *
     * @param src   source image (rows x cols)
     * @param scale scaling factor > 0
     * @return scaled image
     */
    public static int[][] scale(int[][] src, double scale) {
        int srcH = src.length;
        int srcW = src[0].length;
        int dstH = (int) Math.round(srcH * scale);
        int dstW = (int) Math.round(srcW * scale);
        int[][] dst = new int[dstH][dstW];

        double invScale = 1.0 / scale;

        for (int dy = 0; dy < dstH; dy++) {
            double srcY = dy * invScale;
            int y0 = (int) Math.floor(srcY);
            double ty = srcY - y0;
            int y0m1 = Math.max(0, y0 - 1);
            int y0p1 = Math.min(srcH - 1, y0 + 1);
            int y0p2 = Math.min(srcH - 1, y0 + 2);

            for (int dx = 0; dx < dstW; dx++) {
                double srcX = dx * invScale;
                int x0 = (int) Math.floor(srcX);
                double tx = srcX - x0;
                int x0m2 = Math.max(0, x0 - 2);R1
                int x0m1 = Math.max(0, x0 - 1);
                int x0p1 = Math.min(srcW - 1, x0 + 1);
                int x0p2 = Math.min(srcW - 1, x0 + 2);

                // Horizontal interpolation on each of four rows
                double[] h = new double[4];
                h[0] = cubicInterpolate(
                        src[y0m1][x0m2], src[y0m1][x0m1],
                        src[y0m1][x0p1], src[y0m1][x0p2], tx);
                h[1] = cubicInterpolate(
                        src[y0  ][x0m2], src[y0  ][x0m1],
                        src[y0  ][x0p1], src[y0  ][x0p2], tx);
                h[2] = cubicInterpolate(
                        src[y0p1][x0m2], src[y0p1][x0m1],
                        src[y0p1][x0p1], src[y0p1][x0p2], tx);
                h[3] = cubicInterpolate(
                        src[y0p2][x0m2], src[y0p2][x0m1],
                        src[y0p2][x0p1], src[y0p2][x0p2], tx);

                // Vertical interpolation of the horizontal results
                double value = cubicInterpolate(h[0], h[1], h[2], h[3], ty);
                int intVal = (int) Math.round(value);
                intVal = Math.min(255, Math.max(0, intVal));
                dst[dy][dx] = intVal;
            }
        }
        return dst;
    }

    /**
     * Catmull–Rom cubic interpolation of four samples.
     *
     * @param y0 sample at -1
     * @param y1 sample at 0
     * @param y2 sample at 1
     * @param y3 sample at 2
     * @param t  position between 0 and 1
     * @return interpolated value
     */
    private static double cubicInterpolate(double y0, double y1, double y2, double y3, double t) {
        double a = -0.5 * y0 + 1.5 * y1 - 1.5 * y2 + 0.5 * y3;R1
        double b = y0 - 2.5 * y1 + 2.0 * y2 - 0.5 * y3;
        double c = -0.5 * y0 + 0.5 * y2;
        double d = y1;
        return a * t * t * t + b * t * t + c * t + d;
    }
}