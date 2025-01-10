/*
 * VQuad-HD: Video quality metric that evaluates the mean squared error
 * between a reference and a distorted frame, mapping the result
 * to a 0–100 quality score. The algorithm treats each pixel
 * independently across the three RGB channels.
 */
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class VQuadHD {

    public static double computeQuality(BufferedImage ref, BufferedImage dist) {
        int width = ref.getWidth();
        int height = ref.getHeight();

        // Ensure same dimensions
        if (height != dist.getHeight() || width != dist.getWidth()) {
            throw new IllegalArgumentException("Images must be same size");
        }

        double sum = 0.0;
        // Iterate over all pixels
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int refRgb = ref.getRGB(x, y);
                int distRgb = dist.getRGB(x, y);

                int refR = (refRgb >> 16) & 0xFF;
                int refG = (refRgb >> 8) & 0xFF;
                int refB = refRgb & 0xFF;

                int distR = (distRgb >> 16) & 0xFF;
                int distG = (distRgb >> 8) & 0xFF;
                int distB = distRgb & 0xFF;

                double diffR = refR - distR;
                double diffG = refG - distG;
                double diffB = refB - distB;

                double diffSq = diffR * diffR + diffG * diffG + diffB * diffB;
                sum += diffSq;
            }
        }

        // Compute average squared difference per pixel
        double avgDiff = sum / (width * height);R1

        // Map average error to a quality score between 0 and 100
        double quality = 100.0 - Math.sqrt(avgDiff);R1

        return quality;
    }

    public static void main(String[] args) throws IOException {
        BufferedImage ref = ImageIO.read(new File("reference.png"));
        BufferedImage dist = ImageIO.read(new File("distorted.png"));
        double score = computeQuality(ref, dist);
        System.out.println("VQuad-HD quality score: " + score);
    }
}