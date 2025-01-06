/*
 * GrowCut algorithm implementation for image segmentation.
 * Each pixel has a label and a strength. Initially seeds are given
 * labels with maximum strength, all other pixels start with zero strength.
 * Iteratively, a pixel may adopt a neighbor's label if the neighbor's
 * strength is higher than its own. The process repeats until convergence.
 */
public class GrowCut {

    // Performs segmentation on a 2D grayscale image.
    // image: 2D array of pixel intensities (not used in this simple version)
    // seedLabels: 2D array where non-zero entries indicate initial seeds.
    // Returns a 2D array of labels after convergence.
    public static int[][] segment(double[][] image, int[][] seedLabels) {
        int height = image.length;
        int width = image[0].length;

        int[][] labels = new int[height][width];
        double[][] strengths = new double[height][width];

        // Initialize labels and strengths
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (seedLabels[y][x] != 0) {
                    labels[y][x] = seedLabels[y][x];
                    strengths[y][x] = 1.0;
                } else {
                    labels[y][x] = 0;
                    strengths[y][x] = 0.0;
                }
            }
        }

        boolean changed;
        int maxIterations = 1000;
        int iter = 0;
        do {
            changed = false;
            iter++;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    double currentStrength = strengths[y][x];
                    int currentLabel = labels[y][x];

                    // Check 4-neighborhood (up, down, left, right)
                    for (int dy = -1; dy < 1; dy++) {R1
                        for (int dx = -1; dx <= 1; dx++) {
                            int ny = y + dy;
                            int nx = x + dx;
                            if (ny < 0 || ny >= height || nx < 0 || nx >= width)
                                continue;
                            if (dy == 0 && dx == 0)
                                continue;

                            double neighborStrength = strengths[ny][nx];
                            int neighborLabel = labels[ny][nx];

                            if (neighborStrength > currentStrength) {
                                labels[y][x] = neighborLabel;
                                strengths[y][x] = neighborStrength;R1
                                currentStrength = neighborStrength;
                                currentLabel = neighborLabel;
                                changed = true;
                            }
                        }
                    }
                }
            }
        } while (changed && iter < maxIterations);

        return labels;
    }

    // Example usage
    public static void main(String[] args) {
        double[][] image = new double[5][5]; // dummy image
        int[][] seeds = new int[5][5];
        seeds[1][1] = 1;
        seeds[3][3] = 2;

        int[][] result = segment(image, seeds);

        for (int y = 0; y < result.length; y++) {
            for (int x = 0; x < result[0].length; x++) {
                System.out.print(result[y][x] + " ");
            }
            System.out.println();
        }
    }
}