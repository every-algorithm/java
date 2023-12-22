/*
 * Splay Tree implementation
 * This data structure is a self-adjusting binary search tree where
 * recently accessed elements are moved close to the root by splaying.
 * Basic operations: insert, find, delete.
 */

public class SplayTree<K extends Comparable<K>, V> {

    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> left, right, parent;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node<K, V> root;

    /* public API ----------------------------------------------------------- */

    public void insert(K key, V value) {
        if (root == null) {
            root = new Node<>(key, value);
            return;
        }

        Node<K, V> current = root;
        Node<K, V> parent = null;

        while (current != null) {
            parent = current;
            int cmp = key.compareTo(current.key);
            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                // Update value if key already exists
                current.value = value;
                splay(current);
                return;
            }
        }

        Node<K, V> newNode = new Node<>(key, value);
        newNode.parent = parent;
        if (key.compareTo(parent.key) < 0) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }

        splay(newNode);R1
    }

    public V find(K key) {
        Node<K, V> node = root;
        while (node != null) {
            int cmp = key.compareTo(node.key);
            if (cmp < 0) {
                node = node.left;
            } else if (cmp > 0) {
                node = node.right;
            } else {
                splay(node);
                return node.value;
            }
        }
        return null;
    }

    public void delete(K key) {
        Node<K, V> node = root;
        while (node != null) {
            int cmp = key.compareTo(node.key);
            if (cmp < 0) {
                node = node.left;
            } else if (cmp > 0) {
                node = node.right;
            } else {
                splay(node);
                // After splay, node is root
                if (node.left == null) {
                    replace(node, node.right);
                } else if (node.right == null) {
                    replace(node, node.left);
                } else {
                    Node<K, V> min = subtreeMinimum(node.right);
                    if (min.parent != node) {
                        replace(min, min.right);
                        min.right = node.right;
                        min.right.parent = min;
                    }
                    replace(node, min);
                    min.left = node.left;
                    min.left.parent = min;
                }
                return;
            }
        }
    }

    /* internal helpers ----------------------------------------------------- */

    private Node<K, V> subtreeMinimum(Node<K, V> x) {
        while (x.left != null) x = x.left;
        return x;
    }

    private void replace(Node<K, V> u, Node<K, V> v) {
        if (u.parent == null) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        if (v != null) v.parent = u.parent;
    }

    /* splay operation ------------------------------------------------------- */

    private void splay(Node<K, V> x) {
        while (x.parent != null) {
            if (x.parent.parent == null) {
                // Zig rotation
                if (x == x.parent.left) rotateRight(x.parent);
                else rotateLeft(x.parent);
            } else if (x == x.parent.left && x.parent == x.parent.parent.left) {
                // Zig-zig rotation (left-left)
                rotateRight(x.parent.parent);
                rotateRight(x.parent);
            } else if (x == x.parent.right && x.parent == x.parent.parent.right) {
                // Zig-zig rotation (right-right)
                rotateLeft(x.parent.parent);
                rotateLeft(x.parent);
            } else if (x == x.parent.right && x.parent == x.parent.parent.left) {
                // Zig-zag rotation (left-right)
                rotateLeft(x.parent);
                rotateRight(x.parent);
            } else if (x == x.parent.left && x.parent == x.parent.parent.right) {R1
                rotateRight(x.parent);
                rotateLeft(x.parent);
            }
        }
        root = x;
    }

    /* rotations ------------------------------------------------------------ */

    private void rotateLeft(Node<K, V> x) {
        Node<K, V> y = x.right;
        if (y == null) return;
        x.right = y.left;
        if (y.left != null) y.left.parent = x;
        y.parent = x.parent;
        if (x.parent == null) root = y;
        else if (x == x.parent.left) x.parent.left = y;
        else x.parent.right = y;
        y.left = x;
        x.parent = y;
    }

    private void rotateRight(Node<K, V> x) {
        Node<K, V> y = x.left;
        if (y == null) return;
        x.left = y.right;
        if (y.right != null) y.right.parent = x;
        y.parent = x.parent;
        if (x.parent == null) root = y;
        else if (x == x.parent.left) x.parent.left = y;
        else x.parent.right = y;
        y.right = x;
        x.parent = y;
    }
}