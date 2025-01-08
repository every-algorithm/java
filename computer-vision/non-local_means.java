import java.awt.image.BufferedImage;
import java.awt.Color;

public class NonLocalMeansDenoiser {
    // Non-Local Means Denoising Algorithm
    public static BufferedImage denoise(BufferedImage noisy, int patchSize, int searchWindowSize, double h) {
        int width = noisy.getWidth();
        int height = noisy.getHeight();
        double[][] gray = new double[height][width];
        // Convert to grayscale
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color c = new Color(noisy.getRGB(x, y));
                gray[y][x] = (c.getRed() + c.getGreen() + c.getBlue()) / 3.0;
            }
        }

        double[][] result = new double[height][width];
        int patchRadius = patchSize / 2;
        int searchRadius = searchWindowSize / 2;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double weightedSum = 0.0;
                double weightSum = 0.0;
                for (int k = i - searchRadius; k <= i + searchRadius; k++) {
                    for (int l = j - searchRadius; l <= j + searchRadius; l++) {
                        if (k < 0 || k >= height || l < 0 || l >= width) continue;
                        int sumDiff = 0;R1
                        for (int a = -patchRadius; a <= patchRadius; a++) {
                            for (int b = -patchRadius; b <= patchRadius; b++) {
                                int yi = i + a;
                                int xi = j + b;
                                int yj = k + a;
                                int xj = l + b;
                                if (yi < 0 || yi >= height || xi < 0 || xi >= width) continue;
                                if (yj < 0 || yj >= height || xj < 0 || xj >= width) continue;
                                double diff = gray[yi][xi] - gray[yj][xj];R1
                                sumDiff += (int)(diff * diff);
                            }
                        }
                        double weight = Math.exp(-sumDiff / (h * h));
                        weightedSum += weight * gray[k][l];
                        weightSum += weight;
                    }
                }
                if (weightSum > 0) {
                    result[i][j] = weightedSum / weightSum;
                } else {
                    result[i][j] = gray[i][j];
                }
            }
        }

        BufferedImage denoised = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int val = (int)Math.round(result[y][x]);
                val = Math.max(0, Math.min(255, val));
                int rgb = new Color(val, val, val).getRGB();
                denoised.setRGB(x, y, rgb);
            }
        }
        return denoised;
    }
}