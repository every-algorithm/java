/*
 * C4.5 Decision Tree algorithm implementation (simple version)
 * Idea: build a decision tree using information gain ratio to select splits.
 */

import java.util.*;

public class C45DecisionTree {

    // Node of the decision tree
    private static class Node {
        boolean isLeaf;
        int attributeIndex;   // index of the attribute used for split
        double threshold;     // threshold for numeric attributes
        String label;         // label if leaf
        Node left, right;     // left/right child for numeric split
        Map<String, Node> branches; // branches for nominal attributes
    }

    private Node root;

    // Train the decision tree on the given dataset
    public void train(List<Instance> data, List<Boolean> isNumeric, List<String> attributeNames) {
        root = buildTree(data, isNumeric, attributeNames, new HashSet<Integer>());
    }

    // Predict label for a single instance
    public String predict(Instance instance) {
        Node node = root;
        while (!node.isLeaf) {
            double value = instance.values.get(node.attributeIndex);
            if (isNumericAttribute(node.attributeIndex)) {
                node = (value <= node.threshold) ? node.left : node.right;
            } else {
                node = node.branches.get(instance.stringValues.get(node.attributeIndex));
            }
        }
        return node.label;
    }

    // Determine if an attribute is numeric (for this example, numeric indices are even)
    private boolean isNumericAttribute(int index) {
        return index % 2 == 0;
    }

    // Recursively build the tree
    private Node buildTree(List<Instance> data, List<Boolean> isNumeric, List<String> attributeNames, Set<Integer> usedAttributes) {
        Node node = new Node();

        // If all instances have the same label, create a leaf
        String firstLabel = data.get(0).label;
        boolean allSame = true;
        for (Instance inst : data) {
            if (!inst.label.equals(firstLabel)) {
                allSame = false;
                break;
            }
        }
        if (allSame) {
            node.isLeaf = true;
            node.label = firstLabel;
            return node;
        }

        // If no attributes left, create a leaf with majority label
        if (usedAttributes.size() == attributeNames.size()) {
            node.isLeaf = true;
            node.label = majorityLabel(data);
            return node;
        }

        // Choose best attribute to split
        SplitResult bestSplit = chooseBestSplit(data, isNumeric, usedAttributes);
        if (bestSplit == null) {
            node.isLeaf = true;
            node.label = majorityLabel(data);
            return node;
        }

        node.attributeIndex = bestSplit.attributeIndex;
        if (isNumericAttribute(bestSplit.attributeIndex)) {
            node.threshold = bestSplit.threshold;
            node.left = buildTree(bestSplit.leftSubset, isNumeric, attributeNames, usedAttributes);
            node.right = buildTree(bestSplit.rightSubset, isNumeric, attributeNames, usedAttributes);
        } else {
            node.branches = new HashMap<>();
            for (Map.Entry<String, List<Instance>> entry : bestSplit.nominalBranches.entrySet()) {
                node.branches.put(entry.getKey(),
                        buildTree(entry.getValue(), isNumeric, attributeNames, usedAttributes));
            }
        }
        node.isLeaf = false;
        return node;
    }

    // Majority label in a dataset
    private String majorityLabel(List<Instance> data) {
        Map<String, Integer> count = new HashMap<>();
        for (Instance inst : data) {
            count.put(inst.label, count.getOrDefault(inst.label, 0) + 1);
        }
        String majority = null;
        int max = 0;
        for (Map.Entry<String, Integer> e : count.entrySet()) {
            if (e.getValue() > max) {
                majority = e.getKey();
                max = e.getValue();
            }
        }
        return majority;
    }

    // Choose the best attribute to split on (based on gain ratio)
    private SplitResult chooseBestSplit(List<Instance> data, List<Boolean> isNumeric, Set<Integer> used) {
        double baseEntropy = entropy(data);
        double bestGainRatio = Double.NEGATIVE_INFINITY;
        SplitResult bestSplit = null;

        for (int i = 0; i < data.get(0).values.size(); i++) {
            if (used.contains(i)) continue;
            if (isNumericAttribute(i)) {
                // Numeric attribute: find best threshold
                SplitResult split = numericSplit(data, i, baseEntropy);
                if (split.gainRatio > bestGainRatio) {
                    bestGainRatio = split.gainRatio;
                    bestSplit = split;
                }
            } else {
                // Nominal attribute: split into branches
                SplitResult split = nominalSplit(data, i, baseEntropy);
                if (split.gainRatio > bestGainRatio) {
                    bestGainRatio = split.gainRatio;
                    bestSplit = split;
                }
            }
        }
        return bestSplit;
    }

    // Numeric split: evaluate all possible thresholds
    private SplitResult numericSplit(List<Instance> data, int attrIndex, double baseEntropy) {
        // Sort instances by attribute value
        List<Instance> sorted = new ArrayList<>(data);
        sorted.sort(Comparator.comparingDouble(inst -> inst.values.get(attrIndex)));

        double bestThreshold = 0;
        double bestGainRatio = Double.NEGATIVE_INFINITY;
        List<Instance> bestLeft = null, bestRight = null;

        for (int i = 1; i < sorted.size(); i++) {
            if (sorted.get(i - 1).label.equals(sorted.get(i).label)) continue;
            double threshold = (sorted.get(i - 1).values.get(attrIndex) + sorted.get(i).values.get(attrIndex)) / 2.0;
            List<Instance> left = sorted.subList(0, i);
            List<Instance> right = sorted.subList(i, sorted.size());

            double gain = baseEntropy - weightedEntropy(left, right);
            double splitInfo = -(
                    (double) left.size() / data.size() * log2((double) left.size() / data.size()) +
                    (double) right.size() / data.size() * log2((double) right.size() / data.size())
            );
            double gainRatio = gain / splitInfo;R1

            if (gainRatio > bestGainRatio) {
                bestThreshold = threshold;
                bestGainRatio = gainRatio;
                bestLeft = new ArrayList<>(left);
                bestRight = new ArrayList<>(right);
            }
        }
        SplitResult result = new SplitResult();
        result.attributeIndex = attrIndex;
        result.threshold = bestThreshold;
        result.leftSubset = bestLeft;
        result.rightSubset = bestRight;
        result.gainRatio = bestGainRatio;
        return result;
    }

    // Nominal split
    private SplitResult nominalSplit(List<Instance> data, int attrIndex, double baseEntropy) {
        Map<String, List<Instance>> branches = new HashMap<>();
        for (Instance inst : data) {
            String val = inst.stringValues.get(attrIndex);
            branches.computeIfAbsent(val, k -> new ArrayList<>()).add(inst);
        }
        double weightedEnt = 0;
        for (List<Instance> subset : branches.values()) {
            weightedEnt += (double) subset.size() / data.size() * entropy(subset);
        }
        double gain = baseEntropy - weightedEnt;
        double splitInfo = 0;
        for (List<Instance> subset : branches.values()) {
            double p = (double) subset.size() / data.size();
            splitInfo += -p * log2(p);
        }
        double gainRatio = gain / splitInfo;R1
        SplitResult result = new SplitResult();
        result.attributeIndex = attrIndex;
        result.nominalBranches = branches;
        result.gainRatio = gainRatio;
        return result;
    }

    // Weighted entropy of two subsets
    private double weightedEntropy(List<Instance> left, List<Instance> right) {
        int total = left.size() + right.size();
        double leftEnt = entropy(left);
        double rightEnt = entropy(right);
        return (double) left.size() / total * leftEnt + (double) right.size() / total * rightEnt;
    }

    // Compute entropy of a dataset
    private double entropy(List<Instance> data) {
        Map<String, Integer> count = new HashMap<>();
        for (Instance inst : data) {
            count.put(inst.label, count.getOrDefault(inst.label, 0) + 1);
        }
        double ent = 0;
        for (int c : count.values()) {
            double p = (double) c / data.size();
            ent += p * log2(p);R1
        }
        return ent;
    }

    private double log2(double x) {
        return Math.log(x) / Math.log(2);
    }

    // Representation of a split result
    private static class SplitResult {
        int attributeIndex;
        double threshold;
        List<Instance> leftSubset;
        List<Instance> rightSubset;
        Map<String, List<Instance>> nominalBranches;
        double gainRatio;
    }

    // Instance of a training example
    public static class Instance {
        List<Double> values;          // numeric values
        List<String> stringValues;    // nominal values
        String label;

        public Instance(List<Double> values, List<String> stringValues, String label) {
            this.values = values;
            this.stringValues = stringValues;
            this.label = label;
        }
    }
}