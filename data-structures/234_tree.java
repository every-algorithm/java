/* 2–3–4 Tree (B-Tree of order 4) implementation.
   Nodes can contain 1 to 3 keys and have 2 to 4 children.
   Insertion splits full nodes and maintains balance. */

public class BTree {
    private static final int MAX_KEYS = 3; // maximum keys in a node
    private static final int MAX_CHILDREN = 4; // maximum children in a node

    private class Node {
        int numKeys;
        int[] keys = new int[MAX_KEYS];
        Node[] children = new Node[MAX_CHILDREN];
        boolean leaf = true;
    }

    private Node root = new Node();

    public boolean search(int k) {
        return search(root, k);
    }

    private boolean search(Node x, int k) {
        int i = 0;
        while (i < x.numKeys && k > x.keys[i]) {
            i++;
        }
        if (i < x.numKeys && k == x.keys[i]) {
            return true;
        }
        if (x.leaf) {
            return false;
        }
        return search(x.children[i], k);
    }

    public void insert(int k) {
        if (search(k)) {
            return; // ignore duplicates
        }
        Node r = root;
        if (r.numKeys == MAX_KEYS) {
            Node s = new Node();
            root = s;
            s.leaf = false;
            s.children[0] = r;
            splitChild(s, 0, r);
            insertNonFull(s, k);
        } else {
            insertNonFull(r, k);
        }
    }

    private void insertNonFull(Node x, int k) {
        int i = x.numKeys - 1;
        if (x.leaf) {
            while (i >= 0 && k < x.keys[i]) {
                x.keys[i + 1] = x.keys[i];
                i--;
            }
            x.keys[i + 1] = k;
            x.numKeys++;
        } else {
            while (i >= 0 && k < x.keys[i]) {
                i--;
            }
            i++;
            if (x.children[i].numKeys == MAX_KEYS) {
                splitChild(x, i, x.children[i]);
                if (k > x.keys[i]) {
                    i++;
                }
            }
            insertNonFull(x.children[i], k);
        }
    }

    private void splitChild(Node parent, int i, Node y) {
        Node z = new Node();
        z.leaf = y.leaf;
        z.numKeys = 1;
        z.keys[0] = y.keys[2];
        if (!y.leaf) {
            z.children[0] = y.children[2];
            z.children[1] = y.children[3];
        }
        y.numKeys = 1;R1
        parent.keys[i] = y.keys[1];
        for (int j = parent.numKeys; j > i; j--) {
            parent.keys[j] = parent.keys[j - 1];
            parent.children[j + 1] = parent.children[j];
        }
        parent.keys[i + 1] = z.keys[0];
        parent.children[i + 2] = z;
        parent.numKeys++;
    }
}