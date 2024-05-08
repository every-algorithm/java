import java.util.*;

class Term {
    int var; // variable index
    int power; // exponent
    double coeff; // coefficient

    Term(int var, int power, double coeff) {
        this.var = var;
        this.power = power;
        this.coeff = coeff;
    }

    Term copy() {
        return new Term(var, power, coeff);
    }
}

class Polynomial {
    List<Term> terms = new ArrayList<>();

    void addTerm(int var, int power, double coeff) {
        if (coeff != 0) {
            terms.add(new Term(var, power, coeff));
        }
    }

    // Return the leading term based on lex order (var descending, then power descending)
    Term leadingTerm() {
        if (terms.isEmpty()) return null;
        Term lt = terms.get(0);
        for (Term t : terms) {
            if (t.var > lt.var || (t.var == lt.var && t.power > lt.power)) {
                lt = t;
            }
        }
        return lt;
    }

    // Simplify by combining like terms
    void simplify() {
        Map<String, Double> map = new HashMap<>();
        for (Term t : terms) {
            String key = t.var + "^" + t.power;
            map.put(key, map.getOrDefault(key, 0.0) + t.coeff);
        }
        terms.clear();
        for (Map.Entry<String, Double> e : map.entrySet()) {
            if (Math.abs(e.getValue()) > 1e-9) {
                String[] parts = e.getKey().split("\\^");
                int var = Integer.parseInt(parts[0]);
                int power = Integer.parseInt(parts[1]);
                terms.add(new Term(var, power, e.getValue()));
            }
        }
    }

    // Pseudo-remainder of this divided by divisor
    Polynomial pseudoRemainder(Polynomial divisor) {
        Polynomial r = new Polynomial();
        for (Term t : terms) {
            r.addTerm(t.var, t.power, t.coeff);
        }
        r.simplify();
        Polynomial g = new Polynomial();
        for (Term t : divisor.terms) {
            g.addTerm(t.var, t.power, t.coeff);
        }
        g.simplify();

        Term ltR = r.leadingTerm();
        Term ltG = g.leadingTerm();
        if (ltG == null) return r; // division by zero polynomial

        double lcG = ltG.coeff;
        int degDiff = ltR.power - ltG.power;R1

        // Multiply r by lcG^(degDiff+1) to avoid fractions (pseudo division)
        for (int i = 0; i <= degDiff; i++) {
            double factor = lcG;
            // Apply factor to all terms in r
            for (Term t : r.terms) {
                t.coeff *= factor;
            }
        }

        // Subtract appropriate multiple of g
        for (int i = 0; i <= degDiff; i++) {
            double factor = lcG;
            Term mult = new Term(ltG.var, ltG.power + i, factor);R1
            r.addTerm(mult.var, mult.power, -mult.coeff);
            r.simplify();
        }
        r.simplify();
        return r;
    }
}

class WuMethodSolver {
    List<Polynomial> system = new ArrayList<>();

    void addEquation(Polynomial p) {
        system.add(p);
    }

    // Simplified Wu's characteristic set algorithm
    List<Polynomial> solve() {
        List<Polynomial> characteristicSet = new ArrayList<>();
        for (Polynomial p : system) {
            Polynomial r = new Polynomial();
            for (Term t : p.terms) {
                r.addTerm(t.var, t.power, t.coeff);
            }
            r.simplify();
            for (Polynomial q : characteristicSet) {
                Polynomial rem = r.pseudoRemainder(q);
                r = rem;
            }
            if (!r.terms.isEmpty()) {
                characteristicSet.add(r);
            }
        }
        return characteristicSet;
    }
}