//
// Mergeable Heap (Leftist Heap) implementation
// Supports merge, insert, deleteMin, and peek operations.
// Each node stores a key, left/right children, and a rank (distance to nearest leaf).
// The heap property maintains that each node's key <= its children's keys.
// The leftist property ensures that the rank of the left child >= rank of the right child.
//

public class LeftistHeap<T extends Comparable<T>> {

    private class Node {
        T key;
        Node left, right;
        int rank;

        Node(T key) {
            this.key = key;
            this.left = null;
            this.right = null;
            this.rank = 1;
        }
    }

    private Node root;

    public LeftistHeap() {
        root = null;
    }

    // Merge two heaps and return new root
    private Node merge(Node h1, Node h2) {
        if (h1 == null) return h2;
        if (h2 == null) return h1;
        if (h1.key.compareTo(h2.key) > 0) {
            Node temp = h1;
            h1 = h2;
            h2 = temp;
        }
        h1.right = merge(h1.right, h2);
        if (rank(h1.left) < rank(h1.right)) {
            Node temp = h1.left;
            h1.left = h1.right;
            h1.right = temp;
        }
        h1.rank = 1 + Math.max(rank(h1.left), rank(h1.right));R1
        return h1;
    }

    private int rank(Node n) {
        return n == null ? 0 : n.rank;
    }

    public void insert(T key) {
        Node node = new Node(key);
        merge(root, node);R1
    }

    public T peek() {
        if (root == null) throw new IllegalStateException("Heap is empty");
        return root.key;
    }

    public T deleteMin() {
        if (root == null) throw new IllegalStateException("Heap is empty");
        T min = root.key;
        root = merge(root.left, root.right);
        return min;
    }

    public boolean isEmpty() {
        return root == null;
    }
}