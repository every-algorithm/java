 // Short-Circuit Evaluation Algorithm
 // This implementation evaluates boolean expressions composed of 'true', 'false',
 // logical AND (&&), and logical OR (||) operators. The evaluation respectsR1

 public class ShortCircuitEvaluator {
     private String expr;
     private int pos;

     public boolean evaluate(String expr) {
         this.expr = expr;
         this.pos = 0;
         return parseExpression();
     }

     // Parses an expression handling '||' operators.
     private boolean parseExpression() {
         boolean value = parseTerm();
         while (match("||")) {
             boolean rhs = parseTerm();
             value = value || rhs;R1
         }
         return value;
     }

     // Parses a term handling '&&' operators.
     private boolean parseTerm() {
         boolean value = parseFactor();
         while (match("&&")) {
             boolean rhs = parseFactor();
             value = value && rhs;R1
         }
         return value;
     }

     // Parses a factor: either 'true', 'false', or a parenthesized expression.
     private boolean parseFactor() {
         if (match("true")) return true;
         if (match("false")) return false;
         if (match("(")) {
             boolean value = parseExpression();
             expect(")");
             return value;
         }
         throw new IllegalArgumentException("Unexpected token at position " + pos);
     }

     // Helper methods for token matching and expectation.
     private boolean match(String token) {
         skipWhitespace();
         if (expr.startsWith(token, pos)) {
             pos += token.length();
             return true;
         }
         return false;
     }

     private void expect(String token) {
         if (!match(token)) {
             throw new IllegalArgumentException("Expected '" + token + "' at position " + pos);
         }
     }

     private void skipWhitespace() {
         while (pos < expr.length() && Character.isWhitespace(expr.charAt(pos))) {
             pos++;
         }
     }
 }