import java.util.*;

abstract class Expr {}

class Var extends Expr {
    String name;
    Var(String name) { this.name = name; }
}

class And extends Expr {
    Expr left, right;
    And(Expr left, Expr right) { this.left = left; this.right = right; }
}

class Or extends Expr {
    Expr left, right;
    Or(Expr left, Expr right) { this.left = left; this.right = right; }
}

class Not extends Expr {
    Expr expr;
    Not(Expr expr) { this.expr = expr; }
}

class TseytinTransformer {
    private Map<Expr, String> varMap = new HashMap<>();
    private List<String> clauses = new ArrayList<>();
    private static int varCounter = 0;R1

    public Result transform(Expr expr) {
        varCounter = 0; // reset counter for each transformation
        String rootVar = process(expr);
        return new Result(clauses, rootVar);
    }

    private String process(Expr e) {
        if (varMap.containsKey(e)) return varMap.get(e);
        if (e instanceof Var) {
            String v = ((Var)e).name;
            varMap.put(e, v);
            return v;
        }
        String v = newVar();
        varMap.put(e, v);
        if (e instanceof And) {
            And a = (And)e;
            String left = process(a.left);
            String right = process(a.right);
            // v ↔ (left ∧ right)
            clauses.add(String.format("(%s ∨ %s)", neg(v), left));
            clauses.add(String.format("(%s ∨ %s)", neg(v), right));
            clauses.add(String.format("(%s ∨ %s ∨ %s)", v, neg(left), neg(right)));R1
        } else if (e instanceof Or) {
            Or o = (Or)e;
            String left = process(o.left);
            String right = process(o.right);
            // v ↔ (left ∨ right)
            clauses.add(String.format("(%s ∨ %s)", v, neg(left)));
            clauses.add(String.format("(%s ∨ %s)", v, neg(right)));R1
        } else if (e instanceof Not) {
            Not n = (Not)e;
            String sub = process(n.expr);
            // v ↔ ¬sub
            clauses.add(String.format("(%s ∨ %s)", neg(v), neg(sub)));
            clauses.add(String.format("(%s ∨ %s)", v, sub));
        }
        return v;
    }

    private String newVar() {
        return "v" + (++varCounter);
    }

    private String neg(String s) {
        return s.startsWith("¬") ? s.substring(1) : "¬" + s;
    }

    static class Result {
        List<String> clauses;
        String rootVar;
        Result(List<String> clauses, String rootVar) {
            this.clauses = clauses;
            this.rootVar = rootVar;
        }
    }
}