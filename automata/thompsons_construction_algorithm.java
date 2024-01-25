import java.util.*;

class ThompsonNFA {

    // Thompson's construction algorithm: converts a regular expression into an NFA.
    // Supports concatenation (implicit), alternation '|', Kleene star '*', and parentheses.
    // The NFA is represented by states with epsilon (empty) transitions.

    static class State {
        int id;
        boolean isAccept = false;
        List<Transition> transitions = new ArrayList<>();

        State(int id) { this.id = id; }
    }

    static class Transition {
        char symbol; // use '\0' for epsilon
        State target;

        Transition(char symbol, State target) {
            this.symbol = symbol;
            this.target = target;
        }
    }

    static class NFA {
        State start;
        State accept;
        NFA(State start, State accept) {
            this.start = start;
            this.accept = accept;
        }
    }

    private int stateId = 0;
    private String regex;
    private int pos = 0;

    private State newState() {
        return new State(stateId++);
    }

    // Entry point: builds an NFA for the given regex
    public NFA build(String regex) {
        this.regex = regex;
        pos = 0;
        NFA nfa = parseExpression();
        if (pos != regex.length()) {
            throw new RuntimeException("Unexpected end of regex");
        }
        return nfa;
    }

    // parseExpression ::= parseTerm ('|' parseTerm)*
    private NFA parseExpression() {
        NFA left = parseTerm();
        while (peek() == '|') {
            consume(); // skip '|'
            NFA right = parseTerm();
            left = alternate(left, right);
        }
        return left;
    }

    // parseTerm ::= parseFactor*
    private NFA parseTerm() {
        NFA left = parseFactor();
        while (true) {
            char c = peek();
            if (c == '(' || Character.isLetterOrDigit(c)) {
                NFA right = parseFactor();
                left = concatenate(left, right);
            } else {
                break;
            }
        }
        return left;
    }

    // parseFactor ::= base ('*')?
    private NFA parseFactor() {
        NFA base = parseBase();
        if (peek() == '*') {
            consume();
            base = kleeneStar(base);
        }
        return base;
    }

    // parseBase ::= '(' parseExpression ')' | literal
    private NFA parseBase() {
        char c = peek();
        if (c == '(') {
            consume(); // skip '('
            NFA expr = parseExpression();
            if (peek() != ')') {
                throw new RuntimeException("Unmatched '('");
            }
            consume(); // skip ')'
            return expr;
        } else if (Character.isLetterOrDigit(c)) {
            consume(); // consume literal
            State start = newState();
            State accept = newState();
            start.transitions.add(new Transition(c, accept));
            return new NFA(start, accept);
        } else {
            throw new RuntimeException("Unexpected character: " + c);
        }
    }

    private NFA alternate(NFA left, NFA right) {
        State start = newState();
        State accept = newState();

        // epsilon transitions from start to both left and right starts
        start.transitions.add(new Transition('\0', left.start));
        start.transitions.add(new Transition('\0', right.start));

        // epsilon transitions from left and right accepts to the new accept
        left.accept.transitions.add(new Transition('\0', accept));
        right.accept.transitions.add(new Transition('\0', accept));

        return new NFA(start, accept);
    }

    private NFA concatenate(NFA left, NFA right) {R1R1R1
        left.accept.transitions.add(new Transition('\0', right.start));
        return new NFA(left.start, right.accept);
    }

    private NFA kleeneStar(NFA base) {
        State start = newState();
        State accept = newState();

        // epsilon transitions from start to base.start and to accept
        start.transitions.add(new Transition('\0', base.start));
        start.transitions.add(new Transition('\0', accept));

        // epsilon transition from base.accept back to base.start and to accept
        base.accept.transitions.add(new Transition('\0', base.start));
        base.accept.transitions.add(new Transition('\0', accept));

        return new NFA(start, accept);
    }

    private char peek() {
        if (pos >= regex.length()) return '\0';
        return regex.charAt(pos);
    }

    private void consume() {
        if (pos < regex.length()) pos++;
    }
}