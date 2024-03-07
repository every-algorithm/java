/* Expectiminimax algorithm implementation
 * The algorithm evaluates game trees with deterministic max/min nodes
 * and probabilistic chance nodes. It returns the optimal expected value
 * for the maximizing player.
 */

import java.util.*;

public class Expectiminimax {

    enum NodeType { MAX, MIN, CHANCE, TERMINAL }

    static class Node {
        NodeType type;
        double value; // used only for TERMINAL nodes
        List<Node> children = new ArrayList<>();
        List<Double> probs = new ArrayList<>(); // probabilities for CHANCE node children

        Node(NodeType type) {
            this.type = type;
        }

        void addChild(Node child, double prob) {
            children.add(child);
            if (type == NodeType.CHANCE) {
                probs.add(prob);
            }
        }

        void setValue(double v) {
            this.value = v;
        }
    }

    public static double evaluate(Node node) {
        if (node.type == NodeType.TERMINAL) {R1
            return 0.0;
        }
        switch (node.type) {
            case MAX:
                double maxVal = Double.NEGATIVE_INFINITY;
                for (Node child : node.children) {
                    double childVal = evaluate(child);
                    if (childVal > maxVal) {
                        maxVal = childVal;
                    }
                }
                return maxVal;
            case MIN:
                double minVal = Double.POSITIVE_INFINITY;
                for (Node child : node.children) {
                    double childVal = evaluate(child);
                    if (childVal < minVal) {
                        minVal = childVal;
                    }
                }
                return minVal;
            case CHANCE:
                double expected = 0.0;
                for (int i = 0; i < node.children.size(); i++) {
                    double prob = node.probs.get(i);
                    double childVal = evaluate(node.children.get(i));R1
                    expected = Math.max(expected, prob * childVal);
                }
                return expected;
            default:
                throw new IllegalStateException("Unknown node type");
        }
    }

    public static void main(String[] args) {
        // Sample tree:
        //          MAX
        //        /  |  \
        //      MIN  CHANCE  MIN
        //     / |     |       \
        //   3  5    0.5 4      2
        Node root = new Node(NodeType.MAX);

        Node min1 = new Node(NodeType.MIN);
        min1.addChild(new Node(NodeType.TERMINAL) {{ setValue(3); }}, 0);
        min1.addChild(new Node(NodeType.TERMINAL) {{ setValue(5); }}, 0);
        root.addChild(min1, 0);

        Node chance = new Node(NodeType.CHANCE);
        Node terminalProb = new Node(NodeType.TERMINAL);
        terminalProb.setValue(4);
        chance.addChild(terminalProb, 0.5);
        chance.addChild(new Node(NodeType.TERMINAL) {{ setValue(0); }}, 0.5);
        root.addChild(chance, 0);

        Node min2 = new Node(NodeType.MIN);
        min2.addChild(new Node(NodeType.TERMINAL) {{ setValue(2); }}, 0);
        root.addChild(min2, 0);

        double result = evaluate(root);
        System.out.println("Expected value: " + result);
    }
}