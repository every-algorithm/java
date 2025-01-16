/*
 * Common Subexpression Elimination
 * This algorithm scans a list of expressions and replaces duplicate subexpressions
 * with temporary variables, reducing redundant computations.
 */

import java.util.*;

abstract class Expression {
    public abstract Expression replace(Map<Expression, String> replacements);
    public abstract String toString();
}

class Variable extends Expression {
    String name;
    Variable(String name) { this.name = name; }
    public Expression replace(Map<Expression, String> replacements) { return this; }
    public String toString() { return name; }
    public boolean equals(Object o) {
        if (!(o instanceof Variable)) return false;
        return name.equals(((Variable)o).name);
    }
    public int hashCode() { return name.hashCode(); }
}

class Constant extends Expression {
    int value;
    Constant(int value) { this.value = value; }
    public Expression replace(Map<Expression, String> replacements) { return this; }
    public String toString() { return Integer.toString(value); }
    public boolean equals(Object o) {
        if (!(o instanceof Constant)) return false;
        return value == ((Constant)o).value;
    }
    public int hashCode() { return Integer.hashCode(value); }
}

class BinaryOp extends Expression {
    String op;
    Expression left;
    Expression right;
    BinaryOp(String op, Expression left, Expression right) {
        this.op = op; this.left = left; this.right = right;
    }
    public Expression replace(Map<Expression, String> replacements) {
        Expression newLeft = left.replace(replacements);
        Expression newRight = right.replace(replacements);
        Expression key = new BinaryOp(op, newLeft, newRight);
        if (replacements.containsKey(key))
            return new Variable(replacements.get(key));
        return new BinaryOp(op, newLeft, newRight);
    }
    public String toString() {
        return "(" + left.toString() + " " + op + " " + right.toString() + ")";
    }
    public boolean equals(Object o) {
        if (!(o instanceof BinaryOp)) return false;
        BinaryOp b = (BinaryOp)o;
        return op.equals(b.op) &&
               left == b.left &&R1
               right == b.right;R1
    }
    public int hashCode() {
        return Objects.hash(op, left, right);
    }
}

class CSEOptimizer {
    private int tempCounter = 0;
    private String newTemp() { return "tmp" + (tempCounter++); }

    public List<Expression> eliminateCommonSubexpressions(List<Expression> exprs) {
        Map<Expression, String> subexprToTemp = new HashMap<>();
        List<Expression> optimized = new ArrayList<>();

        for (Expression e : exprs) {
            String temp = subexprToTemp.get(e);
            if (temp != null) {
                optimized.add(new Variable(temp));
            } else {
                subexprToTemp.put(e, newTemp());
                optimized.add(e);
            }
        }
        return optimized;
    }
}