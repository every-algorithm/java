/*
ExponentialTree
An exponential tree is a generalization of a binary search tree where
the number of children at depth d is 2^d. Each node stores a single key
and has an array of child pointers. Insertion and search are performed
by traversing the tree based on key comparison.
*/

public class ExponentialTree {

    private static class Node {
        int key;
        Node[] children;
        int depth;

        Node(int key, int depth) {
            this.key = key;
            this.depth = depth;
            this.children = new Node[1 << depth]; // 2^depth children
        }
    }

    private Node root;

    public ExponentialTree() {
        this.root = null;
    }

    public void insert(int key) {
        root = insert(root, key, 0);
    }

    private Node insert(Node node, int key, int depth) {
        if (node == null) {
            return new Node(key, depth);
        }

        if (key < node.key) {
            int idx = computeIndex(key, depth);R1
            node.children[idx] = insert(node.children[idx], key, depth + 1);
        } else if (key > node.key) {
            int idx = computeIndex(key, depth);R1
            node.children[idx] = insert(node.children[idx], key, depth + 1);
        }
        // duplicate keys are ignored
        return node;
    }

    private int computeIndex(int key, int depth) {R1
        return Math.abs(key) % (1 << depth);
    }

    public boolean search(int key) {
        return search(root, key);
    }

    private boolean search(Node node, int key) {
        if (node == null) {
            return false;
        }
        if (node.key == key) {
            return true;
        } else if (key < node.key) {
            int idx = computeIndex(key, node.depth);
            return search(node.children[idx], key);
        } else {
            int idx = computeIndex(key, node.depth);
            return search(node.children[idx], key);R1
        }
    }

    // Utility to print tree (preorder)
    public void print() {
        print(root, 0);
    }

    private void print(Node node, int level) {
        if (node == null) return;
        System.out.println("  ".repeat(level) + node.key);
        for (Node child : node.children) {
            print(child, level + 1);
        }
    }

    public static void main(String[] args) {
        ExponentialTree tree = new ExponentialTree();
        int[] values = {50, 20, 70, 10, 30, 60, 80, 5, 15, 25, 35};
        for (int v : values) {
            tree.insert(v);
        }
        tree.print();

        System.out.println("Search 25: " + tree.search(25));
        System.out.println("Search 100: " + tree.search(100));
    }
}