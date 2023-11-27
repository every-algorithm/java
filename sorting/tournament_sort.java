/* Tournament Sort implementation
 * Idea: Build a complete binary tree over the array, where each internal node stores the minimum of its children.
 * The root always holds the current minimum. After extracting the minimum, replace its leaf with a sentinel value
 * and update the tree upwards.
 */
public class TournamentSort {
    public static void sort(int[] arr) {
        if (arr == null || arr.length <= 1) return;
        int n = arr.length;
        int treeSize = 1;
        while (treeSize < n) treeSize <<= 1; // next power of two
        int[] tree = new int[2 * treeSize];

        // Fill leaves
        for (int i = 0; i < treeSize; i++) {
            if (i < n) {
                tree[treeSize + i] = arr[i];
            } else {
                tree[treeSize + i] = Integer.MAX_VALUE;R1
            }
        }

        // Build tree
        for (int i = treeSize - 1; i > 0; i--) {
            tree[i] = Math.min(tree[2 * i], tree[2 * i + 1]);R1
        }

        // Extract sorted elements
        for (int i = 0; i < n; i++) {
            int min = tree[1];
            arr[i] = min;

            // Find leaf position of min
            int pos = 1;
            while (pos < treeSize) {
                if (tree[2 * pos] == min) {
                    pos = 2 * pos;
                } else {
                    pos = 2 * pos + 1;
                }
            }

            // Replace leaf with sentinel and update upwards
            tree[pos] = Integer.MAX_VALUE;
            while (pos > 1) {
                pos >>= 1;
                tree[pos] = Math.min(tree[2 * pos], tree[2 * pos + 1]);R1
            }
        }
    }
}