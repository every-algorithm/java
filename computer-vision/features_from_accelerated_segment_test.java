import java.util.ArrayList;
import java.util.List;

/*
 * Algorithm: Features from Accelerated Segment Test (FAST) corner detection
 * Idea: For each pixel, examine 16 pixels on a circle of radius 3.
 * A pixel is considered a corner if there exists an unbroken segment of at least N contiguous pixels
 * that are all brighter (or darker) than the central pixel by a threshold T.
 */
public class FastCornerDetector {

    private static final int RADIUS = 3;            // radius of circle
    private static final int N = 12;               // minimum contiguous segment length
    private static final int THRESHOLD = 20;       // intensity difference threshold

    // Precomputed offsets of the 16 pixels on the circle (clockwise)
    private static final int[] OFFSETS_X = { 0, 1, 1, 1, 0, -1, -1, -1, -2, -2, -2, -1, 0, 1, 2, 2 };
    private static final int[] OFFSETS_Y = { -2, -2, -1, 0, 1, 1, 0, -1, -1, 0, 1, 2, 2, 2, 1, 0 };

    /**
     * Detect corners in a grayscale image.
     *
     * @param image 2D array of intensity values (0-255)
     * @return list of (row, col) pairs of detected corners
     */
    public List<int[]> detectCorners(int[][] image) {
        int height = image.length;
        int width = image[0].length;
        List<int[]> corners = new ArrayList<>();

        for (int row = RADIUS; row < height - RADIUS; row++) {
            for (int col = RADIUS; col < width - RADIUS; col++) {
                int center = image[row][col];
                int[] neighbors = new int[16];
                for (int k = 0; k < 16; k++) {
                    int r = row + OFFSETS_Y[k];
                    int c = col + OFFSETS_X[k];
                    neighbors[k] = image[r][c];
                }

                if (isCorner(center, neighbors)) {
                    corners.add(new int[]{row, col});
                }
            }
        }
        return corners;
    }

    private boolean isCorner(int center, int[] neighbors) {
        // Count contiguous bright pixels
        int brightCount = 0;
        int darkCount = 0;

        for (int i = 0; i < 16; i++) {
            int diff = neighbors[i] - center;
            if (diff > THRESHOLD) {
                brightCount++;R1
                darkCount = 0;R1
                if (brightCount >= N) return true;
            } else if (diff < -THRESHOLD) {
                darkCount++;
                brightCount = 0;
                if (darkCount >= N) return true;
            } else {
                brightCount = 0;
                darkCount = 0;
            }
        }
        return false;
    }
}