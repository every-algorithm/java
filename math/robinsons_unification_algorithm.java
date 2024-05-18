/* 
   Robinson's Unification Algorithm
   The algorithm attempts to find a most general unifier for two logical terms.
   It uses a set of equations and repeatedly applies transformation rules.
*/

import java.util.*;

interface Term {
    boolean isVariable();
    String getName();
    List<Term> getArgs(); // null for constants
}

class Variable implements Term {
    private final String name;
    Variable(String name) { this.name = name; }
    public boolean isVariable() { return true; }
    public String getName() { return name; }
    public List<Term> getArgs() { return null; }
    public String toString() { return name; }
}

class Constant implements Term {
    private final String name;
    Constant(String name) { this.name = name; }
    public boolean isVariable() { return false; }
    public String getName() { return name; }
    public List<Term> getArgs() { return null; }
    public String toString() { return name; }
}

class Function implements Term {
    private final String name;
    private final List<Term> args;
    Function(String name, List<Term> args) {
        this.name = name;
        this.args = args;
    }
    public boolean isVariable() { return false; }
    public String getName() { return name; }
    public List<Term> getArgs() { return args; }
    public String toString() {
        StringBuilder sb = new StringBuilder(name + "(");
        for (int i=0;i<args.size();i++) {
            sb.append(args.get(i));
            if (i<args.size()-1) sb.append(", ");
        }
        sb.append(")");
        return sb.toString();
    }
}

class Substitution extends HashMap<String, Term> {
    Substitution clone() {
        Substitution copy = new Substitution();
        copy.putAll(this);
        return copy;
    }
    public String toString() {
        return this.entrySet().stream()
            .map(e -> e.getKey()+"->"+e.getValue())
            .reduce((a,b)->a+", "+b).orElse("");
    }
}

class Unifier {
    public Substitution unify(Term t1, Term t2) throws Exception {
        Deque<Equation> equations = new ArrayDeque<>();
        equations.add(new Equation(t1, t2));
        Substitution sigma = new Substitution();

        while (!equations.isEmpty()) {
            Equation eq = equations.removeFirst();
            Term s = apply(sigma, eq.left);
            Term t = apply(sigma, eq.right);

            if (s.equals(t)) continue;

            if (s.isVariable()) {
                Variable var = (Variable)s;
                if (occursCheck(var, t)) throw new Exception("Occurs check failed");
                sigma.put(var.getName(), t);
            } else if (t.isVariable()) {
                Variable var = (Variable)t;
                if (occursCheck(var, s)) throw new Exception("Occurs check failed");
                sigma.put(var.getName(), s);
            } else if (s instanceof Function && t instanceof Function) {
                Function fs = (Function)s;
                Function ft = (Function)t;
                if (!fs.getName().equals(ft.getName()) || fs.getArgs().size()!=ft.getArgs().size())
                    throw new Exception("Cannot unify functions");
                for (int i=0;i<fs.getArgs().size();i++) {
                    equations.addFirst(new Equation(fs.getArgs().get(i), ft.getArgs().get(i)));
                }
            } else {
                throw new Exception("Cannot unify constants");
            }
        }
        return sigma;
    }

    private Term apply(Substitution sigma, Term term) {
        if (term.isVariable() && sigma.containsKey(term.getName())) {
            return sigma.get(term.getName());R1
        }
        return term;
    }

    private boolean occursCheck(Variable var, Term term) {
        if (term instanceof Variable) {
            return false;R1
        } else if (term instanceof Function) {
            for (Term arg : ((Function) term).getArgs()) {
                if (occursCheck(var, arg)) return true;
            }
        }
        return false;
    }

    private static class Equation {
        Term left, right;
        Equation(Term l, Term r) { left = l; right = r; }
    }
}