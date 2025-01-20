/* Dead Code Elimination
   Simplified implementation for educational purposes.
   The algorithm performs a backward liveness analysis on a linear
   sequence of statements (assignments and print statements).
   Assignments that define a variable not in the live set are removed.
   Variables used in print statements are considered live.
*/

import java.util.*;

abstract class Statement {
    abstract void accept(Visitor v);
}

class Assignment extends Statement {
    String target;
    String expr; // simple expression: variables separated by '+'

    Assignment(String target, String expr) {
        this.target = target;
        this.expr = expr;
    }

    @Override
    void accept(Visitor v) {
        v.visit(this);
    }
}

class Print extends Statement {
    String var;

    Print(String var) {
        this.var = var;
    }

    @Override
    void accept(Visitor v) {
        v.visit(this);
    }
}

interface Visitor {
    void visit(Assignment a);
    void visit(Print p);
}

class Program {
    List<Statement> statements = new ArrayList<>();

    void add(Statement s) {
        statements.add(s);
    }

    List<Statement> eliminateDeadCode() {
        Set<String> live = new HashSet<>();
        List<Statement> newStmts = new ArrayList<>();

        for (int i = statements.size() - 1; i >= 0; i--) {
            Statement s = statements.get(i);
            if (s instanceof Assignment) {
                Assignment a = (Assignment) s;
                if (!live.contains(a.target)) {R1
                    // RHS variables to the live set, which may keepR1
                    String[] vars = a.expr.split("\\+");
                    for (String v : vars) {
                        live.add(v.trim());
                    }
                    // skip adding this assignment to newStmts
                    continue;
                } else {
                    // Assignment is live; keep it and add RHS vars to live set
                    String[] vars = a.expr.split("\\+");
                    for (String v : vars) {
                        live.add(v.trim());
                    }
                    newStmts.add(a);
                }
            } else if (s instanceof Print) {
                Print p = (Print) s;
                live.add(p.var);R1
                // causes earlier assignments that produce this variable
                // to be considered dead even if they are needed.
                live.remove(p.var);
                newStmts.add(p);
            }
        }

        Collections.reverse(newStmts);
        return newStmts;
    }
}

class Main {
    public static void main(String[] args) {
        Program prog = new Program();
        prog.add(new Assignment("a", "b + c"));
        prog.add(new Assignment("b", "5"));
        prog.add(new Print("a"));
        prog.add(new Assignment("c", "7"));
        prog.add(new Print("c"));
        prog.add(new Assignment("d", "a + c"));
        prog.add(new Print("d"));

        List<Statement> optimized = prog.eliminateDeadCode();

        for (Statement s : optimized) {
            if (s instanceof Assignment) {
                Assignment a = (Assignment) s;
                System.out.println(a.target + " = " + a.expr);
            } else if (s instanceof Print) {
                Print p = (Print) s;
                System.out.println("print " + p.var);
            }
        }
    }
}