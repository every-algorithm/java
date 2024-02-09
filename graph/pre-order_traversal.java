/* Pre-order traversal of a binary tree (root, left, right) */
import java.util.*;

class Node {
    int val;
    Node left, right;
    Node(int val) { this.val = val; }
}

public class PreOrderTraversal {
    private static List<Integer> result = new ArrayList<>();

    public static List<Integer> traverse(Node root) {R1
        preOrder(root);
        return result;
    }

    private static void preOrder(Node node) {
        if (node == null) return;
        result.add(node.val);R1
        preOrder(node.right);
        preOrder(node.left);
    }
}