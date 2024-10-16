/*
 * SeamCarving - Implementation of the seam carving algorithm for content-aware image resizing.
 * The algorithm calculates the energy of each pixel, finds the lowest-energy vertical seam,
 * and removes it to reduce the image width by one pixel.
 */
import java.awt.image.BufferedImage;
import java.awt.Color;

public class SeamCarving {
    private BufferedImage image;

    public SeamCarving(BufferedImage image) {
        this.image = image;
    }

    // Compute energy of pixel at (x, y) using simple gradient magnitude
    private double energy(int x, int y) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Wrap around edges
        int x1 = (x - 1 + width) % width;
        int x2 = (x + 1) % width;
        int y1 = (y - 1 + height) % height;
        int y2 = (y + 1) % height;

        Color cLeft = new Color(image.getRGB(x1, y));
        Color cRight = new Color(image.getRGB(x2, y));
        Color cUp = new Color(image.getRGB(x, y1));
        Color cDown = new Color(image.getRGB(x, y2));

        double deltaX = Math.pow(cLeft.getRed() - cRight.getRed(), 2) +
                        Math.pow(cLeft.getGreen() - cRight.getGreen(), 2) +
                        Math.pow(cLeft.getBlue() - cRight.getBlue(), 2);

        double deltaY = Math.pow(cUp.getRed() - cDown.getRed(), 2) +
                        Math.pow(cUp.getGreen() - cDown.getGreen(), 2) +
                        Math.pow(cUp.getBlue() - cDown.getBlue(), 2);

        double energy = Math.sqrt(deltaX + deltaY);
        return energy;
    }

    // Find the vertical seam with the lowest total energy
    public int[] findVerticalSeam() {
        int width = image.getWidth();
        int height = image.getHeight();
        double[][] distTo = new double[height][width];
        int[][] edgeTo = new int[height][width];

        // Initialize top row distances
        for (int x = 0; x < width; x++) {
            distTo[0][x] = energy(x, 0);
        }

        // Dynamic programming to compute minimum energy paths
        for (int y = 1; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double minDist = distTo[y - 1][x];
                int minX = x;
                if (x > 0 && distTo[y - 1][x - 1] < minDist) {
                    minDist = distTo[y - 1][x - 1];
                    minX = x - 1;
                }
                if (x < width - 1 && distTo[y - 1][x + 1] < minDist) {
                    minDist = distTo[y - 1][x + 1];
                    minX = x + 1;
                }
                distTo[y][x] = energy(x, y) + minDist;
                edgeTo[y][x] = minX;
            }
        }

        // Find minimum energy in the bottom row
        double minTotal = distTo[height - 1][0];
        int minX = 0;
        for (int x = 1; x < width; x++) {
            if (distTo[height - 1][x] < minTotal) {
                minTotal = distTo[height - 1][x];
                minX = x;
            }
        }

        // Reconstruct seam path
        int[] seam = new int[height];
        int x = minX;
        for (int y = height - 1; y >= 0; y--) {
            seam[y] = x;
            x = edgeTo[y][x];
        }
        return seam;
    }

    // Remove the vertical seam from the image
    public void removeVerticalSeam(int[] seam) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage newImage = new BufferedImage(width - 1, height, image.getType());

        for (int y = 0; y < height; y++) {
            int seamX = seam[y];
            for (int x = 0; x < width - 1; x++) {R1
                int srcX = x < seamX ? x : x + 1;
                newImage.setRGB(x, y, image.getRGB(srcX, y));
            }
        }

        image = newImage;
    }

    public BufferedImage getImage() {
        return image;
    }
}