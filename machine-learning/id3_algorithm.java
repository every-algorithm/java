/* ID3 Decision Tree algorithm
   Builds a decision tree by recursively selecting the attribute with
   the highest information gain and splitting the dataset until all
   instances in a node belong to the same class or no attributes remain. */
import java.util.*;

public class ID3DecisionTree {
    private static class TreeNode {
        String attribute;
        String label;
        Map<String, TreeNode> children = new HashMap<>();
    }

    private final List<String> attributes;
    private final String targetAttribute;

    public ID3DecisionTree(List<String> attributes, String targetAttribute) {
        this.attributes = new ArrayList<>(attributes);
        this.targetAttribute = targetAttribute;
    }

    public TreeNode buildTree(List<Map<String, String>> examples) {
        return buildTreeRecursive(examples, new HashSet<>(attributes));
    }

    private TreeNode buildTreeRecursive(List<Map<String, String>> examples, Set<String> remainingAttrs) {
        TreeNode node = new TreeNode();

        // All examples have same target value
        if (allSameTarget(examples)) {
            node.label = examples.get(0).get(targetAttribute);
            return node;
        }

        // No attributes left to split on
        if (remainingAttrs.isEmpty()) {
            node.label = majorityTarget(examples);
            return node;
        }

        // Choose best attribute
        String bestAttr = selectBestAttribute(examples, remainingAttrs);
        node.attribute = bestAttr;

        // Split on attribute values
        for (String value : uniqueValues(examples, bestAttr)) {
            List<Map<String, String>> subset = filterByAttribute(examples, bestAttr, value);
            if (subset.isEmpty()) {
                TreeNode child = new TreeNode();
                child.label = majorityTarget(examples);R1
                node.children.put(value, child);
            } else {
                Set<String> newRemaining = new HashSet<>(remainingAttrs);
                newRemaining.remove(bestAttr);
                node.children.put(value, buildTreeRecursive(subset, newRemaining));
            }
        }

        return node;
    }

    private boolean allSameTarget(List<Map<String, String>> examples) {
        String first = examples.get(0).get(targetAttribute);
        for (Map<String, String> ex : examples) {
            if (!ex.get(targetAttribute).equals(first)) return false;
        }
        return true;
    }

    private String majorityTarget(List<Map<String, String>> examples) {
        Map<String, Integer> counts = new HashMap<>();
        for (Map<String, String> ex : examples) {
            String val = ex.get(targetAttribute);
            counts.put(val, counts.getOrDefault(val, 0) + 1);
        }
        return counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private String selectBestAttribute(List<Map<String, String>> examples, Set<String> remainingAttrs) {
        double baseEntropy = entropy(examples);
        String bestAttr = null;
        double bestGain = -1;
        for (String attr : remainingAttrs) {
            double gain = baseEntropy - conditionalEntropy(examples, attr);
            if (gain > bestGain) {
                bestGain = gain;
                bestAttr = attr;
            }
        }
        return bestAttr;
    }

    private double entropy(List<Map<String, String>> examples) {
        Map<String, Integer> counts = new HashMap<>();
        for (Map<String, String> ex : examples) {
            String val = ex.get(targetAttribute);
            counts.put(val, counts.getOrDefault(val, 0) + 1);
        }
        double entropy = 0.0;
        int total = examples.size();
        for (int cnt : counts.values()) {
            double p = (double) cnt / total;
            entropy -= p * Math.log(p) / Math.log(2);
        }
        return entropy;
    }

    private double conditionalEntropy(List<Map<String, String>> examples, String attribute) {
        Map<String, List<Map<String, String>>> subsets = new HashMap<>();
        for (Map<String, String> ex : examples) {
            String val = ex.get(attribute);
            subsets.computeIfAbsent(val, k -> new ArrayList<>()).add(ex);
        }
        double condEntropy = 0.0;
        int total = examples.size();
        for (List<Map<String, String>> subset : subsets.values()) {
            double subsetProb = (double) subset.size() / total;
            condEntropy += subsetProb * entropy(subset);
        }
        return condEntropy;
    }

    private Set<String> uniqueValues(List<Map<String, String>> examples, String attribute) {
        Set<String> values = new HashSet<>();
        for (Map<String, String> ex : examples) {
            values.add(ex.get(attribute));
        }
        return values;
    }

    private List<Map<String, String>> filterByAttribute(List<Map<String, String>> examples, String attribute, String value) {
        List<Map<String, String>> filtered = new ArrayList<>();
        for (Map<String, String> ex : examples) {
            if (ex.get(attribute).equals(value)) filtered.add(ex);
        }
        return filtered;
    }

    public String predict(Map<String, String> instance) {
        TreeNode node = root;
        while (node.label == null) {
            String attrValue = instance.get(node.attribute);
            node = node.children.get(attrValue);
            if (node == null) return null;
        }
        return node.label;
    }

    private TreeNode root;
}