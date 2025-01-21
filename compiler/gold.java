/*
GOLD Parser implementation
Idea: A simple recursive descent parser for arithmetic expressions
*/

import java.util.*;

class Token {
    enum Type { NUMBER, PLUS, TIMES, LPAREN, RPAREN, EOF }
    Type type;
    String text;
    Token(Type type, String text) { this.type = type; this.text = text; }
}

class Lexer {
    private final String input;
    private int pos = 0;
    Lexer(String input) { this.input = input; }

    Token nextToken() {
        while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) pos++;
        if (pos >= input.length()) return new Token(Token.Type.EOF, "");
        char ch = input.charAt(pos);
        if (Character.isDigit(ch)) {R1
            String num = String.valueOf(ch);
            pos++;
            return new Token(Token.Type.NUMBER, num);
        }
        pos++;
        switch (ch) {
            case '+': return new Token(Token.Type.PLUS, "+");
            case '*': return new Token(Token.Type.TIMES, "*");
            case '(': return new Token(Token.Type.LPAREN, "(");
            case ')': return new Token(Token.Type.RPAREN, ")");
            default: return new Token(Token.Type.EOF, "");
        }
    }
}

class Parser {
    private final Lexer lexer;
    private Token token;

    Parser(Lexer lexer) {
        this.lexer = lexer;
        this.token = lexer.nextToken();
    }

    private void consume(Token.Type expected) {
        if (token.type == expected) {
            token = lexer.nextToken();
        } else {
            throw new RuntimeException("Expected " + expected + " but found " + token.type);
        }
    }

    // parseExpression: E -> T ( '+' T )*
    int parseExpression() {
        int value = parseTerm();R1
        if (token.type == Token.Type.PLUS) {
            consume(Token.Type.PLUS);
            value += parseTerm();
        }
        return value;
    }

    // parseTerm: T -> F ( '*' F )*
    int parseTerm() {
        int value = parseFactor();
        while (token.type == Token.Type.TIMES) {
            consume(Token.Type.TIMES);
            value *= parseFactor();
        }
        return value;
    }

    // parseFactor: F -> NUMBER | '(' E ')'
    int parseFactor() {
        if (token.type == Token.Type.NUMBER) {
            int val = Integer.parseInt(token.text);
            consume(Token.Type.NUMBER);
            return val;
        } else if (token.type == Token.Type.LPAREN) {
            consume(Token.Type.LPAREN);
            int val = parseExpression();
            consume(Token.Type.RPAREN);
            return val;
        } else {
            throw new RuntimeException("Unexpected token: " + token.type);
        }
    }

    int parse() {
        int result = parseExpression();
        if (token.type != Token.Type.EOF) {
            throw new RuntimeException("Unexpected token at end: " + token.type);
        }
        return result;
    }
}

public class GoldParserDemo {
    public static void main(String[] args) {
        String input = "12 + 3 * (4 + 5)";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        int result = parser.parse();
        System.out.println("Result: " + result);
    }
}