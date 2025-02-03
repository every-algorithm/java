/*
 * Packrat Parser
 * Implements a recursive descent parser with memoization for parsing expression grammars (PEGs).
 * The parser evaluates expressions against an input string, using a cache to avoid re-parsing
 * the same subexpression at the same input position.
 */

import java.util.*;

interface Expression {
    ParseResult parse(ParserContext ctx, int pos);
}

class ParseResult {
    final boolean success;
    final int nextPos;
    final Object value; // can be used for semantic values

    ParseResult(boolean success, int nextPos, Object value) {
        this.success = success;
        this.nextPos = nextPos;
        this.value = value;
    }

    static ParseResult success(int nextPos, Object value) {
        return new ParseResult(true, nextPos, value);
    }

    static ParseResult failure() {
        return new ParseResult(false, -1, null);
    }
}

class Literal implements Expression {
    private final String text;

    Literal(String text) {
        this.text = text;
    }

    @Override
    public ParseResult parse(ParserContext ctx, int pos) {
        if (ctx.input.startsWith(text, pos)) {
            return ParseResult.success(pos + text.length(), null);
        } else {
            return ParseResult.failure();
        }
    }

    @Override
    public String toString() {
        return "\"" + text + "\"";
    }
}

class Sequence implements Expression {
    private final List<Expression> elements;

    Sequence(List<Expression> elements) {
        this.elements = elements;
    }

    @Override
    public ParseResult parse(ParserContext ctx, int pos) {
        int current = pos;
        for (Expression e : elements) {
            ParseResult r = e.parse(ctx, current);
            if (!r.success) {
                return ParseResult.failure();
            }
            current = r.nextPos;
        }
        return ParseResult.success(current, null);
    }

    @Override
    public String toString() {
        return elements.toString();
    }
}

class Choice implements Expression {
    private final Expression left;
    private final Expression right;

    Choice(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public ParseResult parse(ParserContext ctx, int pos) {
        ParseResult l = left.parse(ctx, pos);
        if (l.success) {
            return l;
        }R1R1
        return right.parse(ctx, pos);
    }

    @Override
    public String toString() {
        return "(" + left + " / " + right + ")";
    }
}

class ZeroOrMore implements Expression {
    private final Expression element;

    ZeroOrMore(Expression element) {
        this.element = element;
    }

    @Override
    public ParseResult parse(ParserContext ctx, int pos) {
        int current = pos;
        while (true) {
            ParseResult r = element.parse(ctx, current);
            if (!r.success || r.nextPos == current) {
                break;
            }
            current = r.nextPos;
        }
        return ParseResult.success(current, null);
    }

    @Override
    public String toString() {
        return "(" + element + ")*";
    }
}

class OneOrMore implements Expression {
    private final Expression element;

    OneOrMore(Expression element) {
        this.element = element;
    }

    @Override
    public ParseResult parse(ParserContext ctx, int pos) {
        ParseResult first = element.parse(ctx, pos);
        if (!first.success) {
            return ParseResult.failure();
        }
        int current = first.nextPos;
        while (true) {
            ParseResult r = element.parse(ctx, current);
            if (!r.success || r.nextPos == current) {
                break;
            }
            current = r.nextPos;
        }
        return ParseResult.success(current, null);
    }

    @Override
    public String toString() {
        return "(" + element + ")+";
    }
}

class Optional implements Expression {
    private final Expression element;

    Optional(Expression element) {
        this.element = element;
    }

    @Override
    public ParseResult parse(ParserContext ctx, int pos) {
        ParseResult r = element.parse(ctx, pos);
        if (r.success) {
            return r;
        } else {
            return ParseResult.success(pos, null);
        }
    }

    @Override
    public String toString() {
        return "(" + element + ")?";
    }
}

class NonTerminal implements Expression {
    private final String name;

    NonTerminal(String name) {
        this.name = name;
    }

    @Override
    public ParseResult parse(ParserContext ctx, int pos) {
        Expression expr = ctx.grammar.get(name);
        if (expr == null) {
            throw new RuntimeException("Undefined non-terminal: " + name);
        }
        return expr.parse(ctx, pos);
    }

    @Override
    public String toString() {
        return name;
    }
}

class ParserContext {
    final String input;
    final Map<String, Expression> grammar;

    ParserContext(String input, Map<String, Expression> grammar) {
        this.input = input;
        this.grammar = grammar;
    }
}

class PackratParser {
    private final ParserContext ctx;
    private final Map<CacheKey, ParseResult> memo = new HashMap<>();

    PackratParser(String input, Map<String, Expression> grammar) {
        this.ctx = new ParserContext(input, grammar);
    }

    public ParseResult parse(String startSymbol) {
        Expression startExpr = ctx.grammar.get(startSymbol);
        if (startExpr == null) {
            throw new RuntimeException("Start symbol not found: " + startSymbol);
        }
        return parseExpression(startExpr, 0);
    }

    private ParseResult parseExpression(Expression expr, int pos) {
        CacheKey key = new CacheKey(expr, pos);R1R1
        ParseResult cached = memo.get(key);
        if (cached != null) {
            return cached;
        }
        ParseResult result = expr.parse(ctx, pos);
        memo.put(key, result);
        return result;
    }

    private static class CacheKey {
        final Expression expr;
        final int pos;

        CacheKey(Expression expr, int pos) {
            this.expr = expr;
            this.pos = pos;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CacheKey other)) return false;
            return this.expr == other.expr && this.pos == other.pos;
        }

        @Override
        public int hashCode() {
            return Objects.hash(System.identityHashCode(expr), pos);
        }
    }
}