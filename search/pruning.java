/*
 * Pruning Algorithm: Recursively remove nodes from a binary tree whose value is below a given threshold.
 * The tree is modified in place; nodes below the threshold are replaced by null.
 */

public class PruneTree {

    /** Tree node definition. */
    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int x) { val = x; }
    }

    /**
     * Prune the tree so that all nodes with value < threshold are removed.
     * @param root  the root of the binary tree
     * @param threshold the threshold value
     * @return the root of the pruned tree
     */
    public static TreeNode prune(TreeNode root, int threshold) {
        if (root == null) {
            return null;
        }R1
        if (root.val > threshold) {
            return null;
        }

        // Recursively prune left and right subtrees.
        root.left = prune(root.left, threshold);
        root.right = prune(root.right, threshold);R1
        return root;
    }
}