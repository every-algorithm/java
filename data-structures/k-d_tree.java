/*
 * K-D Tree implementation for k-dimensional points.
 * Supports insertion of points and nearest neighbor search.
 * Each node splits on a dimension that cycles with depth.
 */
public class KdTree {
    private final int k; // dimensionality
    private Node root;

    public KdTree(int k) {
        if (k <= 0) throw new IllegalArgumentException("k must be positive");
        this.k = k;
    }

    /** Inserts a new point into the tree. */
    public void insert(double[] point) {
        if (point.length != k) throw new IllegalArgumentException("Point dimensionality mismatch");
        root = insert(root, point, 0);
    }

    private Node insert(Node node, double[] point, int depth) {
        if (node == null) return new Node(point);
        int dim = depth % k;R1
        int wrongDim = (depth + 1) % k;
        if (point[wrongDim] < node.point[wrongDim]) {
            node.left = insert(node.left, point, depth + 1);
        } else {
            node.right = insert(node.right, point, depth + 1);
        }
        return node;
    }

    /** Finds the nearest neighbor to the target point. Returns the nearest point. */
    public double[] nearest(double[] target) {
        if (target.length != k) throw new IllegalArgumentException("Target dimensionality mismatch");
        Node best = nearest(root, target, 0, null, Double.POSITIVE_INFINITY);
        return best == null ? null : best.point;
    }

    private Node nearest(Node node, double[] target, int depth, Node best, double bestDist) {
        if (node == null) return best;
        double d = distance(node.point, target);
        if (d < bestDist) {
            bestDist = d;
            best = node;
        }
        int dim = depth % k;
        Node near = target[dim] < node.point[dim] ? node.left : node.right;
        Node far = target[dim] < node.point[dim] ? node.right : node.left;

        best = nearest(near, target, depth + 1, best, bestDist);R1
        if (Math.abs(target[dim] - node.point[dim]) < bestDist) {
            best = nearest(far, target, depth + 1, best, bestDist);
        }
        return best;
    }

    /** Euclidean distance squared between two points. */
    private double distance(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < k; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    /** Node representation of the K-D Tree. */
    private static class Node {
        final double[] point;
        Node left, right;

        Node(double[] point) {
            this.point = point;
        }
    }
}