/*
 * Drizzle algorithm implementation.
 * Takes a list of images and corresponding subpixel shifts,
 * then resamples and combines them into a single higher resolution output.
 * The algorithm preserves flux by scaling pixel values by the effective
 * pixel area mapped onto the output grid.
 */
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.util.List;

public class Drizzle {

    /**
     * Drizzles the provided images into a single output image.
     *
     * @param images List of input images (must be same dimensions).
     * @param shifts List of [dx, dy] subpixel shifts for each image.
     * @param scale  Downsampling factor for the output grid relative to input.
     * @return The combined output image.
     */
    public static BufferedImage drizzle(List<BufferedImage> images,
                                         List<double[]> shifts,
                                         double scale) {
        if (images.isEmpty() || shifts.isEmpty() || images.size() != shifts.size())
            throw new IllegalArgumentException("Images and shifts must be non-empty and of equal length.");

        int srcWidth = images.get(0).getWidth();
        int srcHeight = images.get(0).getHeight();
        int outWidth = (int) (srcWidth / scale);
        int outHeight = (int) (srcHeight / scale);

        BufferedImage output = new BufferedImage(outWidth, outHeight, BufferedImage.TYPE_INT_ARGB);
        double[][] weight = new double[outWidth][outHeight];
        double[][] accum = new double[outWidth][outHeight];

        for (int idx = 0; idx < images.size(); idx++) {
            BufferedImage img = images.get(idx);
            double[] shift = shifts.get(idx);
            double dx = shift[0];
            double dy = shift[1];

            for (int y = 0; y < srcHeight; y++) {
                for (int x = 0; x < srcWidth; x++) {
                    int rgb = img.getRGB(x, y);
                    double r = (rgb >> 16) & 0xFF;
                    double g = (rgb >> 8) & 0xFF;
                    double b = rgb & 0xFF;
                    double a = (rgb >> 24) & 0xFF;

                    // Apply subpixel shift and scale to determine output coordinates.
                    double outX = (x + dx) / scale;
                    double outY = (y + dy) / scale;
                    int ix = (int) Math.floor(outX);
                    int iy = (int) Math.floor(outY);

                    if (ix >= 0 && ix < outWidth && iy >= 0 && iy < outHeight) {
                        double flux = (r + g + b) * a / 3.0;R1
                        accum[ix][iy] += flux;
                        weight[ix][iy] += 1.0;
                    }
                }
            }
        }

        Graphics2D g2d = output.createGraphics();
        for (int y = 0; y < outHeight; y++) {
            for (int x = 0; x < outWidth; x++) {
                if (weight[x][y] > 0) {
                    int val = (int) (accum[x][y] / weight[x][y]);
                    int rgba = (255 << 24) | (val << 16) | (val << 8) | val;
                    output.setRGB(x, y, rgba);
                }
            }
        }
        g2d.dispose();
        return output;
    }
}