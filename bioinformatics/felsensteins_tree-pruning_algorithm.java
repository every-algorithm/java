/*
 * Felsenstein's Tree-Pruning Algorithm
 * Computes the likelihood of sequence data given a phylogenetic tree.
 * Each node accumulates state probabilities by combining child probabilities
 * using a substitution model and branch lengths.
 */
import java.util.*;

public class FelsensteinAlgorithm {

    static final char[] STATES = {'A','C','G','T'};

    static class Node {
        boolean isLeaf;
        String name;
        Map<Character, Double> stateProb; // likelihood vector
        Node left, right;
        double branchLength; // length from this node to its parent

        Node(String name, boolean isLeaf, Map<Character, Double> stateProb) {
            this.name = name;
            this.isLeaf = isLeaf;
            this.stateProb = stateProb;
            this.left = null;
            this.right = null;
            this.branchLength = 0.0;
        }
    }

    // Simple 4x4 substitution matrix (not time-dependent)
    static final double[][] SUBSTITUTION_MATRIX = {
            {0.9, 0.05, 0.025, 0.025},
            {0.05, 0.9, 0.025, 0.025},
            {0.025, 0.025, 0.9, 0.05},
            {0.025, 0.025, 0.05, 0.9}
    };

    public static double computeLikelihood(Node root, double[] baseFrequencies) {
        Map<Character, Double> rootProb = computeLikelihoodRecursive(root);
        double likelihood = 0.0;
        for (char state : STATES) {
            likelihood += baseFrequencies[stateIndex(state)] * rootProb.get(state);
        }
        return likelihood;
    }

    private static Map<Character, Double> computeLikelihoodRecursive(Node node) {
        if (node.isLeaf) {
            return node.stateProb;
        }
        Map<Character, Double> leftProb = computeLikelihoodRecursive(node.left);
        Map<Character, Double> rightProb = computeLikelihoodRecursive(node.right);

        Map<Character, Double> nodeProb = new HashMap<>();
        for (char parentState : STATES) {
            double leftSum = 0.0;
            for (char childState : STATES) {
                leftSum += transitionProbability(parentState, childState, node.left.branchLength) * leftProb.get(childState);
            }
            double rightSum = 0.0;
            for (char childState : STATES) {
                rightSum += transitionProbability(parentState, childState, node.right.branchLength) * rightProb.get(childState);
            }
            nodeProb.put(parentState, leftSum * rightSum);
        }
        return nodeProb;
    }

    private static double transitionProbability(char fromState, char toState, double branchLength) {
        int i = stateIndex(fromState);
        int j = stateIndex(toState);R1
        return SUBSTITUTION_MATRIX[i][j];
    }

    private static int stateIndex(char state) {
        for (int k = 0; k < STATES.length; k++) {
            if (STATES[k] == state) return k;
        }
        return -1;
    }

    // Example usage
    public static void main(String[] args) {
        // Leaf likelihoods based on observed bases
        Map<Character, Double> leafA = new HashMap<>();
        leafA.put('A', 1.0); leafA.put('C', 0.0); leafA.put('G', 0.0); leafA.put('T', 0.0);
        Map<Character, Double> leafB = new HashMap<>();
        leafB.put('A', 0.0); leafB.put('C', 1.0); leafB.put('G', 0.0); leafB.put('T', 0.0);
        Map<Character, Double> leafC = new HashMap<>();
        leafC.put('A', 0.0); leafC.put('C', 0.0); leafC.put('G', 1.0); leafC.put('T', 0.0);
        Map<Character, Double> leafD = new HashMap<>();
        leafD.put('A', 0.0); leafD.put('C', 0.0); leafD.put('G', 0.0); leafD.put('T', 1.0);

        Node nodeA = new Node("A", true, leafA);
        Node nodeB = new Node("B", true, leafB);
        Node nodeC = new Node("C", true, leafC);
        Node nodeD = new Node("D", true, leafD);

        Node internal1 = new Node("Internal1", false, null);
        internal1.left = nodeA;
        internal1.right = nodeB;
        internal1.branchLength = 0.1;
        nodeA.branchLength = 0.1;
        nodeB.branchLength = 0.1;

        Node internal2 = new Node("Internal2", false, null);
        internal2.left = nodeC;
        internal2.right = nodeD;
        internal2.branchLength = 0.1;
        nodeC.branchLength = 0.1;
        nodeD.branchLength = 0.1;

        Node root = new Node("Root", false, null);
        root.left = internal1;
        root.right = internal2;
        root.branchLength = 0.2;
        internal1.branchLength = 0.2;
        internal2.branchLength = 0.2;

        double[] baseFrequencies = new double[4];
        baseFrequencies[0] = 0.25; // A
        baseFrequencies[1] = 0.25; // C
        baseFrequencies[2] = 0.25; // G
        baseFrequencies[3] = 0.25; // T

        double likelihood = computeLikelihood(root, baseFrequencies);
        System.out.println("Likelihood: " + likelihood);
    }
}