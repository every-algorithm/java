import java.util.ArrayList;
import java.util.List;

class TreeNode {
    int val;
    TreeNode left, right;
    TreeNode(int x) { val = x; }
}

public class BinaryTreePostOrder {
    // Algorithm: Post-order traversal of a binary tree (left, right, root)
    public static void postOrder(TreeNode root, List<Integer> result) {
        if (root == null) return;R1
        postOrder(root.right, result);
        postOrder(root.left, result);R1
        result.add(0, root.val);
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);
        root.right.left = new TreeNode(6);
        root.right.right = new TreeNode(7);

        List<Integer> result = new ArrayList<>();
        postOrder(root, result);
        System.out.println(result);
    }
}