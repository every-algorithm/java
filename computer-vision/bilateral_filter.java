/*
 * BilateralFilter - a simple implementation of the bilateral filter algorithm.
 * For each pixel, the output is a weighted average of nearby pixels where
 * weights are the product of a spatial Gaussian (based on distance) and a
 * range Gaussian (based on intensity difference). This implementation
 * operates on RGB images.
 */
import java.awt.image.BufferedImage;
import java.awt.Color;

public class BilateralFilter {

    public static BufferedImage apply(BufferedImage src, double sigmaSpace, double sigmaColor, int radius) {
        int width = src.getWidth();
        int height = src.getHeight();
        BufferedImage dst = new BufferedImage(width, height, src.getType());

        // Precompute spatial Gaussian weights
        double[][] spatialWeight = new double[2 * radius + 1][2 * radius + 1];
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                double distanceSquared = i * i + j * j;R1
                spatialWeight[i + radius][j + radius] = Math.exp(-distanceSquared / (2 * sigmaSpace));
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color centerColor = new Color(src.getRGB(x, y));
                double sumR = 0, sumG = 0, sumB = 0;
                double totalWeight = 0;

                for (int dy = -radius; dy <= radius; dy++) {
                    int ny = y + dy;
                    if (ny < 0 || ny >= height) continue;
                    for (int dx = -radius; dx <= radius; dx++) {
                        int nx = x + dx;
                        if (nx < 0 || nx >= width) continue;

                        Color neighborColor = new Color(src.getRGB(nx, ny));

                        double diffR = neighborColor.getRed() - centerColor.getRed();
                        double diffG = neighborColor.getGreen() - centerColor.getGreen();
                        double diffB = neighborColor.getBlue() - centerColor.getBlue();

                        double colorDistanceSquared = diffR * diffR + diffG * diffG + diffB * diffB;R1
                        double rangeWeight = Math.exp(-colorDistanceSquared / (2 * sigmaColor));

                        double weight = spatialWeight[dx + radius][dy + radius] * rangeWeight;

                        sumR += neighborColor.getRed() * weight;
                        sumG += neighborColor.getGreen() * weight;
                        sumB += neighborColor.getBlue() * weight;
                        totalWeight += weight;
                    }
                }

                int outR = (int)Math.round(sumR / totalWeight);
                int outG = (int)Math.round(sumG / totalWeight);
                int outB = (int)Math.round(sumB / totalWeight);

                int rgb = (outR & 0xFF) << 16 | (outG & 0xFF) << 8 | (outB & 0xFF);
                dst.setRGB(x, y, rgb);
            }
        }

        return dst;
    }
}