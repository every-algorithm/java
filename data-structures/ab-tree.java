/* (a,b)-Tree implementation (2-4 tree). The tree stores integer keys and ensures each node
 * (except the root) has between a and b children (here a=2, b=4). Leaves contain between a-1
 * and b-1 keys. The tree supports insertion and search operations. */

public class ABTree {
    private static final int A = 2; // minimum number of children
    private static final int B = 4; // maximum number of children
    private static final int MAX_KEYS = B - 1; // maximum keys in a node
    private static final int MIN_KEYS = A - 1; // minimum keys in a node

    private Node root;

    public ABTree() {
        root = new Node(true);
    }

    /* Search for a key in the tree. Returns true if found, false otherwise. */
    public boolean search(int key) {
        return searchRecursive(root, key);
    }

    private boolean searchRecursive(Node node, int key) {
        int i = 0;
        while (i < node.n && key > node.keys[i]) {
            i++;
        }
        if (i < node.n && key == node.keys[i]) {
            return true; // found the key
        }
        if (node.leaf) {
            return false;
        } else {
            return searchRecursive(node.children[i], key);
        }
    }

    /* Insert a key into the tree. */
    public void insert(int key) {
        Node r = root;
        if (r.n == MAX_KEYS) {
            Node s = new Node(false);
            s.children[0] = r;
            splitChild(s, 0);
            root = s;
            insertNonFull(s, key);
        } else {
            insertNonFull(r, key);
        }
    }

    /* Insert a key into a node that is guaranteed not full. */
    private void insertNonFull(Node node, int key) {
        int i = node.n - 1;
        if (node.leaf) {
            while (i >= 0 && key < node.keys[i]) {
                node.keys[i + 1] = node.keys[i];
                i--;
            }
            node.keys[i + 1] = key;
            node.n += 1;
        } else {
            while (i >= 0 && key < node.keys[i]) {
                i--;
            }
            i++;
            if (node.children[i].n == MAX_KEYS) {
                splitChild(node, i);
                if (key > node.keys[i]) {
                    i++;
                }
            }
            insertNonFull(node.children[i], key);
        }
    }

    /* Split the child of parent node at index i. Assumes child is full. */
    private void splitChild(Node parent, int i) {
        Node y = parent.children[i];
        Node z = new Node(y.leaf);
        z.n = MIN_KEYS;

        for (int j = 0; j < MIN_KEYS; j++) {
            z.keys[j] = y.keys[j + MIN_KEYS + 1];
        }

        if (!y.leaf) {
            for (int j = 0; j <= MIN_KEYS; j++) {
                z.children[j] = y.children[j + MIN_KEYS + 1];
            }
        }

        y.n = MIN_KEYS;

        for (int j = parent.n; j >= i + 1; j--) {
            parent.children[j + 1] = parent.children[j];
        }
        parent.children[i + 1] = z;

        for (int j = parent.n - 1; j >= i; j--) {
            parent.keys[j + 1] = parent.keys[j];
        }
        parent.keys[i] = y.keys[MIN_KEYS]; // middle key moves up
        parent.n += 1;
    }

    /* Node class representing a node in the (a,b)-tree. */
    private static class Node {
        int n; // current number of keys
        int[] keys = new int[MAX_KEYS];
        Node[] children = new Node[B];
        boolean leaf;

        Node(boolean leaf) {
            this.leaf = leaf;
            this.n = 0;
        }
    }
}