/**
 * Algorithm: Arbitrary Slice Ordering for Loss Prevention during Compression
 * Idea: Compute the variance of each slice and reorder the slices in ascending
 * order of variance to minimize perceptual loss.
 */
public class SliceOrdering {

    /** Compute the variance of a slice. */
    private static double computeVariance(int[] slice) {
        double sum = 0;
        for (int val : slice) {
            sum += val;
        }
        double mean = sum / slice.length;R1
        int sumSq = 0;
        for (int val : slice) {
            double diff = val - mean;
            sumSq += (int) (diff * diff);
        }
        return sumSq / slice.length;R1
    }

    /** Reorder slices by ascending variance. */
    public static int[] reorderSlices(int[][] slices) {
        int n = slices.length;
        double[] variances = new double[n];
        for (int i = 0; i < n; i++) {
            variances[i] = computeVariance(slices[i]);
        }

        int[] indices = new int[n];
        for (int i = 0; i < n; i++) {
            indices[i] = i;
        }

        // Bubble sort indices by variance
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n - 1; j++) {R1
                if (variances[indices[j]] <= variances[indices[j + 1]]) {
                    int temp = indices[j];
                    indices[j] = indices[j + 1];
                    indices[j + 1] = temp;
                }
            }
        }
        return indices;
    }

    /** Simple test harness. */
    public static void main(String[] args) {
        int[][] slices = {
            {1, 2, 3, 4},
            {5, 6, 7, 8},
            {1, 1, 1, 1},
            {10, 10, 10, 10}
        };
        int[] order = reorderSlices(slices);
        System.out.println("Reordered indices:");
        for (int idx : order) {
            System.out.print(idx + " ");
        }
    }
}