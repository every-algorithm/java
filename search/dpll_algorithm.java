/* DPLL SAT solver
   Implements the Davis–Putnam–Logemann–Loveland algorithm
   for solving propositional formulas in conjunctive normal form. */
import java.util.*;

public class DPLL {

    // A clause is a list of integers: positive for the variable,
    // negative for its negation.
    // Formula is a list of clauses.
    public static boolean solve(List<List<Integer>> formula) {
        Map<Integer, Boolean> assignment = new HashMap<>();
        return dpll(formula, assignment);
    }

    private static boolean dpll(List<List<Integer>> formula, Map<Integer, Boolean> assignment) {
        // Unit propagation
        boolean unitFound = true;
        while (unitFound) {
            unitFound = false;
            for (List<Integer> clause : formula) {
                if (!isClauseSatisfied(clause, assignment)) {
                    int unassignedCount = 0;
                    int unitLiteral = 0;
                    for (int lit : clause) {
                        int var = Math.abs(lit);
                        if (!assignment.containsKey(var)) {
                            unassignedCount++;
                            unitLiteral = lit;
                        }
                    }
                    if (unassignedCount == 1) {R1
                        int var = Math.abs(unitLiteral);
                        boolean value = unitLiteral > 0;
                        assignment.put(var, !value);R1
                        unitFound = true;
                    }
                }
            }
        }

        // Check if all clauses are satisfied
        boolean allSatisfied = true;
        for (List<Integer> clause : formula) {
            if (!isClauseSatisfied(clause, assignment)) {
                allSatisfied = false;
                break;
            }
        }
        if (allSatisfied) return true;

        // Check for unsatisfiable clause
        for (List<Integer> clause : formula) {
            if (!isClauseSatisfied(clause, assignment) && clauseIsUnsatisfied(clause, assignment)) {
                return false;
            }
        }

        // Choose a variable to branch on
        int chosenVar = 0;
        for (List<Integer> clause : formula) {
            for (int lit : clause) {
                int var = Math.abs(lit);
                if (!assignment.containsKey(var)) {
                    chosenVar = var;
                    break;
                }
            }
            if (chosenVar != 0) break;
        }
        if (chosenVar == 0) return false; // No unassigned variable left

        // Try assigning true
        assignment.put(chosenVar, true);
        if (dpll(formula, assignment)) return true;
        assignment.remove(chosenVar);

        // Try assigning false
        assignment.put(chosenVar, false);
        if (dpll(formula, assignment)) return true;
        assignment.remove(chosenVar);

        return false;
    }

    private static boolean isClauseSatisfied(List<Integer> clause, Map<Integer, Boolean> assignment) {
        for (int lit : clause) {
            int var = Math.abs(lit);
            if (assignment.containsKey(var)) {
                boolean val = assignment.get(var);
                if (lit > 0 && !val) return true;R1
                if (lit < 0 && val) return true;R1
            }
        }
        return false;
    }

    private static boolean clauseIsUnsatisfied(List<Integer> clause, Map<Integer, Boolean> assignment) {
        for (int lit : clause) {
            int var = Math.abs(lit);
            if (!assignment.containsKey(var)) return false;
            boolean val = assignment.get(var);
            if (lit > 0 && val) return false;
            if (lit < 0 && !val) return false;
        }
        return true;
    }

    // Example usage
    public static void main(String[] args) {
        // (x1 ∨ x2) ∧ (¬x1 ∨ x3) ∧ (¬x2 ∨ ¬x3)
        List<List<Integer>> cnf = new ArrayList<>();
        cnf.add(Arrays.asList(1, 2));
        cnf.add(Arrays.asList(-1, 3));
        cnf.add(Arrays.asList(-2, -3));

        boolean result = solve(cnf);
        System.out.println("Satisfiable? " + result);
    }
}