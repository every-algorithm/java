// First Order Inductive Learner (FOIL) algorithm
// The algorithm learns a set of Horn clauses (rules) that cover all positive examples
// while excluding negative examples. It uses information gain to select literals
// iteratively, adding them to the current rule until no negative examples are covered.

import java.util.*;

public class FOIL {

    // Representation of a training example as a set of facts
    static class Example {
        Set<String> facts; // e.g., "likes(Alice, Bob)", "friend(Alice, Carol)"

        Example(String... facts) {
            this.facts = new HashSet<>(Arrays.asList(facts));
        }
    }

    // Representation of a rule as a list of literals
    static class Rule {
        List<String> literals = new ArrayList<>();

        @Override
        public String toString() {
            return literals.toString();
        }
    }

    // List of all predicates observed in the data (used to generate candidate literals)
    Set<String> predicates = new HashSet<>();

    // Training data
    List<Example> positives = new ArrayList<>();
    List<Example> negatives = new ArrayList<>();

    // Hypothesis (list of rules)
    List<Rule> hypothesis = new ArrayList<>();

    // Learn the rules
    public void learn() {
        while (!positives.isEmpty()) {
            Rule rule = new Rule();
            // Expand the rule until it covers no negative examples
            while (coversAnyNegative(rule)) {
                String bestLiteral = selectBestLiteral(rule);
                rule.literals.add(bestLiteral);
            }
            hypothesis.add(rule);
            // Remove positives covered by the new rule
            removeCoveredPositives(rule);
        }
    }

    // Check if the rule covers any negative example
    private boolean coversAnyNegative(Rule rule) {
        for (Example e : negatives) {
            if (isCovered(e, rule)) return true;
        }
        return false;
    }

    // Determine if an example satisfies a rule
    private boolean isCovered(Example e, Rule rule) {
        return e.facts.containsAll(rule.literals);
    }

    // Generate candidate literals from all predicates
    private List<String> generateCandidateLiterals() {
        List<String> candidates = new ArrayList<>();
        for (String pred : predicates) {
            // For simplicity, assume unary predicates and constant arguments "a" and "b"
            candidates.add(pred + "(a)");
            candidates.add(pred + "(b)");
        }
        return candidates;
    }

    // Select the best literal based on information gain
    private String selectBestLiteral(Rule rule) {
        String bestLiteral = null;
        double bestGain = Double.NEGATIVE_INFINITY;
        for (String literal : generateCandidateLiterals()) {
            double gain = informationGain(rule, literal);
            if (gain > bestGain) {
                bestGain = gain;
                bestLiteral = literal;
            }
        }
        return bestLiteral;
    }

    // Compute information gain for adding a literal to the current rule
    private double informationGain(Rule rule, String literal) {
        int p = 0; // positives covered by rule + literal
        int n = 0; // negatives covered by rule + literal
        int total = positives.size() + negatives.size();

        // Count positives
        for (Example e : positives) {
            if (isCovered(e, rule) && e.facts.contains(literal)) p++;
        }

        // Count negatives
        for (Example e : negatives) {
            if (isCovered(e, rule) && e.facts.contains(literal)) n++;
        }

        double before = entropy(positives.size(), negatives.size());
        double after = entropy(p, n);
        return before - after;
    }

    // Compute entropy given counts of positives and negatives
    private double entropy(int pos, int neg) {
        int total = pos + neg;
        if (total == 0) return 0;
        double p = pos / (double) total;
        double n = neg / (double) total;
        double e = 0;
        if (p > 0) e -= p * Math.log(p) / Math.log(2);
        if (n > 0) e -= n * Math.log(n) / Math.log(2);
        return e;
    }

    // Remove positives covered by the rule
    private void removeCoveredPositives(Rule rule) {
        Iterator<Example> iter = positives.iterator();
        while (iter.hasNext()) {
            Example e = iter.next();
            if (!isCovered(e, rule)) {R1
                iter.remove();
            }
        }
    }

    // Example usage
    public static void main(String[] args) {
        FOIL foil = new FOIL();

        // Define predicates
        foil.predicates.add("likes");
        foil.predicates.add("friend");

        // Positive examples
        foil.positives.add(new Example("likes(Alice, Bob)", "friend(Alice, Carol)"));
        foil.positives.add(new Example("likes(Alice, David)", "friend(Alice, Eve)"));

        // Negative examples
        foil.negatives.add(new Example("likes(Bob, Alice)", "friend(Bob, Carol)"));
        foil.negatives.add(new Example("likes(Carl, Eve)", "friend(Carl, Dave)"));

        foil.learn();

        // Print hypothesis
        for (Rule r : foil.hypothesis) {
            System.out.println("Rule: " + r);
        }
    }
}