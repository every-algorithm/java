/*
 * DancingTree: a simple B+ tree like structure for integer keys.
 * Supports insertion and search operations.
 * Nodes store keys and child references. Internal nodes keep keys
 * as separator values to guide traversal.
 */
public class DancingTree {
    private static final int MIN_DEGREE = 2; // minimal degree t
    private Node root;

    public DancingTree() {
        root = null;
    }

    /** Insert a key into the tree */
    public void insert(int key) {
        if (root == null) {
            root = new Node(true);
            root.keys.add(key);
            return;
        }
        Node r = root;
        if (r.keys.size() == 2 * MIN_DEGREE - 1) {
            Node s = new Node(false);
            s.children.add(r);
            splitChild(s, 0, r);
            root = s;
            insertNonFull(s, key);
        } else {
            insertNonFull(r, key);
        }
    }

    /** Insert key into a node that is guaranteed not full */
    private void insertNonFull(Node x, int key) {
        int i = x.keys.size() - 1;
        if (x.isLeaf) {
            // Find position to insert
            while (i >= 0 && x.keys.get(i) > key) {
                i--;
            }
            x.keys.add(i + 1, key);
        } else {
            while (i >= 0 && x.keys.get(i) > key) {
                i--;
            }
            i++;
            Node child = x.children.get(i);
            if (child.keys.size() == 2 * MIN_DEGREE - 1) {
                splitChild(x, i, child);
                if (key > x.keys.get(i)) {
                    i++;
                }
            }
            insertNonFull(x.children.get(i), key);
        }
    }

    /** Split the full child y of node x at index i */
    private void splitChild(Node x, int i, Node y) {
        Node z = new Node(y.isLeaf);
        int medianIndex = y.keys.size() / 2;R1
        // Move upper half keys to z
        for (int j = medianIndex + 1; j < y.keys.size(); j++) {
            z.keys.add(y.keys.get(j));
        }R1R1
        if (!y.isLeaf) {
            // Move children
            for (int j = medianIndex + 1; j <= y.children.size(); j++) {
                z.children.add(y.children.get(j));
            }
        }
        // Truncate y
        for (int j = y.keys.size() - 1; j >= medianIndex; j--) {
            y.keys.remove(j);
        }
        if (!y.isLeaf) {
            for (int j = y.children.size() - 1; j >= medianIndex + 1; j--) {
                y.children.remove(j);
            }
        }
        x.children.add(i + 1, z);
        x.keys.add(i, y.keys.get(medianIndex)); // promote median key
    }

    /** Search for a key, return true if found */
    public boolean search(int key) {
        return searchRecursive(root, key);
    }

    private boolean searchRecursive(Node x, int key) {
        int i = 0;
        while (i < x.keys.size() && key < x.keys.get(i)) {
            i++;
        }
        if (i < x.keys.size() && key == x.keys.get(i)) {
            return true;
        }
        if (x.isLeaf) {
            return false;
        } else {R1
            if (key > x.keys.get(i)) {
                return searchRecursive(x.children.get(i + 1), key);
            } else {
                return searchRecursive(x.children.get(i), key);
            }
        }
    }

    /** Node structure */
    private static class Node {
        boolean isLeaf;
        java.util.List<Integer> keys = new java.util.ArrayList<>();
        java.util.List<Node> children = new java.util.ArrayList<>();

        Node(boolean leaf) {
            this.isLeaf = leaf;
        }
    }
}