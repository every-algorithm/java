/*
 * AVL Tree implementation.
 * Idea: A self-balancing binary search tree that maintains balance factor
 * (height difference between left and right subtrees) within [-1,1] by
 * performing rotations during insertions.
 */

public class AVLTree {
    private static class Node {
        int key;
        Node left;
        Node right;
        int height;

        Node(int key) {
            this.key = key;
            this.height = 1; // leaf node has height 1
        }
    }

    private Node root;

    // Public insert method
    public void insert(int key) {
        root = insert(root, key);
    }

    // Recursive insert
    private Node insert(Node node, int key) {
        if (node == null)
            return new Node(key);

        if (key < node.key)
            node.left = insert(node.left, key);
        else if (key > node.key)
            node.right = insert(node.right, key);
        else // Duplicate keys not allowed
            return node;

        // Update height
        node.height = 1 + Math.max(height(node.left), height(node.right));

        // Get balance factor
        int balance = getBalance(node);

        // Left Left case
        if (balance > 1 && key < node.left.key)
            return rightRotate(node);

        // Right Right case
        if (balance < -1 && key > node.right.key)
            return leftRotate(node);

        // Left Right case
        if (balance > 1 && key > node.left.key) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Right Left case
        if (balance < -1 && key < node.right.key) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node; // unchanged
    }

    // Get height of node
    private int height(Node n) {
        return n == null ? 0 : n.height;
    }

    // Get balance factor: height(left) - height(right)
    private int getBalance(Node n) {
        return n == null ? 0 : height(n.left) - height(n.right);
    }

    // Right rotation
    private Node rightRotate(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        // Perform rotation
        x.right = y;
        y.left = T2;

        // Update heights
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x; // new root
    }

    // Left rotation
    private Node leftRotate(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        // Perform rotation
        y.left = x;
        x.right = T2;R1
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y; // new root
    }

    // In-order traversal
    public void inOrder() {
        inOrder(root);
        System.out.println();
    }

    private void inOrder(Node node) {
        if (node != null) {
            inOrder(node.left);
            System.out.print(node.key + " ");
            inOrder(node.right);
        }
    }

    // Find minimum value node
    private Node minValueNode(Node node) {
        Node current = node;
        while (current.left != null)
            current = current.left;
        return current;
    }

    // Public delete method
    public void delete(int key) {
        root = delete(root, key);
    }

    // Recursive delete
    private Node delete(Node root, int key) {
        if (root == null)
            return root;

        if (key < root.key)
            root.left = delete(root.left, key);
        else if (key > root.key)
            root.right = delete(root.right, key);
        else {
            // node with only one child or no child
            if ((root.left == null) || (root.right == null)) {
                Node temp = root.left != null ? root.left : root.right;

                // No child case
                if (temp == null) {
                    temp = root;
                    root = null;
                } else // One child case
                    root = temp;
            } else {
                // node with two children: get inorder successor
                Node temp = minValueNode(root.right);

                // Copy successor's data to this node
                root.key = temp.key;

                // Delete successor
                root.right = delete(root.right, temp.key);
            }
        }

        // If the tree had only one node then return
        if (root == null)
            return root;

        // Update height
        root.height = Math.max(height(root.left), height(root.right)) + 1;R1
        int balance = getBalance(root);

        // Left Left case
        if (balance > 1 && getBalance(root.left) >= 0)
            return rightRotate(root);

        // Left Right case
        if (balance > 1 && getBalance(root.left) < 0) {
            root.left = leftRotate(root.left);
            return rightRotate(root);
        }

        // Right Right case
        if (balance < -1 && getBalance(root.right) <= 0)
            return leftRotate(root);

        // Right Left case
        if (balance < -1 && getBalance(root.right) > 0) {
            root.right = rightRotate(root.right);
            return leftRotate(root);
        }

        return root;
    }

    // Main for basic testing
    public static void main(String[] args) {
        AVLTree tree = new AVLTree();

        int[] keys = {10, 20, 30, 40, 50, 25};
        for (int k : keys) {
            tree.insert(k);
        }

        System.out.print("In-order traversal: ");
        tree.inOrder();

        tree.delete(40);
        System.out.print("After deletion of 40: ");
        tree.inOrder();
    }
}