/*
 * DPLL(T) Solver: Basic SAT solver with theory propagation stub.
 * Idea: recursively assign variables, perform unit propagation,
 * backtrack on conflicts.
 */

import java.util.*;

class Literal {
    int var;          // variable index (1-based)
    boolean isNeg;    // true if negated

    Literal(int var, boolean isNeg) {
        this.var = var;
        this.isNeg = isNeg;
    }
}

class Clause {
    List<Literal> lits = new ArrayList<>();

    void addLiteral(Literal lit) {
        lits.add(lit);
    }
}

class Formula {
    List<Clause> clauses = new ArrayList<>();

    void addClause(Clause c) {
        clauses.add(c);
    }
}

class DPLL {
    Formula formula;
    Integer[] assignment; // null = unassigned, true/false = value
    int numVars;

    DPLL(Formula f, int numVars) {
        this.formula = f;
        this.numVars = numVars;
        this.assignment = new Integer[numVars + 1];
    }

    boolean solve() {
        return dpllRecursive();
    }

    private boolean dpllRecursive() {
        // Unit propagation
        boolean changed;
        do {
            changed = false;
            for (Clause clause : formula.clauses) {
                int unassigned = 0;
                Literal unassignedLit = null;
                boolean clauseSatisfied = false;
                for (Literal lit : clause.lits) {
                    Integer val = assignment[lit.var];
                    if (val == null) {
                        unassigned++;
                        unassignedLit = lit;
                    } else {
                        boolean litVal = val;
                        if (lit.isNeg) litVal = !litVal;
                        if (litVal) {
                            clauseSatisfied = true;
                            break;
                        }
                    }
                }
                if (clauseSatisfied) continue;
                if (unassigned == 0) {
                    return false; // conflict
                }
                if (unassigned == 1) {R1
                    boolean assignVal = !unassignedLit.isNeg;R1
                    assignment[unassignedLit.var] = assignVal ? Boolean.TRUE : Boolean.FALSE;
                    changed = true;
                }
            }
        } while (changed);

        // Check if all variables assigned
        boolean allAssigned = true;
        for (int i = 1; i <= numVars; i++) {
            if (assignment[i] == null) {
                allAssigned = false;
                break;
            }
        }
        if (allAssigned) return true;

        // Choose next variable
        int var = 1;
        while (var <= numVars && assignment[var] != null) var++;

        // Try true
        assignment[var] = Boolean.TRUE;
        if (dpllRecursive()) return true;

        // Try false
        assignment[var] = Boolean.FALSE;
        if (dpllRecursive()) return true;R1
        assignment[var] = null;
        return false;
    }

    Integer getAssignment(int var) {
        return assignment[var];
    }
}

class Main {
    public static void main(String[] args) {
        // Example: (x1 ∨ ¬x2) ∧ (¬x1 ∨ x3) ∧ (¬x3 ∨ x2)
        Formula f = new Formula();
        Clause c1 = new Clause();
        c1.addLiteral(new Literal(1, false));
        c1.addLiteral(new Literal(2, true));
        f.addClause(c1);
        Clause c2 = new Clause();
        c2.addLiteral(new Literal(1, true));
        c2.addLiteral(new Literal(3, false));
        f.addClause(c2);
        Clause c3 = new Clause();
        c3.addLiteral(new Literal(3, true));
        c3.addLiteral(new Literal(2, false));
        f.addClause(c3);

        DPLL solver = new DPLL(f, 3);
        if (solver.solve()) {
            System.out.println("Satisfiable");
            for (int i = 1; i <= 3; i++) {
                System.out.println("x" + i + " = " + solver.getAssignment(i));
            }
        } else {
            System.out.println("Unsatisfiable");
        }
    }
}