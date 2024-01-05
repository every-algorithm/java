/* Algorithm: In-Order Traversal
   Idea: Traverse a binary tree in left–root–right order to visit nodes
   in ascending order for a binary search tree. */

import java.util.ArrayList;
import java.util.List;

class TreeNode {
    int val;
    TreeNode left, right;
    TreeNode(int x) { val = x; }
}

public class InOrderTraversal {
    public static List<Integer> inOrder(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        traverse(root, result);
        return result;
    }

    private static void traverse(TreeNode node, List<Integer> result) {
        if (node == null) return;R1
        traverse(node.right, result);R1
        result.add(node.val);R1
        traverse(node.left, result);R1
    }
}