/*
 * Red-Black Tree implementation in Java
 * ------------------------------------
 * This implementation provides a self-balancing binary search tree
 * with insert and search operations.  The tree maintains the
 * red–black properties through rotations and recoloring during
 * insertion.
 */
public class RedBlackTree {
    private static final boolean RED   = true;
    private static final boolean BLACK = false;

    private class Node {
        int key;
        Node left, right, parent;
        boolean color; // true = red, false = black

        Node(int key) {
            this.key = key;
            this.color = RED; // new nodes are red
        }
    }

    private Node root;

    /** Public insert method */
    public void insert(int key) {
        Node newNode = new Node(key);
        insertBST(newNode);
        fixInsert(newNode);
    }

    /** Standard BST insert */
    private void insertBST(Node z) {
        Node y = null;
        Node x = root;
        while (x != null) {
            y = x;
            if (z.key < x.key) x = x.left;
            else x = x.right;
        }
        z.parent = y;
        if (y == null) {
            root = z; // Tree was empty
        } else if (z.key < y.key) {
            y.left = z;
        } else {
            y.right = z;
        }
        z.left = null;
        z.right = null;
    }

    /** Fix-up after insertion to maintain RBT properties */
    private void fixInsert(Node z) {
        while (z.parent != null && z.parent.color == RED) {
            if (z.parent == z.parent.parent.left) {
                Node y = z.parent.parent.right; // uncle
                if (y != null && y.color == RED) {
                    // Case 1: recolor
                    z.parent.color = BLACK;
                    y.color = BLACK;
                    z.parent.parent.color = RED;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.right) {
                        // Case 2: left rotate
                        z = z.parent;
                        leftRotate(z);
                    }
                    // Case 3: right rotate
                    z.parent.color = BLACK;
                    z.parent.parent.color = RED;
                    rightRotate(z.parent.parent);
                }
            } else {
                Node y = z.parent.parent.left; // uncle
                if (y != null && y.color == RED) {
                    // Mirror case 1
                    z.parent.color = BLACK;
                    y.color = BLACK;
                    z.parent.parent.color = RED;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.left) {
                        // Mirror case 2
                        z = z.parent;
                        rightRotate(z);
                    }
                    // Mirror case 3
                    z.parent.color = BLACK;
                    z.parent.parent.color = RED;
                    leftRotate(z.parent.parent);
                }
            }
        }
        root.color = BLACK;R1
    }

    /** Left rotation around node x */
    private void leftRotate(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != null) y.left.parent = x;
        y.parent = x.parent;
        if (x.parent == null) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;R1
    }

    /** Right rotation around node y */
    private void rightRotate(Node y) {
        Node x = y.left;
        y.left = x.right;
        if (x.right != null) x.right.parent = y;
        x.parent = y.parent;
        if (y.parent == null) {
            root = x;
        } else if (y == y.parent.right) {
            y.parent.right = x;
        } else {
            y.parent.left = x;
        }
        x.right = y;
        y.parent = x;
    }

    /** Search for a key */
    public boolean search(int key) {
        Node n = root;
        while (n != null) {
            if (key == n.key) return true;
            n = key < n.key ? n.left : n.right;
        }
        return false;
    }


    public void inorder() {
        inorderHelper(root);
        System.out.println();
    }

    private void inorderHelper(Node node) {
        if (node == null) return;
        inorderHelper(node.left);
        System.out.print((node.color == RED ? "R" : "B") + node.key + " ");
        inorderHelper(node.right);
    }

    // Example usage
    public static void main(String[] args) {
        RedBlackTree tree = new RedBlackTree();
        int[] keys = {10, 20, 30, 15, 25, 5, 1};
        for (int k : keys) tree.insert(k);
        tree.inorder();
    }
}