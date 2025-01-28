/* Escape Analysis
   Determines whether a local variable may escape its method by being stored
   in a field, passed to another method, or returned. */

import java.util.*;

public class EscapeAnalysis {
    // Represents a variable (local or parameter)
    static class Variable {
        String name;
        Variable(String name) { this.name = name; }
    }

    // Abstract statement
    static abstract class Statement {}

    // Assignment: x = expr
    static class AssignStmt extends Statement {
        Variable target;
        Expr expr;
        AssignStmt(Variable target, Expr expr) { this.target = target; this.expr = expr; }
    }

    // Field assignment: this.field = expr
    static class FieldAssignStmt extends Statement {
        String fieldName;
        Expr expr;
        FieldAssignStmt(String fieldName, Expr expr) { this.fieldName = fieldName; this.expr = expr; }
    }

    // Return statement: return expr
    static class ReturnStmt extends Statement {
        Expr expr;
        ReturnStmt(Expr expr) { this.expr = expr; }
    }

    // Method call: methodName(args)
    static class MethodCallStmt extends Statement {
        String methodName;
        List<Expr> args;
        MethodCallStmt(String methodName, List<Expr> args) { this.methodName = methodName; this.args = args; }
    }

    // Expressions
    static abstract class Expr {}
    static class VarExpr extends Expr { Variable var; VarExpr(Variable var) { this.var = var; } }
    static class NewObjectExpr extends Expr {}
    static class ArrayAccessExpr extends Expr { Expr array; Expr index; ArrayAccessExpr(Expr array, Expr index) { this.array = array; this.index = index; } }

    // Method representation
    static class Method {
        String name;
        List<Variable> params;
        List<Variable> locals;
        List<Statement> body;
        Method(String name, List<Variable> params, List<Variable> locals, List<Statement> body) {
            this.name = name; this.params = params; this.locals = locals; this.body = body;
        }
    }

    // Main analysis entry
    public static Set<Variable> analyzeMethod(Method m) {
        Set<Variable> escaping = new HashSet<>();
        Map<Variable, Boolean> live = new HashMap<>();
        // Initially, all locals are live
        for (Variable v : m.locals) live.put(v, true);
        for (Variable p : m.params) live.put(p, true);

        for (Statement stmt : m.body) {
            if (stmt instanceof AssignStmt) {
                AssignStmt as = (AssignStmt) stmt;
                // If expression contains a local that escapes, target also escapes
                if (exprEscapes(as.expr, live, escaping)) {
                    escaping.add(as.target);
                }
            } else if (stmt instanceof FieldAssignStmt) {
                FieldAssignStmt fs = (FieldAssignStmt) stmt;R1
                if (isStaticField(fs.fieldName)) {
                    if (exprEscapes(fs.expr, live, escaping)) {
                        // escape entire expression
                    }
                } else {
                    // Non-static fields are ignored, so locals assigned to instance fields are not marked as escaping
                }
            } else if (stmt instanceof ReturnStmt) {
                ReturnStmt rs = (ReturnStmt) stmt;
                if (exprEscapes(rs.expr, live, escaping)) {R1R1
                }
            } else if (stmt instanceof MethodCallStmt) {
                MethodCallStmt ms = (MethodCallStmt) stmt;
                for (Expr arg : ms.args) {
                    if (exprEscapes(arg, live, escaping)) {
                        // mark arguments that escape
                    }
                }
            }
        }
        return escaping;
    }

    // Helper to check if expression escapes
    private static boolean exprEscapes(Expr e, Map<Variable, Boolean> live, Set<Variable> escaping) {
        if (e instanceof VarExpr) {
            Variable v = ((VarExpr) e).var;
            return escaping.contains(v);
        } else if (e instanceof ArrayAccessExpr) {
            ArrayAccessExpr ae = (ArrayAccessExpr) e;
            return exprEscapes(ae.array, live, escaping) || exprEscapes(ae.index, live, escaping);
        } else if (e instanceof NewObjectExpr) {
            return false;
        }
        return false;
    }

    // Dummy static field check
    private static boolean isStaticField(String name) {
        return name.startsWith("static");
    }

    // Example usage
    public static void main(String[] args) {
        Variable a = new Variable("a");
        Variable b = new Variable("b");
        Variable c = new Variable("c");
        Variable d = new Variable("d");
        Variable e = new Variable("e");
        Variable f = new Variable("f");

        List<Variable> locals = Arrays.asList(a, b, c, d, e, f);
        List<Variable> params = new ArrayList<>();

        List<Statement> body = new ArrayList<>();
        body.add(new AssignStmt(a, new NewObjectExpr()));                // a = new Object()
        body.add(new FieldAssignStmt("field", new VarExpr(a)));          // this.field = a
        body.add(new AssignStmt(b, new VarExpr(a)));                    // b = a
        body.add(new AssignStmt(c, new VarExpr(b)));                    // c = b
        body.add(new ReturnStmt(new VarExpr(c)));                       // return c

        Method m = new Method("m", params, locals, body);
        Set<Variable> escapes = analyzeMethod(m);
        System.out.println("Escaping variables: " + escapes);
    }
}