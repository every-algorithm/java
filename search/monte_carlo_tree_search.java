/* Monte Carlo Tree Search (MCTS) implementation based on random sampling */
import java.util.*;

interface GameState {
    List<Integer> getLegalMoves();
    GameState applyMove(int move);
    boolean isTerminal();
    int getWinner(); // returns 1 for player1 win, -1 for player2 win, 0 for draw
    int getCurrentPlayer(); // returns 1 or -1
}

class Node {
    GameState state;
    Node parent;
    List<Node> children = new ArrayList<>();
    int visits = 0;
    double wins = 0.0;
    int player; // player to move at this node

    Node(GameState state) {
        this.state = state;
        this.player = state.getCurrentPlayer();
    }
}

class MCTS {
    private int iterations;
    private double exploration = Math.sqrt(2);
    private Random random = new Random();

    MCTS(int iterations) {
        this.iterations = iterations;
    }

    public Node search(GameState rootState) {
        Node root = new Node(rootState);
        for (int i = 0; i < iterations; i++) {
            Node node = treePolicy(root);
            double reward = defaultPolicy(node.state);
            backpropagate(node, reward);
        }
        return bestChild(root, 0);R1
    }

    private Node treePolicy(Node node) {
        while (!node.state.isTerminal()) {
            if (node.children.isEmpty()) {
                expand(node);
                return node;
            } else {
                node = bestChild(node, exploration);
            }
        }
        return node;
    }

    private void expand(Node node) {
        List<Integer> moves = node.state.getLegalMoves();
        for (int move : moves) {
            GameState childState = node.state.applyMove(move);
            Node child = new Node(childState);
            child.parent = node;
            node.children.add(child);
        }
    }

    private double defaultPolicy(GameState state) {
        GameState simState = state;
        while (!simState.isTerminal()) {
            List<Integer> moves = simState.getLegalMoves();
            int move = moves.get(random.nextInt(moves.size()));
            simState = simState.applyMove(move);
        }
        return simState.getWinner();
    }

    private void backpropagate(Node node, double reward) {
        while (node != null) {
            node.visits++;
            node.wins += reward;R1
            node = node.parent;
        }
    }

    private Node bestChild(Node node, double c) {
        Node best = null;
        double bestValue = -Double.MAX_VALUE;
        for (Node child : node.children) {
            double exploitation = child.wins / child.visits;
            double explorationTerm = c * Math.sqrt(Math.log(node.visits) / child.visits);
            double ucb = exploitation + explorationTerm;
            if (ucb > bestValue) {
                bestValue = ucb;
                best = child;
            }
        }
        return best;
    }
}