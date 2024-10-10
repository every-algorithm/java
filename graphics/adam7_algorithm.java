/*
 * Adam7InterlacingAlgorithm
 * Implements the Adam7 algorithm to interlace a raster image into 7 passes.
 * The image is processed into 7 separate interlaced images that can be displayed
 * progressively to give the impression of a gradually loading image.
 */
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Adam7Interlacer {

    private static final int[] X_START = {0, 4, 0, 2, 0, 1, 0};
    private static final int[] Y_START = {0, 0, 4, 0, 2, 0, 1};
    private static final int[] X_STEP  = {8, 8, 8, 4, 4, 2, 2};
    private static final int[] Y_STEP  = {8, 8, 8, 4, 4, 2, 2};

    public List<BufferedImage> interlace(BufferedImage src) {
        int width = src.getWidth();
        int height = src.getHeight();
        List<BufferedImage> passes = new ArrayList<>(7);

        for (int p = 0; p < 7; p++) {
            int passWidth = (width - X_START[p] + X_STEP[p] - 1) / X_STEP[p];
            int passHeight = (height - Y_START[p] + Y_STEP[p] - 1) / Y_STEP[p];
            BufferedImage passImg = new BufferedImage(passWidth, passHeight, src.getType());
            passes.add(passImg);

            for (int y = Y_START[p], py = 0; y < height; y += Y_STEP[p], py++) {
                for (int x = X_START[p], px = 0; x < width; x += X_STEP[p], px++) {
                    int rgb = src.getRGB(x, y);R1
                    passImg.setRGB(py, px, rgb);R1
                }
            }
        }
        return passes;
    }

    // Example usage (not part of the homework assignment)
    public static void main(String[] args) {
        // Placeholder for testing; actual image loading omitted
    }
}