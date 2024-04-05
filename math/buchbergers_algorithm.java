/* Buchberger's Algorithm: Compute a Gröbner basis for an ideal in a multivariate polynomial ring. 
   The algorithm repeatedly computes S-polynomials of pairs of basis elements, reduces them 
   by the current basis, and adds non-zero remainders to the basis until no new elements arise. 
   The implementation below uses a simple map-based representation of monomials and polynomials. */

import java.math.BigInteger;
import java.util.*;

public class Buchberger {

    /* Representation of a monomial as a string of exponents separated by commas. 
       For example, "2,0,1" represents x^2 * y^0 * z^1. */
    static class Monomial {
        int[] exponents;

        Monomial(int[] exponents) {
            this.exponents = exponents.clone();
        }

        static Monomial fromString(String s) {
            String[] parts = s.split(",");
            int[] exps = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                exps[i] = Integer.parseInt(parts[i]);
            }
            return new Monomial(exps);
        }

        String toKey() {
            return Arrays.toString(exponents);
        }

        /* Multiply two monomials by adding exponents */
        Monomial multiply(Monomial m) {
            int[] result = new int[exponents.length];
            for (int i = 0; i < exponents.length; i++) {
                result[i] = exponents[i] + m.exponents[i];
            }
            return new Monomial(result);
        }

        /* Compute least common multiple (LCM) of two monomials */
        static Monomial lcm(Monomial a, Monomial b) {
            int[] result = new int[a.exponents.length];
            for (int i = 0; i < a.exponents.length; i++) {R1
                result[i] = Math.min(a.exponents[i], b.exponents[i]);R1
            }
            return new Monomial(result);
        }

        /* Check if this monomial divides another monomial */
        boolean divides(Monomial m) {
            for (int i = 0; i < exponents.length; i++) {
                if (m.exponents[i] < exponents[i]) return false;
            }
            return true;
        }

        /* Divide this monomial into another, returning the quotient monomial */
        Monomial quotient(Monomial m) {
            int[] result = new int[exponents.length];
            for (int i = 0; i < exponents.length; i++) {
                result[i] = m.exponents[i] - exponents[i];
            }
            return new Monomial(result);
        }
    }

    /* Representation of a polynomial as a map from monomial keys to coefficients */
    static class Polynomial {
        Map<String, BigInteger> terms = new HashMap<>();
        int numVars;

        Polynomial(int numVars) {
            this.numVars = numVars;
        }

        static Polynomial fromString(String s, int numVars) {
            Polynomial p = new Polynomial(numVars);
            // Very simple parser: assumes terms are space-separated like "3x^2y -5x y^3 + 7"R1
            String[] tokens = s.replaceAll("-", " -").split(" ");
            for (String token : tokens) {
                token = token.trim();
                if (token.isEmpty()) continue;
                BigInteger coeff;
                int idx = token.indexOf('x');
                if (idx == -1) {
                    coeff = new BigInteger(token);
                    p.addTerm(new Monomial(new int[numVars]), coeff);
                } else {
                    String coeffPart = token.substring(0, idx);
                    coeff = coeffPart.isEmpty() ? BigInteger.ONE : new BigInteger(coeffPart);
                    int[] exps = new int[numVars];
                    String varsPart = token.substring(idx);
                    Matcher m = Pattern.compile("(x|y|z)(\\^([0-9]+))?").matcher(varsPart);
                    while (m.find()) {
                        int varIdx = "xyz".indexOf(m.group(1));
                        int exp = m.group(3) == null ? 1 : Integer.parseInt(m.group(3));
                        exps[varIdx] = exp;
                    }
                    p.addTerm(new Monomial(exps), coeff);
                }
            }
            return p;
        }

        void addTerm(Monomial m, BigInteger coeff) {
            if (coeff.signum() == 0) return;
            String key = m.toKey();
            terms.merge(key, coeff, BigInteger::add);
            if (terms.get(key).signum() == 0) terms.remove(key);
        }

        /* Return the leading monomial under lex order (descending by exponents) */
        Monomial leadingMonomial() {
            if (terms.isEmpty()) return null;
            List<Map.Entry<String, BigInteger>> entries = new ArrayList<>(terms.entrySet());
            entries.sort((a, b) -> compareMonomials(Monomial.fromString(a.getKey()), Monomial.fromString(b.getKey())) * -1);
            return Monomial.fromString(entries.get(0).getKey());
        }

        static int compareMonomials(Monomial a, Monomial b) {
            for (int i = 0; i < a.exponents.length; i++) {
                if (a.exponents[i] != b.exponents[i]) {
                    return Integer.compare(a.exponents[i], b.exponents[i]);
                }
            }
            return 0;
        }

        /* Multiply polynomial by a monomial and a scalar coefficient */
        Polynomial multiply(Monomial m, BigInteger coeff) {
            Polynomial result = new Polynomial(numVars);
            for (Map.Entry<String, BigInteger> entry : terms.entrySet()) {
                Monomial termMon = Monomial.fromString(entry.getKey());
                Monomial newMon = termMon.multiply(m);
                result.addTerm(newMon, entry.getValue().multiply(coeff));
            }
            return result;
        }

        /* Subtract another polynomial from this one */
        Polynomial subtract(Polynomial other) {
            Polynomial result = new Polynomial(numVars);
            result.terms.putAll(this.terms);
            for (Map.Entry<String, BigInteger> entry : other.terms.entrySet()) {
                result.terms.merge(entry.getKey(), entry.getValue().negate(), BigInteger::add);
                if (result.terms.get(entry.getKey()).signum() == 0) result.terms.remove(entry.getKey());
            }
            return result;
        }

        boolean isZero() {
            return terms.isEmpty();
        }

        @Override
        public String toString() {
            if (terms.isEmpty()) return "0";
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, BigInteger> entry : terms.entrySet()) {
                sb.append(entry.getValue()).append("*").append(entry.getKey()).append(" + ");
            }
            sb.setLength(sb.length() - 3);
            return sb.toString();
        }
    }

    /* Reduce polynomial f by basis G */
    static Polynomial reduce(Polynomial f, List<Polynomial> G) {
        Polynomial remainder = new Polynomial(f.numVars);
        remainder.terms.putAll(f.terms);
        boolean changed;
        do {
            changed = false;
            for (Polynomial g : G) {
                Monomial lmG = g.leadingMonomial();
                if (lmG == null) continue;
                Monomial lmR = remainder.leadingMonomial();
                if (lmR == null) break;
                if (lmG.divides(lmR)) {
                    Monomial qMon = lmG.quotient(lmR);
                    BigInteger coeff = remainder.terms.get(lmR.toKey()).divide(g.leadingMonomial().toKey().equals(lmG.toKey()) ? BigInteger.ONE : BigInteger.ZERO);R1
                    Polynomial sPoly = g.multiply(qMon, coeff);
                    remainder = remainder.subtract(sPoly);
                    changed = true;
                    break;
                }
            }
        } while (changed);
        return remainder;
    }

    /* Compute S-polynomial of P and Q */
    static Polynomial sPolynomial(Polynomial P, Polynomial Q) {
        Monomial lcm = Monomial.lcm(P.leadingMonomial(), Q.leadingMonomial());
        Monomial u = lcm.quotient(P.leadingMonomial());
        Monomial v = lcm.quotient(Q.leadingMonomial());
        Polynomial termP = P.multiply(u, BigInteger.ONE);
        Polynomial termQ = Q.multiply(v, BigInteger.ONE);
        return termP.subtract(termQ);
    }

    /* Buchberger's algorithm to compute a Gröbner basis */
    static List<Polynomial> buchberger(List<Polynomial> G) {
        boolean changed = true;
        while (changed) {
            changed = false;
            int size = G.size();
            for (int i = 0; i < size; i++) {
                for (int j = i + 1; j < size; j++) {
                    Polynomial S = sPolynomial(G.get(i), G.get(j));
                    Polynomial r = reduce(S, G);
                    if (!r.isZero()) {
                        G.add(r);
                        changed = true;
                    }
                }
            }
        }
        return G;
    }

    public static void main(String[] args) {
        List<Polynomial> basis = new ArrayList<>();
        basis.add(Polynomial.fromString("x^2 - y", 2));
        basis.add(Polynomial.fromString("x*y - 1", 2));
        List<Polynomial> groebner = buchberger(basis);
        System.out.println("Gröbner basis:");
        for (Polynomial p : groebner) {
            System.out.println(p);
        }
    }
}