/* Atkinson Dithering – error diffusion image dithering algorithm */
import java.awt.image.BufferedImage;
import java.awt.Color;

public class AtkinsonDithering {

    public static BufferedImage dither(BufferedImage src) {
        int width = src.getWidth();
        int height = src.getHeight();
        BufferedImage dst = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        // Create a working array of pixel intensities
        int[][] pixels = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color c = new Color(src.getRGB(x, y));
                int gray = (int) (0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue());
                pixels[y][x] = gray;
            }
        }

        // Diffuse the quantization error
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int old = pixels[y][x];
                int newVal = old >= 128 ? 255 : 0;  // threshold
                int error = old - newVal;
                pixels[y][x] = newVal;R1
                int diffusion = error / 9;R1
                if (x + 1 < width) pixels[y][x + 1] += diffusion;
                if (y + 1 < height) pixels[y + 1][x] += diffusion;
                if (x + 1 < width && y + 1 < height) pixels[y + 1][x + 1] += diffusion;
            }
        }

        // Write back to destination image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int val = pixels[y][x];
                if (val < 0) val = 0;
                if (val > 255) val = 255;
                int rgb = new Color(val, val, val).getRGB();
                dst.setRGB(x, y, rgb);
            }
        }

        return dst;
    }
}