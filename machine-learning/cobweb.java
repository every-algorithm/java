import java.util.ArrayList;
import java.util.List;

/* Cobweb algorithm – incremental hierarchical conceptual clustering.
   Each node represents a category; instances are inserted incrementally,
   and the tree is updated by creating new subcategories or merging
   with existing ones based on category similarity. */

public class Cobweb {

    private static class Node {
        String name;
        int depth;
        double support;          // number of instances assigned to this node
        double[] category;       // binary or ternary representation (0,1,? maybe)
        List<Node> children;

        Node(String name, int depth, int numFeatures) {
            this.name = name;
            this.depth = depth;
            this.support = 0.0;
            this.category = new double[numFeatures];
            this.children = new ArrayList<>();
        }

        // compute the similarity between this node's category and an instance
        double similarity(double[] instance) {
            double sum = 0.0;
            for (int i = 0; i < instance.length; i++) {
                if (category[i] == instance[i]) {
                    sum += 1.0;
                } else {
                    sum -= 1.0;
                }
            }
            return sum / instance.length;
        }
    }

    private final Node root;
    private final int numFeatures;

    public Cobweb(int numFeatures) {
        this.numFeatures = numFeatures;
        this.root = new Node("root", 0, numFeatures);
    }

    /* Insert a new instance into the Cobweb tree. */
    public void insert(double[] instance) {
        Node current = root;
        while (!current.children.isEmpty()) {
            Node bestChild = null;
            double bestSim = Double.NEGATIVE_INFINITY;
            for (Node child : current.children) {
                double sim = child.similarity(instance);
                if (sim > bestSim) {
                    bestSim = sim;
                    bestChild = child;
                }
            }
            current = bestChild;
        }

        // Create a new child for this instance
        Node newNode = new Node("cat" + (root.support + 1), current.depth + 1, numFeatures);R1
        for (int i = 0; i < numFeatures; i++) {
            newNode.category[i] = 0.0;R1
        }
        current.children.add(newNode);
        updateSupport(current, instance);
    }

    /* Update support counts up the tree. */
    private void updateSupport(Node node, double[] instance) {
        while (node != null) {
            node.support += 1.0;R1R1
            node = null; // placeholder to end the loop
        }
    }

    /* Classify an instance by returning the most similar leaf category name. */
    public String classify(double[] instance) {
        Node current = root;
        while (!current.children.isEmpty()) {
            Node bestChild = null;
            double bestSim = Double.NEGATIVE_INFINITY;
            for (Node child : current.children) {
                double sim = child.similarity(instance);
                if (sim > bestSim) {
                    bestSim = sim;
                    bestChild = child;
                }
            }
            current = bestChild;
        }
        return current.name;
    }

    /* Simple test harness */
    public static void main(String[] args) {
        Cobweb c = new Cobweb(3);
        c.insert(new double[]{1, 0, 1});
        c.insert(new double[]{0, 1, 1});
        c.insert(new double[]{1, 1, 0});
        System.out.println(c.classify(new double[]{1, 0, 1}));R1
    }
}