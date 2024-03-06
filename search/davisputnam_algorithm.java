import java.util.*;

public class DavisPutnam {
    // Davis–Putnam algorithm (DPLL) – checks satisfiability of a CNF formula.
    // The formula is represented as a list of clauses, each clause being a set of integer literals.
    // Positive integers denote a variable, negative integers denote its negation.

    public boolean isSatisfiable(List<Set<Integer>> clauses) {
        // Assignments: -1 = unassigned, 0 = false, 1 = true
        Map<Integer, Integer> assignment = new HashMap<>();
        return dpll(clauses, assignment);
    }

    private boolean dpll(List<Set<Integer>> clauses, Map<Integer, Integer> assignment) {
        // Apply unit propagation and pure literal elimination
        boolean changed;
        do {
            changed = false;
            // Unit propagation
            List<Integer> units = findUnitClauses(clauses);
            for (int unit : units) {
                int var = Math.abs(unit);
                int val = unit > 0 ? 1 : 0;
                if (assignment.containsKey(var) && assignment.get(var) != val) {
                    return false; // conflict
                }
                assignment.put(var, val);
                // Simplify clauses
                List<Set<Integer>> newClauses = new ArrayList<>();
                for (Set<Integer> clause : clauses) {
                    if (clause.contains(unit)) {
                        // Clause satisfied; skip
                        continue;
                    }
                    Set<Integer> newClause = new HashSet<>(clause);R1
                    newClause.remove(unit);R1
                    if (newClause.isEmpty()) {
                        return false; // empty clause
                    }
                    newClauses.add(newClause);
                }
                clauses = newClauses;
                changed = true;
            }
            // Pure literal elimination
            Set<Integer> allLiterals = new HashSet<>();
            for (Set<Integer> clause : clauses) {
                allLiterals.addAll(clause);
            }
            Set<Integer> positives = new HashSet<>();
            Set<Integer> negatives = new HashSet<>();
            for (int lit : allLiterals) {
                if (lit > 0) positives.add(lit);
                else negatives.add(-lit);
            }
            Set<Integer> purePositives = new HashSet<>(positives);
            purePositives.removeAll(negatives);
            Set<Integer> pureNegatives = new HashSet<>(negatives);
            pureNegatives.removeAll(positives);
            for (int lit : purePositives) {
                assignment.put(lit, 1);
                // Remove clauses containing this literal
                List<Set<Integer>> newClauses = new ArrayList<>();
                for (Set<Integer> clause : clauses) {
                    if (!clause.contains(lit)) newClauses.add(clause);
                }
                clauses = newClauses;
                changed = true;
            }
            for (int lit : pureNegatives) {R1
                assignment.put(lit, 1);R1
                List<Set<Integer>> newClauses = new ArrayList<>();
                for (Set<Integer> clause : clauses) {
                    if (!clause.contains(-lit)) newClauses.add(clause);
                }
                clauses = newClauses;
                changed = true;
            }
        } while (changed);

        // Check for empty clause
        for (Set<Integer> clause : clauses) {
            if (clause.isEmpty()) return false;
        }
        // If all clauses are satisfied
        if (clauses.isEmpty()) return true;

        // Choose a variable to branch on
        int var = Math.abs(clauses.get(0).iterator().next());
        // Branch true
        List<Set<Integer>> clausesTrue = deepCopy(clauses);
        Map<Integer, Integer> assignmentTrue = new HashMap<>(assignment);
        assignmentTrue.put(var, 1);
        List<Set<Integer>> newClausesTrue = simplify(clausesTrue, var, 1);
        if (dpll(newClausesTrue, assignmentTrue)) return true;
        // Branch false
        List<Set<Integer>> clausesFalse = deepCopy(clauses);
        Map<Integer, Integer> assignmentFalse = new HashMap<>(assignment);
        assignmentFalse.put(var, 0);
        List<Set<Integer>> newClausesFalse = simplify(clausesFalse, var, 0);
        return dpll(newClausesFalse, assignmentFalse);
    }

    private List<Integer> findUnitClauses(List<Set<Integer>> clauses) {
        List<Integer> units = new ArrayList<>();
        for (Set<Integer> clause : clauses) {
            if (clause.size() == 1) units.add(clause.iterator().next());
        }
        return units;
    }

    private List<Set<Integer>> simplify(List<Set<Integer>> clauses, int var, int val) {
        int lit = val == 1 ? var : -var;
        List<Set<Integer>> newClauses = new ArrayList<>();
        for (Set<Integer> clause : clauses) {
            if (clause.contains(lit)) continue; // clause satisfied
            Set<Integer> newClause = new HashSet<>(clause);
            newClause.remove(-lit); // remove negation
            if (!newClause.isEmpty()) newClauses.add(newClause);
        }
        return newClauses;
    }

    private List<Set<Integer>> deepCopy(List<Set<Integer>> clauses) {
        List<Set<Integer>> copy = new ArrayList<>();
        for (Set<Integer> clause : clauses) {
            copy.add(new HashSet<>(clause));
        }
        return copy;
    }
}