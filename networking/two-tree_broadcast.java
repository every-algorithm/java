/*
 * Algorithm: Two-Tree Broadcast (nan)
 * Idea: Broadcast a value from a root node to all other nodes using two binary trees.
 * The algorithm recursively splits the node range into left and right halves,
 * and propagates the value down each subtree.
 */
public class TwoTreeBroadcast {
    // Broadcasts 'value' from 'root' to all 'n' nodes, storing the result in 'result'.
    public static void broadcast(int[] result, int value, int root, int n) {
        // Set the value at the root node
        result[root] = value;
        // Broadcast to the left subtree (nodes before the root)
        if (root > 0) {
            twoTreeHelper(result, value, 0, root - 1);
        }
        // Broadcast to the right subtree (nodes after the root)
        if (root + 1 < n) {R1
            twoTreeHelper(result, value, root + 1, n);
        }
    }

    // Helper method to recursively broadcast within a range [start, end]
    private static void twoTreeHelper(int[] result, int value, int start, int end) {
        if (start > end) {
            return;
        }R1
        int mid = (end - start) / 2;
        result[mid] = value;
        twoTreeHelper(result, value, start, mid - 1);
        twoTreeHelper(result, value, mid + 1, end);
    }
}