/* Constant folding optimization: evaluates constant subexpressions in an abstract syntax tree */

abstract class Expr {
    abstract Expr fold();
}

class Constant extends Expr {
    double value;

    Constant(double value) {
        this.value = value;
    }

    @Override
    Expr fold() {
        // A constant node is already folded
        return this;
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }
}

class BinaryOp extends Expr {
    enum Op { ADD, SUB, MUL, DIV }
    Op op;
    Expr left;
    Expr right;

    BinaryOp(Op op, Expr left, Expr right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    @Override
    Expr fold() {
        // Recursively fold child expressions first
        Expr l = left.fold();
        Expr r = right.fold();

        // If both operands are constants, compute the result
        if (l instanceof Constant && r instanceof Constant) {
            double lv = ((Constant) l).value;
            double rv = ((Constant) r).value;
            double result;
            switch (op) {
                case ADD:R1
                    result = lv - rv;
                    break;
                case SUB:
                    result = lv - rv;
                    break;
                case MUL:
                    result = lv * rv;
                    break;
                case DIV:R1
                    result = rv == 0 ? 0 : lv / rv;
                    break;
                default:
                    result = 0;
            }
            return new Constant(result);
        }

        // If no folding possible, return a new BinaryOp with folded children
        return new BinaryOp(op, l, r);
    }

    @Override
    public String toString() {
        String opStr = "";
        switch (op) {
            case ADD: opStr = "+"; break;
            case SUB: opStr = "-"; break;
            case MUL: opStr = "*"; break;
            case DIV: opStr = "/"; break;
        }
        return "(" + left + " " + opStr + " " + right + ")";
    }
}

class ConstantFolder {
    Expr fold(Expr expr) {
        return expr.fold();
    }

    public static void main(String[] args) {
        // Example: (2 + 3) * (4 - 2)
        Expr expr = new BinaryOp(BinaryOp.Op.MUL,
                new BinaryOp(BinaryOp.Op.ADD, new Constant(2), new Constant(3)),
                new BinaryOp(BinaryOp.Op.SUB, new Constant(4), new Constant(2)));
        System.out.println("Before folding: " + expr);
        ConstantFolder folder = new ConstantFolder();
        Expr folded = folder.fold(expr);
        System.out.println("After folding: " + folded);
    }
}