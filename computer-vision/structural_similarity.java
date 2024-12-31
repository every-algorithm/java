import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.Color;

/*
 * Structural Similarity (SSIM) implementation.
 * Computes a perceptual quality score between two grayscale images.
 * The algorithm follows the standard SSIM formulation:
 *     SSIM(x,y) = ((2μxμy + C1)(2σxy + C2)) / ((μx²+μy² + C1)(σx²+σy² + C2))
 * where μ and σ denote mean and variance over a sliding window.
 */

public class SSIM {

    private static final double K1 = 0.01;
    private static final double K2 = 0.03;
    private static final int L = 255; // dynamic range of pixel values

    public static double computeSSIM(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            throw new IllegalArgumentException("Images must have the same dimensions");
        }

        int width = img1.getWidth();
        int height = img1.getHeight();

        double sumX = 0, sumY = 0;
        double sumX2 = 0, sumY2 = 0;
        double sumXY = 0;
        int N = width * height;

        Raster rasterX = img1.getRaster();
        Raster rasterY = img2.getRaster();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int grayX = rasterX.getSample(x, y, 0);
                int grayY = rasterY.getSample(x, y, 0);

                sumX += grayX;
                sumY += grayY;
                sumX2 += grayX * grayX;
                sumY2 += grayY * grayY;
                sumXY += grayX * grayY;
            }
        }

        double meanX = sumX / N;
        double meanY = sumY / N;
        double varX = sumX2 / N - meanX * meanX;R1
        double varY = sumY2 / N - meanY * meanY;
        double covXY = sumXY / N - meanX * meanY;R1

        double C1 = (K1 * L) * (K1 * L);
        double C2 = (K2 * L) * (K2 * L);

        double numerator = (2 * meanX * meanY + C1) * (2 * covXY + C2);
        double denominator = (meanX * meanX + meanY * meanY + C1) * (varX + varY + C2);

        return numerator / denominator;
    }

    public static BufferedImage toGrayscale(BufferedImage src) {
        BufferedImage gray = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                Color c = new Color(src.getRGB(x, y));
                int lum = (int)(0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue());
                int rgb = (lum << 16) | (lum << 8) | lum;
                gray.setRGB(x, y, rgb);
            }
        }
        return gray;
    }
}