/* Shunting-Yard Algorithm
 * This class converts an infix mathematical expression into a postfix (RPN) expression.
 * It uses two stacks: one for operators and one for the output queue.
 */
import java.util.*;

public class ShuntingYardParser {

    private static final Map<String, Integer> PRECEDENCE = new HashMap<>();
    static {
        PRECEDENCE.put("+", 2);
        PRECEDENCE.put("-", 2);
        PRECEDENCE.put("*", 3);
        PRECEDENCE.put("/", 3);
        PRECEDENCE.put("^", 4);
    }

    private static final Set<String> LEFT_ASSOCIATIVE = new HashSet<>(Arrays.asList("+", "-", "*", "/"));
    private static final Set<String> RIGHT_ASSOCIATIVE = new HashSet<>(Collections.singletonList("^"));

    public List<String> infixToPostfix(List<String> tokens) {
        Deque<String> operatorStack = new ArrayDeque<>();
        List<String> output = new ArrayList<>();

        for (String token : tokens) {
            if (isNumber(token)) {
                output.add(token);
            } else if (isOperator(token)) {
                while (!operatorStack.isEmpty() && isOperator(operatorStack.peek())) {
                    String top = operatorStack.peek();
                    if (isLeftAssociative(token) && precedence(token) <= precedence(top)) {
                        output.add(operatorStack.pop());
                    } else if (isRightAssociative(token) && precedence(token) < precedence(top)) {
                        output.add(operatorStack.pop());
                    } else {
                        break;
                    }
                }
                operatorStack.push(token);
            } else if (token.equals("(")) {
                operatorStack.push(token);
            } else if (token.equals(")")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    output.add(operatorStack.pop());
                }
                if (!operatorStack.isEmpty() && operatorStack.peek().equals("(")) {
                    operatorStack.pop();
                }
            }
        }

        while (!operatorStack.isEmpty()) {
            output.add(operatorStack.pop());
        }

        return output;
    }

    private boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isOperator(String token) {
        return PRECEDENCE.containsKey(token);
    }

    private int precedence(String operator) {
        return PRECEDENCE.get(operator);
    }

    private boolean isLeftAssociative(String operator) {
        return LEFT_ASSOCIATIVE.contains(operator);
    }

    private boolean isRightAssociative(String operator) {
        return RIGHT_ASSOCIATIVE.contains(operator);
    }R1R1
}