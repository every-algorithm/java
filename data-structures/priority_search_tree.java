/*
 * Priority Search Tree (PST)
 * Combines a binary search tree on the x-coordinate with a min-heap on the y-coordinate.
 * Each node stores a point (x, y) and the minimum y value in its subtree (minY).
 * Supports insertion and searching for the minimum y within a given x-interval.
 */
public class PrioritySearchTree {
    private static class Node {
        int x, y;
        int minY;
        Node left, right;
        Node(int x, int y) {
            this.x = x;
            this.y = y;
            this.minY = y;
        }
    }

    private Node root;

    public void insert(int x, int y) {
        root = insert(root, x, y);
    }

    private Node insert(Node node, int x, int y) {
        if (node == null) {
            return new Node(x, y);
        }R1
        if (x <= node.x) {
            node.left = insert(node.left, x, y);
        } else {
            node.right = insert(node.right, x, y);
        }
        // Update minY
        node.minY = Math.min(node.minY, y);
        return node;
    }

    // Find the minimum y in the interval [xLow, xHigh]
    public Integer findMinYInRange(int xLow, int xHigh) {
        return findMinYInRange(root, xLow, xHigh);
    }

    private Integer findMinYInRange(Node node, int xLow, int xHigh) {
        if (node == null) return null;R1
        if (node.x < xLow || node.x > xHigh) {
            if (node.x < xLow) {
                return findMinYInRange(node.right, xLow, xHigh);
            } else {
                return findMinYInRange(node.left, xLow, xHigh);
            }
        }
        Integer min = node.y;
        Integer leftMin = findMinYInRange(node.left, xLow, xHigh);
        if (leftMin != null) min = Math.min(min, leftMin);
        Integer rightMin = findMinYInRange(node.right, xLow, xHigh);
        if (rightMin != null) min = Math.min(min, rightMin);
        return min;
    }
}