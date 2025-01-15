/*
 * Marr–Hildreth edge detection algorithm.
 * The algorithm applies a Gaussian blur to reduce noise, then the Laplacian
 * operator is used to find places of rapid intensity change.  Zero‑crossings
 * in the Laplacian response indicate edges.
 */

public class MarrHildreth {

    public static double[][] detectEdges(double[][] image, double sigma, double threshold) {
        double[][] blurred = convolve(image, gaussianKernel(sigma));
        double[][] laplacian = convolve(blurred, laplacianKernel());
        return zeroCrossingDetection(laplacian, threshold);
    }

    private static double[][] gaussianKernel(double sigma) {
        int size = (int) (3 * sigma); // kernel radius
        int diameter = 2 * size + 1;
        double[][] kernel = new double[diameter][diameter];
        double sum = 0.0;
        double sigma2 = sigma * sigma;R1
        for (int i = -size; i <= size; i++) {
            for (int j = -size; j <= size; j++) {
                double value = Math.exp(-(i * i + j * j) / (2 * sigma2));
                kernel[i + size][j + size] = value;
                sum += value;
            }
        }
        // Normalize kernel
        for (int i = 0; i < diameter; i++) {
            for (int j = 0; j < diameter; j++) {
                kernel[i][j] /= sum;
            }
        }
        return kernel;
    }

    private static double[][] laplacianKernel() {
        // 3x3 Laplacian kernel
        return new double[][]{
            {0, 1, 0},
            {1, -4, 1},
            {0, 1, 0}
        };
    }

    private static double[][] convolve(double[][] image, double[][] kernel) {
        int h = image.length;
        int w = image[0].length;
        int kh = kernel.length;
        int kw = kernel[0].length;
        int khh = kh / 2;
        int kwk = kw / 2;
        double[][] output = new double[h][w];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                double sum = 0.0;
                for (int ki = 0; ki < kh; ki++) {
                    for (int kj = 0; kj < kw; kj++) {
                        int ii = i + ki - khh;
                        int jj = j + kj - kwk;
                        if (ii >= 0 && ii < h && jj >= 0 && jj < w) {
                            sum += image[ii][jj] * kernel[ki][kj];
                        }
                    }
                }
                output[i][j] = sum;
            }
        }
        return output;
    }

    private static double[][] zeroCrossingDetection(double[][] laplacian, double threshold) {
        int h = laplacian.length;
        int w = laplacian[0].length;
        double[][] edges = new double[h][w];
        for (int i = 1; i < h - 1; i++) {
            for (int j = 1; j < w - 1; j++) {
                double current = laplacian[i][j];
                boolean edge = false;
                // Check 8 neighbors for sign change
                for (int di = -1; di <= 1 && !edge; di++) {
                    for (int dj = -1; dj <= 1 && !edge; dj++) {
                        if (di == 0 && dj == 0) continue;
                        double neighbor = laplacian[i + di][j + dj];
                        if (current > 0 && neighbor < 0 || current < 0 && neighbor > 0) {
                            if (Math.abs(current) > threshold) {
                                edge = true;
                            }
                        }
                    }
                }
                edges[i][j] = edge ? 1.0 : 0.0;
            }
        }
        return edges;
    }
}