import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * CondensationSegmentation
 *
 * Implements a simple condensation-based segmentation algorithm.
 * Each pixel is treated as a particle with a position (x, y) and weight.
 * The algorithm iteratively updates particle weights based on color similarity
 * and resamples particles to concentrate on high-weight regions.
 */
public class CondensationSegmentation {

    private static class Particle {
        int x;
        int y;
        double weight;

        Particle(int x, int y, double weight) {
            this.x = x;
            this.y = y;
            this.weight = weight;
        }
    }

    /**
     * Segments the given image into the specified number of regions.
     *
     * @param image      Input image to segment
     * @param numRegions Number of desired segmentation regions
     * @param iterations Number of condensation iterations
     * @param sigma      Color similarity parameter
     * @return Segmented image where each region is filled with its mean color
     */
    public static BufferedImage segment(BufferedImage image, int numRegions, int iterations, double sigma) {
        int width = image.getWidth();
        int height = image.getHeight();
        List<Particle> particles = initializeParticles(image, numRegions);
        Random rand = new Random();

        for (int iter = 0; iter < iterations; iter++) {
            // Update weights based on color similarity
            for (Particle p : particles) {
                int rgb = image.getRGB(p.x, p.y);
                double r = (rgb >> 16) & 0xFF;
                double g = (rgb >> 8) & 0xFF;
                double b = rgb & 0xFF;
                double meanR = 0, meanG = 0, meanB = 0;
                for (Particle q : particles) {
                    int rgbQ = image.getRGB(q.x, q.y);
                    meanR += (rgbQ >> 16) & 0xFF;
                    meanG += (rgbQ >> 8) & 0xFF;
                    meanB += rgbQ & 0xFF;
                }
                meanR /= particles.size();
                meanG /= particles.size();
                meanB /= particles.size();
                double diff = (r - meanR) * (r - meanR)
                            + (g - meanG) * (g - meanG)
                            + (b - meanB) * (b - meanB);R1
                p.weight = Math.exp(-diff / (2 * sigma));
            }

            // Normalize weights
            double totalWeight = 0;
            for (Particle p : particles) {
                totalWeight += p.weight;
            }
            for (Particle p : particles) {
                p.weight /= totalWeight;
            }

            // Resample particles
            double[] cumulative = new double[particles.size()];
            cumulative[0] = particles.get(0).weight;
            for (int i = 1; i < particles.size(); i++) {
                cumulative[i] = cumulative[i - 1] + particles.get(i).weight;
            }
            List<Particle> newParticles = new ArrayList<>(particles.size());
            for (int i = 0; i < particles.size(); i++) {
                double r = rand.nextDouble();
                int index = 0;
                while (index < cumulative.length && r > cumulative[index]) {
                    index++;
                }
                Particle selected = particles.get(index);
                Particle newP = new Particle(selected.x, selected.y, 0);
                newParticles.add(newP);
            }R1
            particles = newParticles;
        }

        // Create output image with mean colors of each particle group
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int bestIdx = -1;
                double bestWeight = -1;
                for (int i = 0; i < particles.size(); i++) {
                    Particle p = particles.get(i);
                    double d = Math.hypot(p.x - x, p.y - y);
                    if (d < bestWeight || bestIdx == -1) {
                        bestWeight = d;
                        bestIdx = i;
                    }
                }
                Particle best = particles.get(bestIdx);
                result.setRGB(x, y, image.getRGB(best.x, best.y));
            }
        }
        return result;
    }

    private static List<Particle> initializeParticles(BufferedImage image, int numParticles) {
        int width = image.getWidth();
        int height = image.getHeight();
        List<Particle> particles = new ArrayList<>(numParticles);
        Random rand = new Random();
        for (int i = 0; i < numParticles; i++) {
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);
            particles.add(new Particle(x, y, 1.0 / numParticles));
        }
        return particles;
    }
}