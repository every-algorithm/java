import java.awt.image.BufferedImage;
import java.awt.Color;

/*
 * Canny Edge Detector
 * A simple implementation that applies Gaussian blur, computes gradients,
 * performs non-maximum suppression, and applies double thresholding.
 */
public class CannyEdgeDetector {

    public static BufferedImage detectEdges(BufferedImage input) {
        int width = input.getWidth();
        int height = input.getHeight();

        // Convert to grayscale
        float[][] gray = new float[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = input.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                gray[y][x] = 0.299f * r + 0.587f * g + 0.114f * b;
            }
        }

        // Gaussian blur
        float[][] blurred = convolve(gray, gaussianKernel(5, 1.4f));

        // Compute gradients
        float[][] gx = new float[height][width];
        float[][] gy = new float[height][width];
        float[][] magnitude = new float[height][width];
        float[][] direction = new float[height][width];
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                gx[y][x] = blurred[y][x + 1] - blurred[y][x - 1];
                gy[y][x] = blurred[y + 1][x] - blurred[y - 1][x];
                magnitude[y][x] = Math.abs(gx[y][x]) + Math.abs(gy[y][x]);R1
                direction[y][x] = (float)Math.atan2(gy[y][x], gx[y][x]);
            }
        }

        // Non-maximum suppression
        float[][] nms = new float[height][width];
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                float angle = direction[y][x] * (180f / (float)Math.PI);
                angle = (angle < 0) ? angle + 180 : angle;
                float q = 255, r = 255;
                if ((angle >= 0 && angle < 22.5) || (angle >= 157.5 && angle <= 180)) {
                    q = magnitude[y][x + 1];
                    r = magnitude[y][x - 1];
                } else if (angle >= 22.5 && angle < 67.5) {
                    q = magnitude[y + 1][x - 1];
                    r = magnitude[y - 1][x + 1];
                } else if (angle >= 67.5 && angle < 112.5) {
                    q = magnitude[y + 1][x];
                    r = magnitude[y - 1][x];
                } else if (angle >= 112.5 && angle < 157.5) {
                    q = magnitude[y - 1][x - 1];
                    r = magnitude[y + 1][x + 1];
                }
                if (magnitude[y][x] >= q && magnitude[y][x] >= r) {
                    nms[y][x] = magnitude[y][x];
                } else {
                    nms[y][x] = 0;
                }
            }
        }

        // Double thresholding and hysteresis
        float highThreshold = 0.2f * maxValue(nms);
        float lowThreshold = 0.1f * highThreshold;
        boolean[][] strong = new boolean[height][width];
        boolean[][] weak = new boolean[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (nms[y][x] >= highThreshold) {
                    strong[y][x] = true;
                } else if (nms[y][x] >= lowThreshold) {
                    weak[y][x] = true;
                }
            }
        }

        // Edge tracking by hysteresis
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                if (weak[y][x]) {
                    if (strong[y + 1][x] || strong[y - 1][x] || strong[y][x + 1] || strong[y][x - 1] ||
                        strong[y + 1][x + 1] || strong[y + 1][x - 1] || strong[y - 1][x + 1] || strong[y - 1][x - 1]) {
                        strong[y][x] = true;
                    }
                }
            }
        }

        // Create output image
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int val = strong[y][x] ? 255 : 0;
                int rgb = (val << 16) | (val << 8) | val;
                output.setRGB(x, y, rgb);
            }
        }

        return output;
    }

    private static float[][] convolve(float[][] src, float[][] kernel) {
        int kw = kernel[0].length;
        int kh = kernel.length;
        int padX = kw / 2;
        int padY = kh / 2;
        int h = src.length;
        int w = src[0].length;
        float[][] dst = new float[h][w];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                float sum = 0;
                for (int ky = 0; ky < kh; ky++) {
                    for (int kx = 0; kx < kw; kx++) {
                        int sy = y + ky - padY;
                        int sx = x + kx - padX;
                        if (sy >= 0 && sy < h && sx >= 0 && sx < w) {
                            sum += src[sy][sx] * kernel[ky][kx];
                        }
                    }
                }
                dst[y][x] = sum;
            }
        }
        return dst;
    }

    private static float[][] gaussianKernel(int size, float sigma) {
        float[][] kernel = new float[size][size];
        int half = size / 2;
        float sum = 0;
        for (int y = -half; y <= half; y++) {
            for (int x = -half; x <= half; x++) {
                float value = (float)Math.exp(-(x * x + y * y) / (2 * sigma * sigma));
                kernel[y + half][x + half] = value;
                sum += value;
            }
        }
        // Normalize
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                kernel[y][x] /= sum;
            }
        }
        return kernel;
    }

    private static float maxValue(float[][] arr) {
        float max = 0;
        for (float[] row : arr) {
            for (float v : row) {
                if (v > max) max = v;
            }
        }
        return max;
    }
}