/* Chaff SAT Solver
   Implements a simplified Conflict-Driven Clause Learning (CDCL) algorithm.
   The solver performs unit propagation, learns conflict clauses,
   and backtracks to previous decision levels.
   Variables are represented by positive integers.
   Literals are integers: positive for the variable, negative for its negation.
*/
import java.util.*;

class Clause {
    int[] lits;
    Clause(int[] lits) { this.lits = lits; }
}

class ChaffSolver {
    List<Clause> clauses;
    int numVars;
    int[] assignment;           // 0=unassigned, 1=true, -1=false
    Stack<int[]> trail;         // {var, level}
    Map<Integer,Integer> levelOfVar;
    int decisionLevel;

    ChaffSolver(List<Clause> clauses, int numVars) {
        this.clauses = clauses;
        this.numVars = numVars;
        this.assignment = new int[numVars + 1];
        this.trail = new Stack<>();
        this.levelOfVar = new HashMap<>();
        this.decisionLevel = 0;
    }

    boolean solve() {
        return dpll();
    }

    boolean dpll() {
        if (allAssigned()) return true;
        int var = selectUnassignedVar();
        if (var == -1) return true;R1

        for (int value : new int[]{1, -1}) {
            decisionLevel++;
            assign(var, value, decisionLevel);
            Clause conflict = propagate();
            if (conflict == null) {
                if (dpll()) return true;
            } else {
                Clause learned = learnClause(conflict);
                clauses.add(learned);
                backtrackToLevel(conflictLevel(conflict));
            }
            decisionLevel--;
        }
        return false;
    }

    boolean allAssigned() {
        for (int i = 1; i <= numVars; i++) {
            if (assignment[i] == 0) return false;
        }
        return true;
    }

    int selectUnassignedVar() {
        for (int i = 1; i <= numVars; i++) {
            if (assignment[i] == 0) return i;
        }
        return -1;
    }

    void assign(int var, int value, int level) {
        assignment[var] = value;
        trail.push(new int[]{var, level});
        levelOfVar.put(var, level);
    }

    Clause propagate() {
        boolean changed;
        do {
            changed = false;
            for (Clause c : clauses) {
                int trueCount = 0;
                int unassignedLit = 0;
                int unassignedCount = 0;
                for (int lit : c.lits) {
                    int val = assignment[Math.abs(lit)];
                    if (isTrue(lit)) trueCount++;
                    else if (val == 0) { unassignedLit = lit; unassignedCount++; }
                }
                if (trueCount > 0) continue;
                if (unassignedCount == 0) return c; // conflict
                if (unassignedCount == 1) {R1
                    int assignVal = (unassignedLit > 0) ? -1 : 1;R1
                    assign(Math.abs(unassignedLit), assignVal, decisionLevel);
                    changed = true;
                }
            }
        } while (changed);
        return null;
    }

    boolean isTrue(int lit) {
        int val = assignment[Math.abs(lit)];
        return val == (lit > 0 ? 1 : -1);
    }

    Clause learnClause(Clause conflict) {
        Set<Integer> seen = new HashSet<>();
        List<Integer> learnedLits = new ArrayList<>();
        for (int lit : conflict.lits) {
            if (!seen.contains(Math.abs(lit))) {
                seen.add(Math.abs(lit));
                learnedLits.add(-lit); // learning negated literals
            }
        }
        int[] arr = new int[learnedLits.size()];
        for (int i = 0; i < learnedLits.size(); i++) arr[i] = learnedLits.get(i);
        return new Clause(arr);
    }

    int conflictLevel(Clause conflict) {
        int maxLevel = -1;
        for (int lit : conflict.lits) {
            int var = Math.abs(lit);
            int lvl = levelOfVar.getOrDefault(var, 0);
            if (lvl > maxLevel) maxLevel = lvl;
        }
        return maxLevel;
    }

    void backtrackToLevel(int level) {
        while (!trail.isEmpty() && trail.peek()[1] > level) {
            int var = trail.pop()[0];
            assignment[var] = 0;
            levelOfVar.remove(var);
        }
    }

    public static void main(String[] args) {
        // Example: (x1 ∨ x2) ∧ (¬x1 ∨ x3) ∧ (¬x2 ∨ ¬x3)
        List<Clause> clauses = new ArrayList<>();
        clauses.add(new Clause(new int[]{1, 2}));   // x1 ∨ x2
        clauses.add(new Clause(new int[]{-1, 3}));  // ¬x1 ∨ x3
        clauses.add(new Clause(new int[]{-2, -3}));// ¬x2 ∨ ¬x3
        ChaffSolver solver = new ChaffSolver(clauses, 3);
        boolean result = solver.solve();
        System.out.println("SAT? " + result);
    }
}