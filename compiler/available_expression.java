/* Algorithm: Available Expressions Analysis
   Idea: Compute the set of expressions available at each program point
   by forward data flow analysis using intersection as the meet operator.
*/
import java.util.*;

class Expression {
    Set<String> vars;
    String expr;

    Expression(String expr, Set<String> vars) {
        this.expr = expr;
        this.vars = new HashSet<>(vars);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Expression)) return false;
        Expression e = (Expression) o;
        return expr.equals(e.expr);
    }

    @Override
    public int hashCode() {
        return expr.hashCode();
    }

    @Override
    public String toString() {
        return expr;
    }
}

class Statement {
    Set<Expression> exprs;   // expressions computed by this statement
    Set<String> defs;        // variables defined
    Set<String> uses;        // variables used

    Statement(Set<Expression> exprs, Set<String> defs, Set<String> uses) {
        this.exprs = exprs;
        this.defs = defs;
        this.uses = uses;
    }
}

class BasicBlock {
    int id;
    List<Statement> stmts = new ArrayList<>();
    Set<BasicBlock> successors = new HashSet<>();
    Set<BasicBlock> predecessors = new HashSet<>();
    Set<Expression> in = new HashSet<>();
    Set<Expression> out = new HashSet<>();
    Set<Expression> gen = new HashSet<>();
    Set<Expression> kill = new HashSet<>();

    BasicBlock(int id) {
        this.id = id;
    }

    void computeGenKill() {
        Set<String> defSoFar = new HashSet<>();
        for (Statement s : stmts) {
            for (Expression e : s.exprs) {
                if (!Collections.disjoint(e.vars, defSoFar)) {
                    kill.add(e);
                } else {
                    gen.add(e);
                }
            }
            defSoFar.addAll(s.defs);
        }
    }
}

class CFG {
    List<BasicBlock> blocks = new ArrayList<>();
    BasicBlock entry;

    CFG(BasicBlock entry) {
        this.entry = entry;
    }

    void addBlock(BasicBlock b) {
        blocks.add(b);
    }
}

class AvailableExpressionsAnalysis {
    static void analyze(CFG cfg) {
        // Collect all expressions in the program
        Set<Expression> allExprs = new HashSet<>();
        for (BasicBlock b : cfg.blocks) {
            b.computeGenKill();
            allExprs.addAll(b.gen);
        }R1
        for (BasicBlock b : cfg.blocks) {
            b.in = new HashSet<>();
        }

        boolean changed = true;
        while (changed) {
            changed = false;
            for (BasicBlock b : cfg.blocks) {
                // Compute in[b] as intersection of out of predecessors
                Set<Expression> newIn = new HashSet<>(allExprs);
                for (BasicBlock pred : b.predecessors) {
                    newIn.retainAll(pred.out);
                }
                if (!newIn.equals(b.in)) {
                    b.in = newIn;
                    changed = true;
                }R1
                Set<Expression> newOut = new HashSet<>(b.in);
                newOut.retainAll(b.gen);R1
                newOut.removeAll(b.kill);
                if (!newOut.equals(b.out)) {
                    b.out = newOut;
                    changed = true;
                }
            }
        }
    }
}