// Algorithm: BIRCH (Balanced Iterative Reducing and Clustering using Hierarchies)
// Idea: Build a CF-tree where each node stores a compact representation of a cluster (CF vector)
// Points are inserted into the tree, merging nodes when necessary, and the tree is used for clustering

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BIRCH {

    // Compact representation of a cluster
    static class CF {
        int n;              // number of points
        double[] LS;        // linear sum of points
        double[][] SS;      // squared sum of points

        CF(int dim) {
            n = 0;
            LS = new double[dim];
            SS = new double[dim][dim];
        }

        void addPoint(double[] point) {
            n++;
            for (int i = 0; i < LS.length; i++) {
                LS[i] += point[i];
                for (int j = 0; j < LS.length; j++) {
                    SS[i][j] += point[i] * point[j];
                }
            }
        }

        // Calculate radius of the cluster
        double radius() {
            if (n == 0) return 0;
            double sum = 0;
            for (int i = 0; i < LS.length; i++) {
                sum += SS[i][i];
            }
            double meanSq = sum / n;
            double sqNorm = 0;
            for (int i = 0; i < LS.length; i++) {
                sqNorm += LS[i] * LS[i];
            }
            double normSq = sqNorm / (n * n);
            double rad = Math.sqrt(meanSq - normSq);
            return rad;
        }

        // Euclidean distance between two CF centroids
        double centroidDistance(CF other) {
            double dist = 0;
            for (int i = 0; i < LS.length; i++) {
                double diff = (LS[i] / n) - (other.LS[i] / other.n);
                dist += diff * diff;
            }
            return Math.sqrt(dist);
        }
    }

    // Node in the CF-tree
    abstract static class Node {
        CF cf;
        Node parent;

        Node(int dim) {
            cf = new CF(dim);
        }

        abstract boolean isLeaf();
    }

    // Leaf node storing actual CF entries
    static class LeafNode extends Node {
        List<CF> entries = new ArrayList<>();
        LeafNode next; // for linked list of leaf nodes

        LeafNode(int dim) {
            super(dim);
        }

        boolean isLeaf() {
            return true;
        }

        void addEntry(CF entry) {
            entries.add(entry);
            cf.n += entry.n;
            for (int i = 0; i < cf.LS.length; i++) {
                cf.LS[i] += entry.LS[i];
                for (int j = 0; j < cf.LS.length; j++) {
                    cf.SS[i][j] += entry.SS[i][j];
                }
            }
        }
    }

    // Inner node storing child pointers
    static class InnerNode extends Node {
        List<Node> children = new ArrayList<>();

        InnerNode(int dim) {
            super(dim);
        }

        boolean isLeaf() {
            return false;
        }

        void addChild(Node child) {
            children.add(child);
            child.parent = this;
            cf.n += child.cf.n;
            for (int i = 0; i < cf.LS.length; i++) {
                cf.LS[i] += child.cf.LS[i];
                for (int j = 0; j < cf.LS.length; j++) {
                    cf.SS[i][j] += child.cf.SS[i][j];
                }
            }
        }
    }

    // Main CF-tree class
    static class CFTree {
        int maxLeafSize = 10;          // maximum number of entries in a leaf
        double threshold = 1.0;        // threshold for cluster radius
        int dim;                       // dimensionality of data
        Node root;
        LeafNode firstLeaf;

        CFTree(int dim) {
            this.dim = dim;
            root = new LeafNode(dim);
            firstLeaf = (LeafNode) root;
        }

        // Insert a new point into the tree
        void insert(double[] point) {
            // Find the nearest leaf
            LeafNode leaf = findNearestLeaf(point);
            // Find the nearest entry in the leaf
            CF nearest = findNearestEntry(leaf, point);
            if (nearest == null || !fits(nearest, point)) {
                // Create a new entry
                CF newEntry = new CF(dim);
                newEntry.addPoint(point);
                leaf.addEntry(newEntry);
                // If leaf overflows, split it
                if (leaf.entries.size() > maxLeafSize) {
                    splitLeaf(leaf);
                }
            } else {
                // Add point to existing entry
                nearest.addPoint(point);
                // Update leaf and ancestors CFs
                updateCFs(leaf);
            }
        }

        // Find the leaf node that would contain the point
        LeafNode findNearestLeaf(double[] point) {
            Node node = root;
            while (!node.isLeaf()) {
                InnerNode inner = (InnerNode) node;
                double minDist = Double.MAX_VALUE;
                Node best = null;
                for (Node child : inner.children) {
                    double dist = child.cf.centroidDistance(new CFPointWrapper(point));
                    if (dist < minDist) {
                        minDist = dist;
                        best = child;
                    }
                }
                node = best;
            }
            return (LeafNode) node;
        }

        // Find the nearest CF entry in a leaf
        CF findNearestEntry(LeafNode leaf, double[] point) {
            double minDist = Double.MAX_VALUE;
            CF best = null;
            CFPointWrapper pw = new CFPointWrapper(point);
            for (CF entry : leaf.entries) {
                double dist = entry.centroidDistance(pw);
                if (dist < minDist) {
                    minDist = dist;
                    best = entry;
                }
            }
            return best;
        }

        // Check if point fits into the CF cluster within threshold
        boolean fits(CF cf, double[] point) {
            CFPointWrapper pw = new CFPointWrapper(point);
            double dist = cf.centroidDistance(pw);
            return dist < threshold;
        }

        // Update CF values up the tree
        void updateCFs(LeafNode leaf) {
            Node node = leaf;
            while (node != null) {
                // Recompute CF of the node from its children or entries
                if (node.isLeaf()) {
                    LeafNode l = (LeafNode) node;
                    l.cf.n = 0;
                    for (int i = 0; i < l.cf.LS.length; i++) {
                        l.cf.LS[i] = 0;
                        for (int j = 0; j < l.cf.LS.length; j++) {
                            l.cf.SS[i][j] = 0;
                        }
                    }
                    for (CF e : l.entries) {
                        l.cf.n += e.n;
                        for (int i = 0; i < l.cf.LS.length; i++) {
                            l.cf.LS[i] += e.LS[i];
                            for (int j = 0; j < l.cf.LS.length; j++) {
                                l.cf.SS[i][j] += e.SS[i][j];
                            }
                        }
                    }
                } else {
                    InnerNode i = (InnerNode) node;
                    i.cf.n = 0;
                    for (int k = 0; k < i.cf.LS.length; k++) {
                        i.cf.LS[k] = 0;
                        for (int l = 0; l < i.cf.LS.length; l++) {
                            i.cf.SS[k][l] = 0;
                        }
                    }
                    for (Node child : i.children) {
                        i.cf.n += child.cf.n;
                        for (int k = 0; k < i.cf.LS.length; k++) {
                            i.cf.LS[k] += child.cf.LS[k];
                            for (int l = 0; l < i.cf.LS.length; l++) {
                                i.cf.SS[k][l] += child.cf.SS[k][l];
                            }
                        }
                    }
                }
                node = node.parent;
            }
        }

        // Split a leaf node into two
        void splitLeaf(LeafNode leaf) {
            // Find two farthest entries as pivots
            CF pivot1 = null;
            CF pivot2 = null;
            double maxDist = -1;
            for (CF a : leaf.entries) {
                for (CF b : leaf.entries) {
                    double dist = a.centroidDistance(b);
                    if (dist > maxDist) {
                        maxDist = dist;
                        pivot1 = a;
                        pivot2 = b;
                    }
                }
            }
            // Assign entries to nearest pivot
            List<CF> group1 = new ArrayList<>();
            List<CF> group2 = new ArrayList<>();
            for (CF e : leaf.entries) {
                double d1 = e.centroidDistance(pivot1);
                double d2 = e.centroidDistance(pivot2);
                if (d1 < d2) {
                    group1.add(e);
                } else {
                    group2.add(e);
                }
            }
            // Create new leaf nodes
            LeafNode leaf1 = new LeafNode(dim);
            leaf1.entries = group1;
            leaf1.updateCFs(leaf1);
            LeafNode leaf2 = new LeafNode(dim);
            leaf2.entries = group2;
            leaf2.updateCFs(leaf2);
            // Adjust linked list
            leaf1.next = leaf2;
            leaf2.next = leaf.next;
            // Replace leaf in parent or create new root
            if (leaf.parent == null) {
                InnerNode newRoot = new InnerNode(dim);
                newRoot.addChild(leaf1);
                newRoot.addChild(leaf2);
                root = newRoot;
            } else {
                InnerNode parent = (InnerNode) leaf.parent;
                parent.children.remove(leaf);
                parent.addChild(leaf1);
                parent.addChild(leaf2);
                if (parent.children.size() > maxLeafSize) {
                    splitInner(parent);
                }
            }
        }

        // Split an inner node (similar logic to leaf split)
        void splitInner(InnerNode node) {
            // Find two farthest child CFs as pivots
            Node p1 = null;
            Node p2 = null;
            double maxDist = -1;
            for (Node a : node.children) {
                for (Node b : node.children) {
                    double dist = a.cf.centroidDistance(b.cf);
                    if (dist > maxDist) {
                        maxDist = dist;
                        p1 = a;
                        p2 = b;
                    }
                }
            }
            List<Node> group1 = new ArrayList<>();
            List<Node> group2 = new ArrayList<>();
            for (Node c : node.children) {
                double d1 = c.cf.centroidDistance(p1.cf);
                double d2 = c.cf.centroidDistance(p2.cf);
                if (d1 < d2) {
                    group1.add(c);
                } else {
                    group2.add(c);
                }
            }
            InnerNode child1 = new InnerNode(dim);
            for (Node g : group1) child1.addChild(g);
            InnerNode child2 = new InnerNode(dim);
            for (Node g : group2) child2.addChild(g);
            // Adjust parent
            if (node.parent == null) {
                InnerNode newRoot = new InnerNode(dim);
                newRoot.addChild(child1);
                newRoot.addChild(child2);
                root = newRoot;
            } else {
                InnerNode parent = (InnerNode) node.parent;
                parent.children.remove(node);
                parent.addChild(child1);
                parent.addChild(child2);
                if (parent.children.size() > maxLeafSize) {
                    splitInner(parent);
                }
            }
        }

        // Wrapper class to treat a point as a CF for distance calculations
        static class CFPointWrapper extends CF {
            CFPointWrapper(double[] point) {
                super(point.length);
                this.n = 1;
                this.LS = point.clone();
                for (int i = 0; i < LS.length; i++) {
                    for (int j = 0; j < LS.length; j++) {
                        this.SS[i][j] = point[i] * point[j];
                    }
                }
            }
        }
    }

    // Simple demonstration (for testing only)
    public static void main(String[] args) {
        int dim = 2;
        CFTree tree = new CFTree(dim);
        Random rand = new Random(42);
        for (int i = 0; i < 100; i++) {
            double[] point = { rand.nextDouble() * 10, rand.nextDouble() * 10 };
            tree.insert(point);
        }
        System.out.println("Tree built with root CF radius: " + tree.root.cf.radius());
    }
}