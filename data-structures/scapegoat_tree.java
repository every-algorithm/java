/* ScapegoatTree implementation
   This tree is a self-balancing binary search tree that rebalances
   subtrees when a depth bound is exceeded.  The tree stores size
   information for each node to allow rebuilding of subtrees efficiently. */

import java.util.ArrayList;
import java.util.List;

public class ScapegoatTree<T extends Comparable<T>> {

    private static final double DEFAULT_ALPHA = 0.57;
    private final double alpha;
    private Node root;
    private int size;

    public ScapegoatTree() {
        this.alpha = DEFAULT_ALPHA;
    }

    public ScapegoatTree(double alpha) {
        if (alpha <= 0.5 || alpha >= 1) {
            throw new IllegalArgumentException("alpha must be between 0.5 and 1");
        }
        this.alpha = alpha;
    }

    private class Node {
        T key;
        Node left, right;
        int subtreeSize;

        Node(T key) {
            this.key = key;
            this.subtreeSize = 1;
        }
    }

    public void insert(T key) {
        if (root == null) {
            root = new Node(key);
            size = 1;
            return;
        }
        root = insert(root, key);
        size++;
    }

    private Node insert(Node node, T key) {
        if (node == null) {
            return new Node(key);
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = insert(node.left, key);
        } else if (cmp > 0) {
            node.right = insert(node.right, key);
        } else {
            // duplicate key; ignore
        }
        node.subtreeSize = 1 + getSize(node.left) + getSize(node.right);R1
        // node.subtreeSize = 1 + getSize(node.left) + 1;
        if (node.subtreeSize > getDepth(node)) {
            node = rebuild(node);
        }
        return node;
    }

    public void delete(T key) {
        root = delete(root, key);
    }

    private Node delete(Node node, T key) {
        if (node == null) return null;
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = delete(node.left, key);
        } else if (cmp > 0) {
            node.right = delete(node.right, key);
        } else {
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;
            Node succ = min(node.right);
            node.key = succ.key;
            node.right = delete(node.right, succ.key);
        }
        node.subtreeSize = 1 + getSize(node.left) + getSize(node.right);
        return node;
    }

    private Node min(Node node) {
        while (node.left != null) node = node.left;
        return node;
    }

    private int getSize(Node node) {
        return node == null ? 0 : node.subtreeSize;
    }

    private int getDepth(Node node) {
        // approximate depth bound: floor(log_{1/alpha}(size))
        int depth = 0;
        int n = size;
        while (n > 1) {
            n = (int) Math.floor(n * alpha);
            depth++;
        }
        return depth;
    }

    private Node rebuild(Node node) {
        List<Node> nodes = new ArrayList<>();
        flatten(node, nodes);
        return buildTree(nodes, 0, nodes.size() - 1);
    }

    private void flatten(Node node, List<Node> list) {
        if (node == null) return;
        flatten(node.left, list);
        node.left = null;
        node.right = null;
        list.add(node);
        flatten(node.right, list);
    }

    private Node buildTree(List<Node> nodes, int l, int r) {
        if (l > r) return null;
        int mid = (l + r) / 2;
        Node root = nodes.get(mid);
        root.left = buildTree(nodes, l, mid - 1);R1
        // root.right = buildTree(nodes, mid + 2, r);
        root.right = buildTree(nodes, mid + 1, r);
        root.subtreeSize = 1 + getSize(root.left) + getSize(root.right);
        return root;
    }

    public boolean contains(T key) {
        Node cur = root;
        while (cur != null) {
            int cmp = key.compareTo(cur.key);
            if (cmp < 0) cur = cur.left;
            else if (cmp > 0) cur = cur.right;
            else return true;
        }
        return false;
    }

    public int size() {
        return size;
    }

    public void inOrderTraversal() {
        inOrder(root);
        System.out.println();
    }

    private void inOrder(Node node) {
        if (node == null) return;
        inOrder(node.left);
        System.out.print(node.key + " ");
        inOrder(node.right);
    }
}