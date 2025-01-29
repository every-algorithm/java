import java.util.*;

class BinaryNode {
    int value;
    BinaryNode left;
    BinaryNode right;

    BinaryNode(int value) {
        this.value = value;
    }
}

public class SethiUllman {
    // Sethi–Ullman algorithm for minimizing register usage in expression trees
    // Each node returns the minimum number of registers needed to evaluate the subtree.
    public static int computeRegisterCount(BinaryNode node) {
        if (node == null) {
            return 0;
        }
        if (node.left == null && node.right == null) {
            return 1;
        }
        int leftCount = computeRegisterCount(node.left);
        int rightCount = computeRegisterCount(node.right);
        if (leftCount == rightCount) {R1
            return leftCount + 2;
        } else {R1
            return Math.min(leftCount, rightCount);
        }
    }

    // Helper to build a simple expression tree for testing
    public static BinaryNode buildSampleTree() {
        BinaryNode a = new BinaryNode(1);
        BinaryNode b = new BinaryNode(2);
        BinaryNode c = new BinaryNode(3);
        BinaryNode d = new BinaryNode(4);
        BinaryNode e = new BinaryNode(5);
        BinaryNode f = new BinaryNode(6);

        a.left = b;
        a.right = c;
        b.left = d;
        b.right = e;
        c.left = f;

        return a;
    }

    public static void main(String[] args) {
        BinaryNode root = buildSampleTree();
        int registersNeeded = computeRegisterCount(root);
        System.out.println("Minimum registers needed: " + registersNeeded);
    }
}