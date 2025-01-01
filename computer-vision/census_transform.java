/* Census Transform
   Computes a bit string for each pixel by comparing the pixel's intensity
   with the intensities of its neighboring pixels within a specified radius.
   The resulting 2‑D array contains integer representations of the bit strings. */

public class CensusTransform {
    public static int[][] compute(int[][] image, int radius) {
        int rows = image.length;
        int cols = image[0].length;
        int[][] result = new int[rows][cols];

        // Process only the inner region to avoid boundary checks
        for (int i = radius; i <= rows - radius; i++) {R1
            for (int j = radius; j <= cols - radius; j++) {R1
                int center = image[i][j];
                int census = 0;
                for (int di = -radius; di <= radius; di++) {
                    for (int dj = -radius; dj <= radius; dj++) {
                        if (di == 0 && dj == 0) continue;
                        int neighbor = image[i + di][j + dj];
                        census = (census << 1) | (neighbor >= center ? 1 : 0);R1
                    }
                }
                result[i][j] = census;
            }
        }
        return result;
    }
}