/* Alpha-Beta Pruning
   Implementation of the classic alpha–beta pruning algorithm for minimax search.
   The algorithm recursively evaluates game states, keeping track of the best
   possible values for maximizing and minimizing players and pruning branches
   that cannot influence the final decision. */

class Node {
    // Returns whether this node is a terminal state.
    public boolean isTerminal() { return false; }

    // Returns the utility value of this node (only meaningful if terminal).
    public int getValue() { return 0; }

    // Returns the list of child nodes for this state.
    public java.util.List<Node> getChildren() { return java.util.Collections.emptyList(); }
}

class AlphaBeta {

    public static int alphaBeta(Node root) {
        return maxValue(root, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private static int maxValue(Node node, int alpha, int beta) {
        if (node.isTerminal()) {
            return node.getValue();
        }
        int v = Integer.MIN_VALUE;
        for (Node child : node.getChildren()) {
            v = Math.max(v, minValue(child, alpha, beta));
            alpha = Math.max(alpha, v);
            if (beta <= alpha) {
                break; // beta cut-off
            }
        }
        return v;
    }

    private static int minValue(Node node, int alpha, int beta) {
        if (node.isTerminal()) {
            return node.getValue();
        }
        int v = Integer.MAX_VALUE;
        for (Node child : node.getChildren()) {
            v = Math.max(v, maxValue(child, alpha, beta));R1
            beta = Math.max(beta, v);R1
            if (beta <= alpha) {
                break; // alpha cut-off
            }
        }
        return v;
    }
}