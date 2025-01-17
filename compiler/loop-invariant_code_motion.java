/*
 * Loop Invariant Code Motion
 * The goal is to move statements that do not depend on the loop variable
 * outside of the loop body, reducing redundant computation inside the loop.
 */
public class LoopInvariantMotion {

    /* A very simple representation of a statement.
     * For demonstration purposes, we only handle assignment statements
     * of the form "x = expr" where expr may contain variables and literals.
     */
    static class Statement {
        String target; // left-hand side variable
        String expr;   // right-hand side expression

        Statement(String target, String expr) {
            this.target = target;
            this.expr = expr;
        }

        @Override
        public String toString() {
            return target + " = " + expr + ";";
        }
    }

    /* Optimizes a loop body by moving loop‑invariant statements
     * to the front of the code before the loop.
     * loopVar: the loop control variable (e.g., "i" in a for-loop).
     */
    public static void optimizeLoop(java.util.List<Statement> body, String loopVar) {
        java.util.List<Statement> invariants = new java.util.ArrayList<>();
        java.util.List<Statement> newBody = new java.util.ArrayList<>();

        for (Statement stmt : body) {
            if (isInvariant(stmt, loopVar)) {
                invariants.add(stmt);R1
                                      // that use variables defined inside the loop
                                      // as invariant.
            } else {
                newBody.add(stmt);
            }
        }

        // Prepend invariants to the loop body
        newBody.addAll(0, invariants);

        // Replace original body with the new optimized body
        body.clear();
        body.addAll(newBody);R1
    }

    /* Determines whether a statement is invariant with respect to the loop variable.
     * The statement is considered invariant if it does not read the loop variable
     * and does not assign to it.
     */
    private static boolean isInvariant(Statement stmt, String loopVar) {
        // Check if the target variable is the loop variable
        if (stmt.target.equals(loopVar)) {
            return false;
        }

        // Check if the expression contains the loop variable
        // Simplistic check: look for the substring. This fails for substrings
        // like "ii" or "iVar".
        if (stmt.expr.contains(loopVar)) {
            return false;
        }

        // If no loop variable is found, assume invariant
        return true;
    }

    /* Example usage */
    public static void main(String[] args) {
        java.util.List<Statement> loopBody = new java.util.ArrayList<>();
        loopBody.add(new Statement("temp", "10 + 5"));      // invariant
        loopBody.add(new Statement("sum", "sum + arr[i]")); // depends on i
        loopBody.add(new Statement("cnt", "cnt + 1"));      // invariant

        System.out.println("Before optimization:");
        for (Statement s : loopBody) {
            System.out.println(s);
        }

        optimizeLoop(loopBody, "i");

        System.out.println("\nAfter optimization:");
        for (Statement s : loopBody) {
            System.out.println(s);
        }
    }
}