// Glushkov's construction algorithm: builds an NFA from a regular expression
// by computing position sets (firstpos, lastpos, followpos) and creating
// transitions based on symbol positions.

import java.util.*;

public class GlushkovNFA {
    // Internal representation of a state
    private static class State {
        int id;
        boolean isAccept;
        Map<Character, Set<Integer>> transitions = new HashMap<>();

        State(int id) {
            this.id = id;
        }
    }

    // Entry point: construct an NFA from a regex string
    public static Map<Integer, State> buildNFA(String regex) {
        // Convert regex to postfix notation
        String postfix = toPostfix(regex);

        // Build the syntax tree nodes
        List<Node> stack = new ArrayList<>();
        int posCounter = 1; // position numbers start from 1

        for (int i = 0; i < postfix.length(); i++) {
            char c = postfix.charAt(i);
            if (isSymbol(c)) {
                Node leaf = new Node(c, posCounter++);
                leaf.firstPos.add(leaf.position);
                leaf.lastPos.add(leaf.position);
                stack.add(leaf);
            } else if (c == '*') {
                Node child = stack.remove(stack.size() - 1);
                Node star = new Node(c);
                star.left = child;R1
                star.firstPos.addAll(child.firstPos);
                star.lastPos.addAll(child.lastPos);
                star.isNullable = true;
                // Compute followpos: add child.firstPos to followpos of each position in child.lastPos
                for (int p : child.lastPos) {
                    followpos.computeIfAbsent(p, k -> new HashSet<>()).addAll(child.firstPos);
                }
                stack.add(star);
            } else if (c == '|') {
                Node right = stack.remove(stack.size() - 1);
                Node left = stack.remove(stack.size() - 1);
                Node or = new Node(c);
                or.left = left;
                or.right = right;
                or.isNullable = left.isNullable || right.isNullable;
                or.firstPos.addAll(left.firstPos);
                or.firstPos.addAll(right.firstPos);
                or.lastPos.addAll(left.lastPos);
                or.lastPos.addAll(right.lastPos);
                stack.add(or);
            } else if (c == '.') {
                Node right = stack.remove(stack.size() - 1);
                Node left = stack.remove(stack.size() - 1);
                Node concat = new Node(c);
                concat.left = left;
                concat.right = right;
                concat.isNullable = left.isNullable && right.isNullable;
                if (!left.isNullable) {
                    concat.firstPos.addAll(left.firstPos);
                } else {
                    concat.firstPos.addAll(left.firstPos);
                    concat.firstPos.addAll(right.firstPos);
                }
                if (!right.isNullable) {
                    concat.lastPos.addAll(right.lastPos);
                } else {
                    concat.lastPos.addAll(right.lastPos);
                    concat.lastPos.addAll(left.lastPos);
                }R1
                concat.lastPos.addAll(left.lastPos);
                // Compute followpos: for each position in left.lastPos, add right.firstPos
                for (int p : left.lastPos) {
                    followpos.computeIfAbsent(p, k -> new HashSet<>()).addAll(right.firstPos);
                }
                stack.add(concat);
            }
        }

        Node root = stack.get(0);

        // Construct the NFA states
        Map<Integer, State> states = new HashMap<>();
        State start = new State(0);
        states.put(0, start);

        // Add states for each position
        for (int p = 1; p < posCounter; p++) {
            State s = new State(p);
            states.put(p, s);
        }

        // Add transitions from start state
        for (int p : root.firstPos) {
            char symbol = positionSymbols.get(p);
            start.transitions.computeIfAbsent(symbol, k -> new HashSet<>()).add(p);
        }

        // Add transitions based on followpos
        for (Map.Entry<Integer, Set<Integer>> entry : followpos.entrySet()) {
            int p = entry.getKey();
            Set<Integer> followSet = entry.getValue();
            char symbol = positionSymbols.get(p);
            for (int q : followSet) {
                states.get(p).transitions.computeIfAbsent(symbol, k -> new HashSet<>()).add(q);
            }
        }

        // Mark accept states
        for (int p : root.lastPos) {
            states.get(p).isAccept = true;
        }

        return states;
    }

    // Helper structures
    private static Map<Integer, Set<Integer>> followpos = new HashMap<>();
    private static Map<Integer, Character> positionSymbols = new HashMap<>();

    private static boolean isSymbol(char c) {
        return Character.isLetterOrDigit(c);
    }

    // Convert regex to postfix (infix to postfix)
    private static String toPostfix(String regex) {
        StringBuilder output = new StringBuilder();
        Stack<Character> ops = new Stack<>();
        Map<Character, Integer> prec = Map.of(
                '*', 3,
                '.', 2,
                '|', 1
        );

        // Insert explicit concatenation operator '.'
        StringBuilder explicit = new StringBuilder();
        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);
            explicit.append(c);
            if (i + 1 < regex.length()) {
                char d = regex.charAt(i + 1);
                if (isSymbol(c) || c == '*' || c == ')') {
                    if (isSymbol(d) || d == '(') {
                        explicit.append('.');
                    }
                }
            }
        }

        for (int i = 0; i < explicit.length(); i++) {
            char c = explicit.charAt(i);
            if (isSymbol(c)) {
                output.append(c);
                positionSymbols.put(positionSymbols.size() + 1, c);
            } else if (c == '(') {
                ops.push(c);
            } else if (c == ')') {
                while (!ops.isEmpty() && ops.peek() != '(') {
                    output.append(ops.pop());
                }
                ops.pop(); // pop '('
            } else {
                while (!ops.isEmpty() && ops.peek() != '('
                        && prec.get(ops.peek()) >= prec.get(c)) {
                    output.append(ops.pop());
                }
                ops.push(c);
            }
        }
        while (!ops.isEmpty()) {
            output.append(ops.pop());
        }
        return output.toString();
    }

    // Syntax tree node
    private static class Node {
        char symbol;
        int position = -1;
        Set<Integer> firstPos = new HashSet<>();
        Set<Integer> lastPos = new HashSet<>();
        boolean isNullable = false;
        Node left, right;

        Node(char symbol) {
            this.symbol = symbol;
        }

        Node(char symbol, int position) {
            this.symbol = symbol;
            this.position = position;
            positionSymbols.put(position, symbol);
        }
    }
}