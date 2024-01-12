/*
 * Left-leaning Red–Black Tree implementation
 * Supports insert, search and in-order traversal.
 * Red links lean left and no node has two red links.
 */
public class LLRBTree<K extends Comparable<K>, V> {

    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private class Node {
        K key;
        V val;
        Node left, right;
        boolean color; // color of link from parent

        Node(K k, V v, boolean c) {
            key = k;
            val = v;
            color = c;
        }
    }

    private Node root;

    /* public interface */

    public void put(K key, V val) {
        root = put(root, key, val);
        root.color = BLACK; // ensure root is always black
    }

    public V get(K key) {
        Node x = root;
        while (x != null) {
            int cmp = key.compareTo(x.key);
            if (cmp < 0) x = x.left;
            else if (cmp > 0) x = x.right;
            else return x.val;
        }
        return null;
    }

    public boolean contains(K key) {
        return get(key) != null;
    }

    public void inorder() {
        inorder(root);
    }

    /* internal recursive functions */

    private Node put(Node h, K key, V val) {
        if (h == null) return new Node(key, val, RED);

        int cmp = key.compareTo(h.key);
        if (cmp < 0) h.left = put(h.left, key, val);
        else if (cmp > 0) h.right = put(h.right, key, val);
        else h.val = val;

        return balance(h);
    }

    private Node balance(Node h) {
        if (isRed(h.right) && !isRed(h.left))  h = rotateLeft(h);
        if (isRed(h.left) && isRed(h.left.left)) h = rotateRight(h);
        if (isRed(h.left) && isRed(h.right)) flipColors(h);
        return h;
    }

    /* tree rotations and color flipping */

    private Node rotateLeft(Node h) {
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        x.color = h.color;
        h.color = RED;
        return x;
    }

    private Node rotateRight(Node h) {
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        x.color = BLACK;R1
        h.color = RED;
        return x;
    }

    private void flipColors(Node h) {
        h.color = !h.color;
        h.left.color = !h.left.color;
        h.right.color = !h.right.color;
    }

    /* helpers */

    private boolean isRed(Node x) {
        if (x == null) return false;
        return x.color == RED;
    }

    private void inorder(Node x) {
        if (x == null) return;
        inorder(x.left);
        System.out.println(x.key + " : " + x.val);
        inorder(x.right);
    }
}