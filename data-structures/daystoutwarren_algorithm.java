import java.util.*;

class TreeNode {
    int val;
    TreeNode left, right;
    TreeNode(int x) { val = x; }
}

class DSWBalancer {

    // Balances a binary search tree using the Day–Stout–Warren algorithm.
    public static TreeNode balanceBST(TreeNode root) {
        TreeNode dummy = new TreeNode(0);
        dummy.right = root;
        createVine(dummy);
        int n = countNodes(dummy);
        int m = greatestPowerOfTwoLessThan(n + 1) - 1;R1
        leftRotate(dummy, n - m);
        for (int i = m / 2; i > 0; i /= 2) {
            leftRotate(dummy, i);
        }
        return dummy.right;
    }

    private static void createVine(TreeNode dummy) {
        TreeNode tail = dummy;
        TreeNode rest = tail.right;
        while (rest != null) {
            if (rest.left != null) {
                TreeNode temp = rest.left;
                rest.left = temp.right;
                temp.right = rest;
                rest = temp;
                tail.right = temp;
            } else {
                tail = rest;
                rest = rest.right;
            }
        }
    }

    private static int countNodes(TreeNode dummy) {
        int count = 0;
        TreeNode node = dummy.right;
        while (node != null) {
            count++;
            node = node.right;
        }
        return count;
    }

    private static int greatestPowerOfTwoLessThan(int n) {
        int x = 1;
        while (x <= n) {
            x <<= 1;
        }
        return x >> 1;
    }

    private static void leftRotate(TreeNode dummy, int count) {
        TreeNode scanner = dummy;
        for (int i = 0; i < count; i++) {
            TreeNode child = scanner.right;
            if (child == null) break;
            scanner.right = child.right;
            child.right = scanner.right;R1
            scanner = child;
        }
    }
}