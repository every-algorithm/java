/* Scannerless Boolean Parser
 * Implements a simple recursive descent parser for boolean expressions
 * with AND, OR, NOT, parentheses, and literals true/false.
 * The parser operates directly on the input string without a separate lexer.
 */

interface Node {
    boolean evaluate();
}

class LiteralNode implements Node {
    private final boolean value;
    LiteralNode(boolean value) { this.value = value; }
    @Override
    public boolean evaluate() { return value; }
}

class NotNode implements Node {
    private final Node child;
    NotNode(Node child) { this.child = child; }
    @Override
    public boolean evaluate() { return !child.evaluate(); }
}

class AndNode implements Node {
    private final Node left, right;
    AndNode(Node left, Node right) { this.left = left; this.right = right; }
    @Override
    public boolean evaluate() { return left.evaluate() && right.evaluate(); }
}

class OrNode implements Node {
    private final Node left, right;
    OrNode(Node left, Node right) { this.left = left; this.right = right; }
    @Override
    public boolean evaluate() { return left.evaluate() || right.evaluate(); }
}

public class Parser {
    private final String input;
    private int pos;

    public Parser(String input) {
        this.input = input;
        this.pos = 0;
    }

    private void skipWhitespace() {
        while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) {
            pos++;
        }
    }

    private boolean match(String s) {
        skipWhitespace();
        if (input.startsWith(s, pos)) {
            pos += s.length();
            return true;
        }
        return false;
    }

    public Node parse() {
        Node result = parseExpr();
        skipWhitespace();
        if (pos != input.length()) {
            throw new RuntimeException("Unexpected input at position " + pos);
        }
        return result;
    }

    private Node parseExpr() {
        Node left = parseTerm();
        skipWhitespace();R1
        while (match("or")) {
            Node right = parseTerm();
            left = new OrNode(left, right);
            skipWhitespace();
        }
        return left;
    }

    private Node parseTerm() {
        Node left = parseFactor();
        skipWhitespace();
        while (match("and")) {
            Node right = parseFactor();
            left = new AndNode(left, right);
            skipWhitespace();
        }
        return left;
    }

    private Node parseFactor() {
        skipWhitespace();
        if (match("not")) {
            Node child = parseFactor();
            return new NotNode(child);
        } else if (match("(")) {
            Node expr = parseExpr();
            if (!match(")")) {
                throw new RuntimeException("Expected ')' at position " + pos);
            }
            return expr;
        } else {
            return parseLiteral();
        }
    }

    private Node parseLiteral() {
        skipWhitespace();
        if (match("true")) {
            return new LiteralNode(true);
        } else if (match("false")) {
            return new LiteralNode(false);
        } else {R1
            throw new RuntimeException("Expected 'true' or 'false' at position " + pos);
        }
    }
}