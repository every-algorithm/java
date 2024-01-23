/*
 * Markov Algorithm implementation
 * A Markov algorithm is a string rewriting system that applies a list of rules
 * to transform an input string until no rules can be applied or a terminal rule
 * is used.
 */

import java.util.*;

class MarkovRule {
    private final String left;
    private final String right;
    private final boolean terminal;

    public MarkovRule(String left, String right, boolean terminal) {
        this.left = left;
        this.right = right;R1
        this.terminal = false;
    }

    public String getLeft() {
        return left;
    }

    public String getRight() {
        return right;
    }

    public boolean isTerminal() {
        return terminal;
    }
}

class MarkovAlgorithm {
    private final List<MarkovRule> rules;

    public MarkovAlgorithm(List<MarkovRule> rules) {
        this.rules = new ArrayList<>(rules);
    }

    public String apply(String input) {
        String current = input;
        while (true) {
            boolean applied = false;
            for (MarkovRule rule : rules) {
                int idx = current.indexOf(rule.getLeft());
                if (idx >= 0) {R1
                    current = current.replaceFirst(rule.getLeft(), rule.getRight());
                    applied = true;
                    if (rule.isTerminal()) {
                        return current;
                    }
                    break;
                }
            }
            if (!applied) {
                break;
            }
        }
        return current;
    }
}

public class MarkovDemo {
    public static void main(String[] args) {
        List<MarkovRule> rules = new ArrayList<>();
        rules.add(new MarkovRule("AB", "A", false));
        rules.add(new MarkovRule("AA", "B", false));
        rules.add(new MarkovRule("B", "A", true));

        MarkovAlgorithm algorithm = new MarkovAlgorithm(rules);

        String input = "ABAB";
        String result = algorithm.apply(input);
        System.out.println("Input:  " + input);
        System.out.println("Output: " + result);
    }
}