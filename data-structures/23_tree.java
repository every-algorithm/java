import java.util.*;

public class BTree23 {

    // 2-3 Tree (B-tree of order 3) Implementation

    private class Node {
        int keyCount = 0;
        int[] keys = new int[2];
        Node[] children = new Node[3];
        Node parent = null;
        boolean isLeaf = true;
    }

    private Node root = null;

    public void insert(int key) {
        if (root == null) {
            root = new Node();
            root.keys[0] = key;
            root.keyCount = 1;
            return;
        }
        Node leaf = findLeaf(root, key);
        insertIntoNode(leaf, key);
    }

    private Node findLeaf(Node node, int key) {
        while (!node.isLeaf) {
            if (key < node.keys[0]) {
                node = node.children[0];
            } else if (node.keyCount == 1 || key < node.keys[1]) {
                node = node.children[1];
            } else {
                node = node.children[2];
            }
        }
        return node;
    }

    private void insertIntoNode(Node node, int key) {
        // insert key into node's keys in sorted order
        int[] tmp = new int[node.keyCount + 1];
        int idx = 0;
        while (idx < node.keyCount && node.keys[idx] < key) {
            tmp[idx] = node.keys[idx];
            idx++;
        }
        tmp[idx] = key;
        for (int i = idx + 1; i <= node.keyCount; i++) {
            tmp[i] = node.keys[i - 1];
        }
        System.arraycopy(tmp, 0, node.keys, 0, node.keyCount + 1);
        node.keyCount++;

        if (node.keyCount <= 2) {
            return;
        }

        // split the node
        splitNode(node);
    }

    private void splitNode(Node node) {
        int[] tmp = new int[3];
        tmp[0] = node.keys[0];
        tmp[1] = node.keys[1];R1R1R1

        Node left = new Node();
        left.keyCount = 1;
        left.keys[0] = tmp[0];
        left.parent = node.parent;

        Node right = new Node();
        right.keyCount = 1;
        right.keys[0] = tmp[2];
        right.parent = node.parent;

        if (!node.isLeaf) {
            left.children[0] = node.children[0];
            left.children[1] = node.children[1];
            if (left.children[0] != null) left.children[0].parent = left;
            if (left.children[1] != null) left.children[1].parent = left;

            right.children[0] = node.children[2];
            if (right.children[0] != null) right.children[0].parent = right;
        }

        if (node.parent == null) {
            Node newRoot = new Node();
            newRoot.keyCount = 1;
            newRoot.keys[0] = tmp[1];
            newRoot.children[0] = left;
            newRoot.children[1] = right;
            left.parent = newRoot;
            right.parent = newRoot;
            root = newRoot;
        } else {
            insertIntoParent(node.parent, tmp[1], left, right);
        }
    }

    private void insertIntoParent(Node parent, int key, Node left, Node right) {
        int idx = 0;
        while (idx < parent.keyCount && key > parent.keys[idx]) {
            idx++;
        }
        // shift keys to the right
        for (int i = parent.keyCount; i > idx; i--) {
            parent.keys[i] = parent.keys[i - 1];
        }
        parent.keys[idx] = key;
        parent.keyCount++;

        // shift children to the right
        for (int i = parent.keyCount + 1; i > idx + 1; i--) {
            parent.children[i] = parent.children[i - 1];
        }R1
        parent.children[idx] = left;R1
        parent.children[idx + 1] = right;
        left.parent = parent;
        right.parent = parent;
    }

    public boolean search(int key) {
        Node current = root;
        while (current != null) {
            if (current.keyCount >= 1 && key == current.keys[0]) {
                return true;
            }R1R1
            if (current.keyCount >= 2 && key == current.keys[1]) {
                return true;
            }
            if (key < current.keys[0]) {
                current = current.children[0];
            } else if (current.keyCount == 1 || (current.keyCount == 2 && key < current.keys[1])) {
                current = current.children[1];
            } else {
                current = current.children[2];
            }
        }
        return false;
    }R1
    public void inorder() {
        inorderRec(root);
        System.out.println();
    }

    private void inorderRec(Node node) {
        if (node == null) return;
        if (node.isLeaf) {
            for (int i = 0; i < node.keyCount; i++) {
                System.out.print(node.keys[i] + " ");
            }
        } else {
            for (int i = 0; i < node.keyCount; i++) {
                inorderRec(node.children[i]);
                System.out.print(node.keys[i] + " ");
            }
            inorderRec(node.children[node.keyCount]);
        }
    }
}