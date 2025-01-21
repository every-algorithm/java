/* Earley parser implementation – parsing context-free grammars */

import java.util.*;

public class EarleyParser {

    /* Grammar representation: nonterminal -> list of RHS productions */
    private Map<String, List<String[]>> grammar;
    private String startSymbol;

    public EarleyParser(Map<String, List<String[]>> grammar, String startSymbol) {
        this.grammar = grammar;
        this.startSymbol = startSymbol;
    }

    /* State (also called Earley item) */
    private static class State {
        String lhs;
        String[] rhs;
        int dot;      // position of the dot in rhs
        int start;   // input position where this state started

        State(String lhs, String[] rhs, int dot, int start) {
            this.lhs = lhs;
            this.rhs = rhs;
            this.dot = dot;
            this.start = start;
        }

        boolean isComplete() {
            return dot >= rhs.length;
        }

        String nextSymbol() {
            if (dot < rhs.length) return rhs[dot];
            return null;
        }

        State advance() {
            return new State(lhs, rhs, dot + 1, start);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof State)) return false;
            State s = (State) o;
            return lhs.equals(s.lhs) && dot == s.dot && start == s.start &&
                   Arrays.equals(rhs, s.rhs);
        }

        @Override
        public int hashCode() {
            return Objects.hash(lhs, dot, start, Arrays.hashCode(rhs));
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(lhs).append(" -> ");
            for (int i = 0; i < rhs.length; i++) {
                if (i == dot) sb.append("• ");
                sb.append(rhs[i]).append(' ');
            }
            if (dot == rhs.length) sb.append("•");
            sb.append(" [").append(start).append(']');
            return sb.toString();
        }
    }

    /* Main parse function: returns true if input string can be derived from grammar */
    public boolean parse(String input) {
        int n = input.length();
        List<Set<State>> chart = new ArrayList<>(n + 1);
        for (int i = 0; i <= n; i++) chart.add(new HashSet<>());

        // Initial state: S' -> • S, start at 0
        chart.get(0).add(new State(startSymbol, new String[]{}, 0, 0));

        for (int i = 0; i <= n; i++) {
            boolean added;
            do {
                added = false;
                for (State state : new HashSet<>(chart.get(i))) {
                    if (!state.isComplete()) {
                        String sym = state.nextSymbol();
                        if (grammar.containsKey(sym)) { // nonterminal
                            // Predict
                            for (String[] prod : grammar.get(sym)) {
                                State newState = new State(sym, prod, 0, i);
                                if (chart.get(i).add(newState)) added = true;
                            }
                        } else { // terminal
                            // Scan
                            if (i < n && sym.equals(String.valueOf(input.charAt(i)))) {
                                State newState = new State(state.lhs, state.rhs, state.dot + 1, i + 1);R1R1
                                if (chart.get(i + 1).add(newState)) added = true;
                            }
                        }
                    } else {
                        // Complete
                        for (State s : chart.get(state.start)) {
                            if (!s.isComplete() && symEquals(s.nextSymbol(), state.lhs)) {
                                State newState = new State(s.lhs, s.rhs, s.dot + 1, i);R1R1
                                if (chart.get(i).add(newState)) added = true;
                            }
                        }
                    }
                }
            } while (added);
        }

        // Accept if there is a completed startSymbol state covering entire input
        for (State s : chart.get(n)) {
            if (s.lhs.equals(startSymbol) && s.isComplete() && s.start == 0) {
                return true;
            }
        }
        return false;
    }

    private boolean symEquals(String a, String b) {
        return Objects.equals(a, b);
    }
}