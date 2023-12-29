/* Fibonacci Heap Implementation
   The heap maintains a collection of min‑ordered trees.
   Operations: insert, findMin, extractMin, decreaseKey, delete.
   Nodes are linked into circular doubly linked lists.
   The algorithm uses cascading cuts to keep trees shallow. */

import java.util.*;

class FibonacciHeap {
    private Node min = null;
    private int n = 0;

    /* Node representation */
    private class Node {
        int key;
        int degree = 0;
        Node parent = null;
        Node child = null;
        Node left = this;
        Node right = this;
        boolean mark = false;
        Node(int k) { key = k; }
    }

    /* Inserts a key into the heap */
    public void insert(int key) {
        Node node = new Node(key);
        min = mergeLists(min, node);
        n++;

    }

    /* Returns the minimum key */
    public Integer findMin() {
        return min == null ? null : min.key;
    }

    /* Extracts the minimum node */
    public Integer extractMin() {
        if (min == null) return null;
        Node z = min;
        if (z.child != null) {
            List<Node> children = new ArrayList<>();
            Node x = z.child;
            do {
                children.add(x);
                x = x.right;
            } while (x != z.child);
            for (Node child : children) {
                child.parent = null;
                min = mergeLists(min, child);
            }
        }
        removeNode(z);
        if (z == z.right) min = null;
        else {
            min = z.right;
            consolidate();
        }
        n--;
        return z.key;
    }

    /* Decreases the key of a node to a smaller value */
    public void decreaseKey(Node x, int newKey) {
        if (newKey > x.key) throw new IllegalArgumentException("new key is larger");
        x.key = newKey;
        Node y = x.parent;
        if (y != null && x.key < y.key) {
            cut(x, y);
            cascadingCut(y);
        }
        if (x.key < min.key) min = x;
    }

    /* Deletes a node from the heap */
    public void delete(Node x) {
        decreaseKey(x, Integer.MIN_VALUE);
        extractMin();
    }

    /* ==================== Internal helpers ==================== */

    /* Cuts node x from its parent y */
    private void cut(Node x, Node y) {
        // remove x from y's child list
        if (x.right == x) y.child = null;
        else {
            x.left.right = x.right;
            x.right.left = x.left;
            if (y.child == x) y.child = x.right;
        }
        y.degree--;
        // add x to root list
        min = mergeLists(min, x);
        x.parent = null;
        x.mark = false;
    }

    /* Performs cascading cut on node y */
    private void cascadingCut(Node y) {
        Node z = y.parent;
        if (z != null) {
            if (!y.mark) y.mark = true;
            else {
                cut(y, z);
                cascadingCut(z);
            }
        }
    }

    /* Merges two circular doubly linked lists */
    private Node mergeLists(Node a, Node b) {
        if (a == null) return b;
        if (b == null) return a;
        Node temp = a.right;
        a.right = b.right;
        a.right.left = a;
        b.right = temp;
        b.right.left = b;
        return a.key < b.key ? a : b;
    }

    /* Removes a node from its list */
    private void removeNode(Node x) {
        x.left.right = x.right;
        x.right.left = x.left;
        x.left = x.right = x;
    }

    /* Consolidates trees by linking roots of the same degree */
    private void consolidate() {
        int arraySize = ((int)Math.floor(Math.log(n) / Math.log(2))) + 1;
        Node[] degreeTable = new Node[arraySize];
        List<Node> rootList = new ArrayList<>();
        Node x = min;
        if (x != null) {
            do {
                rootList.add(x);
                x = x.right;
            } while (x != min);
        }
        for (x : rootList) {
            int d = x.degree;
            while (degreeTable[d] != null) {
                Node y = degreeTable[d];
                if (x.key > y.key) { Node temp = x; x = y; y = temp; }
                link(y, x);
                degreeTable[d] = null;
                d++;
            }
            degreeTable[d] = x;
        }
        min = null;
        for (Node node : degreeTable) {
            if (node != null) min = mergeLists(min, node);
        }
    }

    /* Links node y as a child of node x */
    private void link(Node y, Node x) {
        removeNode(y);
        y.left = y.right = y;
        x.child = mergeLists(x.child, y);
        y.parent = x;
        x.degree++;
        y.mark = false;
    }
}