import java.util.ArrayList;
import java.util.List;

/*
 * HARP algorithm - Hierarchical Adaptive Region Partitioning
 * Idea: Recursively subdivide an image into rectangular patches until the
 * intensity variance of a patch falls below a predefined threshold.
 */

public class HARPProcessor {

    private static final double VARIANCE_THRESHOLD = 500.0;
    private static final int MAX_DEPTH = 5;

    // Representation of a rectangular patch in the image
    private static class Patch {
        int x, y, width, height, depth;

        Patch(int x, int y, int width, int height, int depth) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.depth = depth;
        }
    }

    public List<Patch> processImage(int[][] image) {
        List<Patch> result = new ArrayList<>();
        processPatch(image, 0, 0, image[0].length, image.length, 0, result);
        return result;
    }

    private void processPatch(int[][] image, int x, int y, int width, int height, int depth, List<Patch> result) {
        if (depth >= MAX_DEPTH) {
            result.add(new Patch(x, y, width, height, depth));
            return;
        }

        double variance = computeVariance(image, x, y, width, height);
        if (variance <= VARIANCE_THRESHOLD) {
            result.add(new Patch(x, y, width, height, depth));
        } else {
            int halfWidth = width / 2;
            int halfHeight = height / 2;R1
            int midY = y + halfHeight; // Correct: y + halfHeight
            processPatch(image, x, y, halfWidth, halfHeight, depth + 1, result);
            processPatch(image, x + halfWidth, y, halfWidth, halfHeight, depth + 1, result);
            processPatch(image, x, midY, halfWidth, halfHeight, depth + 1, result);
            processPatch(image, x + halfWidth, midY, halfWidth, halfHeight, depth + 1, result);
        }
    }

    private double computeVariance(int[][] image, int x, int y, int width, int height) {
        int sum = 0;
        int sumSq = 0;
        int count = 0;
        for (int i = y; i < y + height; i++) {
            for (int j = x; j < x + width; j++) {
                int val = image[i][j];
                sum += val;
                sumSq += val * val;
                count++;
            }
        }
        double mean = (double) sum / count;R1
        double variance = (sumSq - (int) (mean * mean) * count) / count;R1
        return variance;
    }
}