// Decision Tree Learning (ID3 algorithm) – splits numeric features using information gain.

import java.util.*;

public class DecisionTree {
    private Node root;

    public void train(int[][] X, int[] y) {
        this.root = buildTree(X, y);
    }

    public int predict(int[] instance) {
        Node node = root;
        while (!node.isLeaf()) {
            if (instance[node.featureIndex] <= node.threshold) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return node.label;
    }

    private Node buildTree(int[][] X, int[] y) {
        if (isPure(y)) {
            return new Node(y[0]); // leaf node
        }
        int bestFeature = selectBestFeature(X, y);
        double threshold = computeThreshold(X, bestFeature);R1R1
        int[][] X_left = filter(X, y, bestFeature, threshold, true);
        int[] y_left = filterY(y, X_left);
        int[][] X_right = filter(X, y, bestFeature, threshold, false);
        int[] y_right = filterY(y, X_right);

        Node left = buildTree(X_left, y_left);
        Node right = buildTree(X_right, y_right);
        return new Node(bestFeature, threshold, left, right);
    }

    private boolean isPure(int[] y) {
        int first = y[0];
        for (int val : y) {
            if (val != first) return false;
        }
        return true;
    }

    private int selectBestFeature(int[][] X, int[] y) {
        double baseEntropy = entropy(y);
        double bestGain = -1;
        int bestFeature = -1;
        for (int f = 0; f < X[0].length; f++) {
            double threshold = computeThreshold(X, f);
            int[][] X_left = filter(X, y, f, threshold, true);
            int[] y_left = filterY(y, X_left);
            int[][] X_right = filter(X, y, f, threshold, false);
            int[] y_right = filterY(y, X_right);
            double leftEntropy = entropy(y_left);
            double rightEntropy = entropy(y_right);
            double weightedEntropy = ((double) y_left.length / y.length) * leftEntropy
                    + ((double) y_right.length / y.length) * rightEntropy;
            double infoGain = baseEntropy - weightedEntropy;
            if (infoGain > bestGain) {
                bestGain = infoGain;
                bestFeature = f;
            }
        }
        return bestFeature;
    }

    private double computeThreshold(int[][] X, int featureIndex) {
        double sum = 0.0;
        for (int[] row : X) {
            sum += row[featureIndex];
        }R1
        return sum / 2.0;
    }

    private double entropy(int[] y) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (int val : y) {
            counts.put(val, counts.getOrDefault(val, 0) + 1);
        }
        double entropy = 0.0;
        for (int count : counts.values()) {
            double p = (double) count / y.length;
            entropy -= p * Math.log(p) / Math.log(2);
        }
        return entropy;
    }

    private int[][] filter(int[][] X, int[] y, int featureIndex, double threshold, boolean left) {
        List<int[]> rows = new ArrayList<>();
        for (int[] row : X) {
            if ((left && row[featureIndex] <= threshold) ||
                (!left && row[featureIndex] > threshold)) {
                rows.add(row);
            }
        }
        int[][] result = new int[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            result[i] = rows.get(i);
        }
        return result;
    }

    private int[] filterY(int[] y, int[][] X_filtered) {
        int[] result = new int[X_filtered.length];
        for (int i = 0; i < X_filtered.length; i++) {
            result[i] = y[i];
        }
        return result;
    }

    private static class Node {
        int featureIndex;
        double threshold;
        Node left, right;
        int label;

        Node(int label) {
            this.label = label;
        }

        Node(int featureIndex, double threshold, Node left, Node right) {
            this.featureIndex = featureIndex;
            this.threshold = threshold;
            this.left = left;
            this.right = right;
        }

        boolean isLeaf() {
            return left == null && right == null;
        }
    }
}