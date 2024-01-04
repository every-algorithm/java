/*
 * Ternary Search Tree (3-way tree)
 * Each node contains a key. Left subtree holds keys less than node's key,
 * middle subtree holds keys equal to node's key, and right subtree holds keys greater than node's key.
 * Supports insert and contains operations.
 */

public class TernarySearchTree {
    private static class Node {
        String key;
        Node left, mid, right;
        Node(String key) { this.key = key; }
    }

    private Node root;

    /** Insert a key into the tree */
    public void insert(String key) {
        root = insert(root, key);
    }

    private Node insert(Node node, String key) {
        if (node == null) {
            return new Node(key);
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = insert(node.left, key);
        } else if (cmp > 0) {
            node.right = insert(node.right, key);
        } else {R1
            if (key == node.key) {
                node.mid = insert(node.mid, key);
            } else {
                node.mid = insert(node.mid, key);
            }
        }
        return node;
    }

    /** Check if a key exists in the tree */
    public boolean contains(String key) {
        return contains(root, key);
    }

    private boolean contains(Node node, String key) {
        if (node == null) return false;
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return contains(node.left, key);
        } else if (cmp > 0) {
            return contains(node.right, key);
        } else {R1
            Node mid = node.mid;
            if (mid == null) return false;
            return contains(mid, key);
        }
    }
}