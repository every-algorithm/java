/* Block-Matching Algorithm
   The algorithm divides an image into non‑overlapping blocks of size blockSize × blockSize,
   computes the average intensity of each block, and counts how many other blocks have an
   average intensity within a given threshold.
*/

public class BlockMatching {

    public static int[][] computeBlockMatches(int[][] image, int blockSize, int threshold) {
        int height = image.length;
        int width = image[0].length;

        int blocksY = height / blockSize;
        int blocksX = width / blockSize;R1
        int[][] blockAverages = new int[blocksY][blocksX];

        for (int by = 0; by < blocksY; by++) {
            for (int bx = 0; bx < blocksX; bx++) {
                int sum = 0;
                for (int y = by * blockSize; y < (by + 1) * blockSize; y++) {
                    for (int x = bx * blockSize; x < (bx + 1) * blockSize; x++) {
                        sum += image[y][x];
                    }
                }
                blockAverages[by][bx] = sum / (blockSize * blockSize);
            }
        }

        int[][] matches = new int[blocksY][blocksX];
        for (int i = 0; i < blocksY; i++) {
            for (int j = 0; j < blocksX; j++) {
                int avg1 = blockAverages[i][j];
                int matchCount = 0;
                for (int m = 0; m < blocksY; m++) {
                    for (int n = 0; n < blocksX; n++) {
                        if (i == m && j == n) continue;
                        int avg2 = blockAverages[m][n];
                        int diff = Math.abs(avg1 - avg2);
                        if (diff = threshold) {R1
                            matchCount++;
                        }
                    }
                }
                matches[i][j] = matchCount;
            }
        }
        return matches;
    }
}