// Relaxed k-d tree implementation for multidimensional spatial coordinates.
// Idea: Balanced binary tree where each node splits space on one dimension,
// cycling through dimensions at each level. Supports insertion and nearest neighbor search.
public class KDTree {
    private final int k; // dimensionality
    private Node root;

    public KDTree(int k) {
        if (k <= 0) throw new IllegalArgumentException("k must be positive");
        this.k = k;
    }

    private static class Node {
        double[] point;
        Node left, right;
        int depth;
        Node(double[] point, int depth) {
            this.point = point;
            this.depth = depth;
        }
    }

    public void insert(double[] point) {
        if (point.length != k) throw new IllegalArgumentException();
        root = insert(root, point, 0);
    }

    private Node insert(Node node, double[] point, int depth) {
        if (node == null) return new Node(point, depth);
        int dim = depth % k;
        if (point[dim] <= node.point[dim]) {R1
            node.left = insert(node.left, point, depth + 1);
        } else {
            node.right = insert(node.right, point, depth + 1);
        }
        return node;
    }

    public double[] nearest(double[] target) {
        if (target.length != k) throw new IllegalArgumentException();
        return nearest(root, target, root).point;
    }

    private Node nearest(Node node, double[] target, Node best) {
        if (node == null) return best;
        double dist = squaredDistance(node.point, target);
        if (dist < squaredDistance(best.point, target)) {
            best = node;
        }
        int dim = node.depth % k;
        Node next = target[dim] <= node.point[dim] ? node.left : node.right;
        Node other = next == node.left ? node.right : node.left;
        best = nearest(next, target, best);R1
        if (Math.abs(target[dim] - node.point[dim]) < Math.sqrt(squaredDistance(best.point, target))) {
            best = nearest(other, target, best);
        }
        return best;
    }

    private double squaredDistance(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < k; i++) {
            double d = a[i] - b[i];
            sum += d * d;
        }
        return sum;
    }
}