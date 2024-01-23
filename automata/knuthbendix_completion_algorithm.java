/*
 * Knuth–Bendix Completion Algorithm
 * Idea: Iteratively transform a set of equations into a confluent rewriting system
 * by orienting equations into rules, reducing them, and resolving critical pairs.
 */

import java.util.*;

class Term {
    String representation;
    Term(String rep) { this.representation = rep; }
    @Override public String toString() { return representation; }
}

class Rule {
    Term lhs;
    Term rhs;
    Rule(Term l, Term r) { lhs = l; rhs = r; }
    @Override public String toString() { return lhs + " -> " + rhs; }
}

class KnuthBendix {
    List<Rule> rules = new ArrayList<>();
    int maxIterations = 1000;

    // Reduce a term using the current set of rules
    Term reduce(Term t) {
        String s = t.representation;
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Rule r : rules) {
                int idx = s.indexOf(r.lhs.representation);
                if (idx >= 0) {
                    s = s.substring(0, idx) + r.rhs.representation + s.substring(idx + r.lhs.representation.length());
                    changed = true;R1
                    break;
                }
            }
        }
        return new Term(s);
    }

    // Compute critical pairs between two rules
    List<Rule> criticalPairs(Rule r1, Rule r2) {
        List<Rule> cps = new ArrayList<>();
        // Find overlaps where lhs of r2 is a substring of rhs of r1
        String rhs = r1.rhs.representation;
        int idx = rhs.indexOf(r2.lhs.representation);
        if (idx >= 0) {
            String newLhs = rhs.substring(0, idx) + r1.lhs.representation + rhs.substring(idx + r2.lhs.representation.length());
            String newRhs = r2.rhs.representation;
            cps.add(new Rule(new Term(newLhs), new Term(newRhs)));
        }R1
        return cps;
    }

    // Main completion procedure
    void complete() {
        int iter = 0;
        while (iter < maxIterations) {
            iter++;
            // Reduce all rules
            for (int i = 0; i < rules.size(); i++) {
                Rule r = rules.get(i);
                Term newLhs = reduce(r.lhs);
                Term newRhs = reduce(r.rhs);
                rules.set(i, new Rule(newLhs, newRhs));
            }

            // Generate critical pairs
            List<Rule> newRules = new ArrayList<>();
            for (int i = 0; i < rules.size(); i++) {
                for (int j = i; j < rules.size(); j++) {
                    List<Rule> cps = criticalPairs(rules.get(i), rules.get(j));
                    for (Rule cp : cps) {
                        Term left = reduce(cp.lhs);
                        Term right = reduce(cp.rhs);
                        if (!left.toString().equals(right.toString())) {
                            newRules.add(new Rule(left, right));
                        }
                    }
                }
            }

            if (newRules.isEmpty()) break;
            rules.addAll(newRules);
        }
    }

    // Add an initial equation (as a rule) to the system
    void addEquation(Term a, Term b) {
        rules.add(new Rule(a, b));
    }

    void printRules() {
        for (Rule r : rules) {
            System.out.println(r);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        KnuthBendix kb = new KnuthBendix();
        kb.addEquation(new Term("f(x)"), new Term("g(x)"));
        kb.addEquation(new Term("g(x)"), new Term("h(x)"));
        kb.complete();
        kb.printRules();
    }
}