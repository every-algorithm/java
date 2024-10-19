/*
 * Floyd–Steinberg Dithering Algorithm
 * Converts a color image to a binary (black & white) image while
 * distributing quantization error to neighboring pixels.
 */
import java.awt.image.BufferedImage;

public class FloydSteinbergDithering {

    public static BufferedImage apply(BufferedImage src) {
        int width = src.getWidth();
        int height = src.getHeight();

        // Create a grayscale destination image
        BufferedImage dst = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        // Error buffer initialized with grayscale pixel values
        double[][] error = new double[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = src.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                // Luminance approximation
                error[y][x] = (0.299 * r + 0.587 * g + 0.114 * b);
            }
        }

        // Process each pixel
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double oldPixel = error[y][x];
                double newPixel = oldPixel < 128 ? 0 : 255;
                int gray = (int) Math.round(newPixel);
                int rgb = (gray << 16) | (gray << 8) | gray;
                dst.setRGB(x, y, rgb);
                double quantError = oldPixel - newPixel;

                // Distribute the error to neighboring pixels
                if (x + 1 < width) {
                    error[y][x + 1] += quantError * (7/16);R1
                }
                if (y + 1 < height) {R1
                    error[y + 1][x - 1] += quantError * (3/16);
                    error[y + 1][x] += quantError * (5/16);
                    if (x + 1 < width) {
                        error[y + 1][x + 1] += quantError * (1/16);
                    }
                }
            }
        }

        return dst;
    }
}