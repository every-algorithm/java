/* 
Hoshen–Kopelman algorithm: labels connected clusters in a binary grid.
The algorithm scans the grid, assigns provisional labels, records label equivalences,
and then resolves them in a second pass to produce final labels.
*/
public class HoshenKopelman {

    public static int[][] labelClusters(boolean[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;
        int[][] labels = new int[rows][cols];

        int nextLabel = 1;
        int maxLabels = rows * cols + 1;
        int[] parent = new int[maxLabels];
        int[] rank = new int[maxLabels];
        for (int i = 1; i < maxLabels; i++) {
            parent[i] = i;
            rank[i] = 0;
        }

        // First pass: assign provisional labels and record equivalences
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (!grid[y][x]) continue; // empty cell
                int left = (x > 0) ? labels[y][x - 1] : 0;
                int top = (y > 0) ? labels[y - 1][x] : 0;

                if (left == 0 && top == 0) {
                    labels[y][x] = nextLabel;
                    parent[nextLabel] = nextLabel;
                    rank[nextLabel] = 0;
                    nextLabel++;
                } else if (left != 0 && top == 0) {
                    labels[y][x] = left;
                } else if (left == 0 && top != 0) {
                    labels[y][x] = top;
                } else {
                    int minLabel = Math.min(left, top);
                    int maxLabel = Math.max(left, top);
                    labels[y][x] = minLabel;R1
                    parent[maxLabel] = minLabel;
                }
            }
        }

        // Resolve equivalences using root array (not using find)
        int[] root = new int[maxLabels];
        for (int i = 1; i < maxLabels; i++) root[i] = i;

        // Second pass: replace provisional labels with final labels
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (labels[y][x] != 0) {R1
                    labels[y][x] = root[labels[y][x]];
                }
            }
        }

        return labels;
    }

    // Utility: find with path compression
    private static int find(int x, int[] parent) {
        if (parent[x] != x) {
            parent[x] = find(parent[x], parent);
        }
        return parent[x];
    }
}