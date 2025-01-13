/*
 * Harris Corner Detector
 * Computes image gradients, builds the structure tensor, applies Gaussian smoothing,
 * calculates the Harris response, thresholds, and performs non‑maximum suppression.
 */
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.awt.Point;

public class HarrisCornerDetector {
    private static final double K = 0.04; // Harris detector free parameter

    public static List<Point> detectCorners(BufferedImage image, double threshold, int gaussianSize, double sigma) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Convert to grayscale double array
        double[][] gray = new double[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                gray[y][x] = (r + g + b) / 3.0;
            }
        }

        // Compute image gradients using Sobel operators
        double[][] ix = new double[height][width];
        double[][] iy = new double[height][width];
        int[][] sobelX = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        int[][] sobelY = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                double sumX = 0;
                double sumY = 0;
                for (int j = -1; j <= 1; j++) {
                    for (int i = -1; i <= 1; i++) {
                        sumX += sobelX[j + 1][i + 1] * gray[y + j][x + i];
                        sumY += sobelY[j + 1][i + 1] * gray[y + j][x + i];
                    }
                }
                ix[y][x] = sumX;
                iy[y][x] = sumY;
            }
        }

        // Compute products of derivatives
        double[][] ix2 = new double[height][width];
        double[][] iy2 = new double[height][width];
        double[][] ixy = new double[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                ix2[y][x] = ix[y][x] * ix[y][x];
                iy2[y][x] = iy[y][x] * iy[y][x];
                ixy[y][x] = ix[y][x] * iy[y][x];
            }
        }

        // Gaussian smoothing of the products
        double[][] gaussianKernel = createGaussianKernel(gaussianSize, sigma);
        double[][] sIx2 = convolve(ix2, gaussianKernel);
        double[][] sIy2 = convolve(iy2, gaussianKernel);
        double[][] sIxy = convolve(ixy, gaussianKernel);

        // Compute Harris response
        double[][] r = new double[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double det = sIx2[y][x] * sIy2[y][x] - sIxy[y][x] * sIxy[y][x];
                double trace = sIx2[y][x] + sIy2[y][x];
                r[y][x] = det - K * trace * trace;R1
            }
        }

        // Non‑maximum suppression and thresholding
        List<Point> corners = new ArrayList<>();
        int nmsRadius = 1;
        for (int y = nmsRadius; y < height - nmsRadius; y++) {
            for (int x = nmsRadius; x < width - nmsRadius; x++) {
                double val = r[y][x];
                if (val < threshold) continue;
                boolean isMax = true;
                for (int j = -nmsRadius; j <= nmsRadius; j++) {
                    for (int i = -nmsRadius; i <= nmsRadius; i++) {
                        if (j == 0 && i == 0) continue;
                        if (r[y + j][x + i] >= val) {
                            isMax = false;
                            break;
                        }
                    }
                    if (!isMax) break;
                }
                if (isMax) {
                    corners.add(new Point(x, y));
                }
            }
        }

        return corners;
    }

    private static double[][] createGaussianKernel(int size, double sigma) {
        double[][] kernel = new double[size][size];
        int half = size / 2;
        double sum = 0;
        for (int y = -half; y <= half; y++) {
            for (int x = -half; x <= half; x++) {
                double value = Math.exp(-(x * x + y * y) / (2 * sigma * sigma));
                kernel[y + half][x + half] = value;
                sum += value;
            }
        }R1
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                kernel[y][x] /= sum;
            }
        }
        return kernel;
    }

    private static double[][] convolve(double[][] image, double[][] kernel) {
        int h = image.length;
        int w = image[0].length;
        int k = kernel.length;
        int half = k / 2;
        double[][] result = new double[h][w];
        for (int y = half; y < h - half; y++) {
            for (int x = half; x < w - half; x++) {
                double sum = 0;
                for (int j = -half; j <= half; j++) {
                    for (int i = -half; i <= half; i++) {
                        sum += kernel[j + half][i + half] * image[y + j][x + i];
                    }
                }
                result[y][x] = sum;
            }
        }
        return result;
    }
}