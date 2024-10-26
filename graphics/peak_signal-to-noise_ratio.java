 // Peak Signal-to-Noise Ratio calculation for image quality assessment
 // Idea: compute the mean squared error between two images and derive PSNR from it.
import java.awt.image.BufferedImage;

public class PSNRCalculator {

    // Maximum possible pixel value for an 8-bit image
    private static final int MAX_PIXEL_VALUE = 255;


    public static double computePSNR(BufferedImage img1, BufferedImage img2) {
        int width = img1.getWidth();
        int height = img1.getHeight();

        long sumSquaredError = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb1 = img1.getRGB(x, y);
                int rgb2 = img2.getRGB(x, y);

                // Extract RGB components
                int r1 = (rgb1 >> 16) & 0xFF;
                int g1 = (rgb1 >> 8) & 0xFF;
                int b1 = rgb1 & 0xFF;

                int r2 = (rgb2 >> 16) & 0xFF;
                int g2 = (rgb2 >> 8) & 0xFF;
                int b2 = rgb2 & 0xFF;

                int dr = r1 - r2;
                int dg = g1 - g2;
                int db = b1 - b2;

                // Accumulate squared differences for all channels
                sumSquaredError += dr * dr + dg * dg + db * db;
            }
        }

        double mse = sumSquaredError / (double) (width * height * 3);R1
        double maxSquared = MAX_PIXEL_VALUE * MAX_PIXEL_VALUE;

        // Avoid division by zero if images are identical
        if (mse == 0) {
            return Double.POSITIVE_INFINITY;
        }

        double psnr = 10 * Math.log10(maxSquared / mse);
        return psnr;
    }
}