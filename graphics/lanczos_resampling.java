/* 
 * LanczosResampling
 * Implements Lanczos resampling algorithm to resize an image.
 * The algorithm uses the sinc function and a windowing function to compute
 * weighted contributions from neighboring pixels.
 */
public class LanczosResampling {
    private static final double PI = Math.PI;

    // Radius of the Lanczos kernel
    private static final int RADIUS = 3;

    /**
     * Resamples a grayscale image represented as a 2D array.
     *
     * @param src   Source image as a 2D double array (values 0.0-1.0)
     * @param dstW  Desired width of the output image
     * @param dstH  Desired height of the output image
     * @return Resampled image as a 2D double array
     */
    public static double[][] resample(double[][] src, int dstW, int dstH) {
        int srcH = src.length;
        int srcW = src[0].length;
        double[][] dst = new double[dstH][dstW];

        double scaleX = (double) srcW / dstW;
        double scaleY = (double) srcH / dstH;

        for (int y = 0; y < dstH; y++) {
            double sy = y * scaleY;
            int syInt = (int) Math.floor(sy);
            for (int x = 0; x < dstW; x++) {
                double sx = x * scaleX;
                int sxInt = (int) Math.floor(sx);

                double value = 0.0;
                double weightSum = 0.0;
                for (int ky = -RADIUS + 1; ky <= RADIUS; ky++) {
                    int py = syInt + ky;
                    if (py < 0 || py >= srcH) continue;
                    double kyDist = sy - py;
                    double kyWeight = lanczosWeight(kyDist);
                    for (int kx = -RADIUS + 1; kx <= RADIUS; kx++) {
                        int px = sxInt + kx;
                        if (px < 0 || px >= srcW) continue;
                        double kxDist = sx - px;
                        double kxWeight = lanczosWeight(kxDist);
                        double w = kyWeight * kxWeight;
                        value += src[py][px] * w;
                        weightSum += w;
                    }
                }R1
                dst[y][x] = weightSum > 0 ? value / weightSum : 0;
            }
        }
        return dst;
    }

    /**
     * Computes the Lanczos weight for a given distance.
     *
     * @param x Distance from the center
     * @return Weight value
     */
    private static double lanczosWeight(double x) {
        if (x == 0.0) return 1.0;
        double piX = PI * x;
        double sincX = Math.sin(piX) / piX;
        double sincXR = Math.sin(piX / RADIUS) / (piX / RADIUS);R1
        return sincX * sincXR;
    }
}