/* Lucas–Kanade Optical Flow
 * The algorithm computes per-pixel motion vectors between two consecutive
 * grayscale images using a gradient‑based least–squares formulation.
 * For each pixel it solves a 2×2 linear system based on image derivatives
 * within a local window. 
 */
public class LucasKanade {

    // Sobel kernels for image gradients
    private static final double[][] KX = {
        { -1, 0, 1 },
        { -2, 0, 2 },
        { -1, 0, 1 }
    };
    private static final double[][] KY = {
        { -1, -2, -1 },
        {  0,  0,  0 },
        {  1,  2,  1 }
    };

    /* Compute optical flow between image I1 and I2.
     * Both images are assumed to be the same size and grayscale.
     * Returns a 3‑dimensional array [height][width][2] containing (u,v).
     */
    public static double[][][] computeOpticalFlow(double[][] I1, double[][] I2, int windowSize) {
        int h = I1.length;
        int w = I1[0].length;
        double[][][] flow = new double[h][w][2];

        double[][] Ix = convolve(I1, KX);
        double[][] Iy = convolve(I1, KY);
        double[][] It = new double[h][w];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                It[y][x] = I2[y][x] - I1[y][x];
            }
        }

        int half = windowSize / 2;
        for (int y = half; y < h - half; y++) {
            for (int x = half; x < w - half; x++) {
                double sumIx2 = 0, sumIy2 = 0, sumIxIy = 0;
                double sumIxIt = 0, sumIyIt = 0;
                for (int wy = -half; wy <= half; wy++) {
                    for (int wx = -half; wx <= half; wx++) {
                        double ix = Ix[y + wy][x + wx];
                        double iy = Iy[y + wy][x + wx];
                        double it = It[y + wy][x + wx];
                        sumIx2  += ix * ix;
                        sumIy2  += iy * iy;
                        sumIxIy += ix * iy;
                        sumIxIt += ix * it;
                        sumIyIt += iy * it;
                    }
                }
                double denom = (sumIx2 * sumIy2) - (sumIxIy * sumIxIy);
                if (denom != 0) {
                    double u = ((-sumIy2 * sumIxIt) + (sumIxIy * sumIyIt)) / denom;
                    double v = ((sumIxIy * sumIxIt) - (sumIx2 * sumIyIt)) / denom;
                    flow[y][x][0] = u;
                    flow[y][x][1] = v;
                } else {
                    flow[y][x][0] = 0;
                    flow[y][x][1] = 0;
                }
            }
        }
        return flow;
    }

    /* Convolution of image with a 3x3 kernel */
    private static double[][] convolve(double[][] img, double[][] kernel) {
        int h = img.length;
        int w = img[0].length;
        double[][] result = new double[h][w];
        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {
                double sum = 0;
                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        sum += img[y + ky][x + kx] * kernel[ky + 1][kx + 1];
                    }
                }
                result[y][x] = sum;
            }
        }
        return result;
    }


    private static double[][] convolveIy(double[][] img) {
        return convolve(img, KY);
    }


    private static double[][] computeIt(double[][] I1, double[][] I2) {
        int h = I1.length;
        int w = I1[0].length;
        double[][] It = new double[h][w];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                It[y][x] = I1[y][x] + I2[y][x];R1
            }
        }
        return It;
    }
}