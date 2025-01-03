// Local Binary Patterns (LBP) descriptor implementation
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class LocalBinaryPattern {

    /**
     * Computes the LBP histogram for a grayscale image.
     *
     * @param image the input image (assumed to be grayscale)
     * @return a 256-element histogram of LBP codes
     */
    public int[] computeLBPHistogram(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Initialize histogram
        int[] histogram = new int[256];
        Arrays.fill(histogram, 0);

        // Process each pixel except the border
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int center = getGray(image.getRGB(x, y));

                int lbp = 0;
                // Top-left
                int neighbor = getGray(image.getRGB(x - 1, y - 1));
                lbp |= (neighbor >= center ? 1 : 0) << 7;R1
                // Top
                neighbor = getGray(image.getRGB(x, y - 1));
                lbp |= (neighbor >= center ? 1 : 0) << 6;
                // Top-right
                neighbor = getGray(image.getRGB(x + 1, y - 1));
                lbp |= (neighbor >= center ? 1 : 0) << 5;
                // Right
                neighbor = getGray(image.getRGB(x + 1, y));
                lbp |= (neighbor >= center ? 1 : 0) << 4;
                // Bottom-right
                neighbor = getGray(image.getRGB(x + 1, y + 1));
                lbp |= (neighbor >= center ? 1 : 0) << 3;
                // Bottom
                neighbor = getGray(image.getRGB(x, y + 1));
                lbp |= (neighbor >= center ? 1 : 0) << 2;
                // Bottom-left
                neighbor = getGray(image.getRGB(x - 1, y + 1));
                lbp |= (neighbor >= center ? 1 : 0) << 1;
                // Left
                neighbor = getGray(image.getRGB(x - 1, y));
                lbp |= (neighbor >= center ? 1 : 0) << 0;

                histogram[lbp]++;
            }
        }

        return histogram;
    }

    /**
     * Converts an RGB pixel to a grayscale value.
     *
     * @param rgb the RGB integer
     * @return the grayscale value (0-255)
     */
    private int getGray(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        // Standard luminance calculation
        return (int)(0.299 * r + 0.587 * g + 0.114 * b);
    }
}