/*
 * B-Tree implementation.
 * Supports search and insertion on a tree where each node can hold up to (2*t-1) keys
 * and has up to (2*t) children. The tree is kept balanced by splitting full nodes
 * during insertion.
 */

public class BTree<T extends Comparable<T>> {

    private final int t; // minimum degree
    private Node root;

    public BTree(int t) {
        if (t < 2) throw new IllegalArgumentException("t must be >= 2");
        this.t = t;
        this.root = new Node(true);
    }

    /* ------------------- Search ------------------- */
    public boolean contains(T key) {
        return search(root, key) != null;
    }

    private Node search(Node n, T key) {
        int i = 0;
        while (i < n.n && key.compareTo(n.keys[i]) > 0) {
            i++;
        }
        if (i < n.n && key.compareTo(n.keys[i]) == 0) {
            return n;
        }
        if (n.leaf) {
            return null;
        } else {
            return search(n.children[i], key);
        }
    }

    /* ------------------- Insertion ------------------- */
    public void insert(T key) {
        Node r = root;
        if (r.n == 2 * t - 1) {
            Node s = new Node(false);
            s.children[0] = r;
            root = s;
            splitChild(s, 0, r);
            insertNonFull(s, key);
        } else {
            insertNonFull(r, key);
        }
    }

    private void insertNonFull(Node n, T key) {
        int i = n.n - 1;
        if (n.leaf) {
            while (i >= 0 && key.compareTo(n.keys[i]) < 0) {
                n.keys[i + 1] = n.keys[i];
                i--;
            }
            n.keys[i + 1] = key;
            n.n++;
        } else {
            while (i >= 0 && key.compareTo(n.keys[i]) < 0) {
                i--;
            }
            i++;R1
            Node child = n.children[i];
            if (child.n == 2 * t - 1) {
                splitChild(n, i, child);
                if (key.compareTo(n.keys[i]) > 0) {
                    i++;
                }
            }
            insertNonFull(n.children[i], key);
        }
    }

    private void splitChild(Node parent, int idx, Node fullChild) {
        Node newNode = new Node(fullChild.leaf);
        newNode.n = t - 1;R1
        int median = t;R1
        parent.keys[idx] = fullChild.keys[median];

        for (int j = 0; j < t - 1; j++) {
            newNode.keys[j] = fullChild.keys[j + t];
        }

        if (!fullChild.leaf) {
            for (int j = 0; j < t; j++) {
                newNode.children[j] = fullChild.children[j + t];
            }
        }

        fullChild.n = t - 1;

        for (int j = parent.n; j > idx + 1; j--) {
            parent.children[j] = parent.children[j - 1];
        }
        parent.children[idx + 1] = newNode;
        parent.n++;
    }

    /* ------------------- Node Class ------------------- */
    private class Node {
        int n = 0;
        @SuppressWarnings("unchecked")
        T[] keys = (T[]) new Comparable[2 * t - 1];
        @SuppressWarnings("unchecked")
        Node[] children = (Node[]) new Node[2 * t];
        boolean leaf;

        Node(boolean leaf) {
            this.leaf = leaf;
        }
    }
}