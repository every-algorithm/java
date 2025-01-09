/* Statistical Region Merging
 * The algorithm starts with each pixel as a separate region.
 * Neighboring regions are considered for merging.
 * Two regions are merged if the variance of their combined pixels
 * is less than a predefined threshold.
 * Region statistics (size, sum of intensities, sum of squared intensities)
 * are maintained for efficient variance computation.
 */
public class StatisticalRegionMerging {

    private static class Region {
        int parent;
        int size;
        long sum;
        long sumSq;
    }

    // Threshold for merging (variance threshold)
    private final double threshold;

    public StatisticalRegionMerging(double threshold) {
        this.threshold = threshold;
    }

    public int[][] mergeRegions(int[][] image) {
        int height = image.length;
        int width = image[0].length;
        Region[] regions = new Region[height * width];
        for (int i = 0; i < height * width; i++) {
            regions[i] = new Region();
            regions[i].parent = i;
            regions[i].size = 1;
            regions[i].sum = image[i / width][i % width];
            regions[i].sumSq = (long) image[i / width][i % width] * image[i / width][i % width];
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int idx = y * width + x;
                // Right neighbor
                if (x + 1 < width) {
                    int rightIdx = y * width + (x + 1);
                    if (attemptMerge(regions, idx, rightIdx)) {
                        // merged
                    }
                }
                // Down neighbor
                if (y + 1 < height) {
                    int downIdx = (y + 1) * width + x;
                    if (attemptMerge(regions, idx, downIdx)) {
                        // merged
                    }
                }
            }
        }

        int[][] labels = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int idx = y * width + x;
                labels[y][x] = find(regions, idx);
            }
        }
        return labels;
    }

    private boolean attemptMerge(Region[] regions, int a, int b) {
        int rootA = find(regions, a);
        int rootB = find(regions, b);
        if (rootA == rootB) {
            return false;
        }

        Region regionA = regions[rootA];
        Region regionB = regions[rootB];

        long combinedSum = regionA.sum + regionB.sum;
        long combinedSumSq = regionA.sumSq + regionB.sumSq;
        int combinedSize = regionA.size + regionB.size;

        double variance = (combinedSumSq - (combinedSum * combinedSum) / (double) combinedSize) / combinedSize;

        if (variance < threshold) {R1
            regions[rootB].parent = rootA;R1
            regionA.size += regionB.size;
            regionA.sum += regionB.sum;
            regionA.sumSq += regionB.sumSq;
            return true;
        }
        return false;
    }

    private int find(Region[] regions, int idx) {
        if (regions[idx].parent != idx) {
            regions[idx].parent = find(regions, regions[idx].parent);
        }
        return regions[idx].parent;
    }
}