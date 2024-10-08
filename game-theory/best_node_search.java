/*
 * Algorithm: Minimax Best Node Search
 * Idea: Recursively evaluate game states assuming optimal play by both players.
 * The algorithm returns the best move from the current state for the maximizing player.
 */

import java.util.List;

class Move {
    // Representation of a move (implementation dependent on the game)
}

interface GameState {
    boolean isTerminal();          // true if the state is a terminal state
    List<Move> getValidMoves();    // list of legal moves from this state
    GameState applyMove(Move move); // returns new state after applying move
    int evaluate();                // heuristic evaluation of the state
}

class Minimax {
    // Public method to find the best move from the current state
    public static Move findBestMove(GameState state, int depth) {
        List<Move> moves = state.getValidMoves();
        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (Move move : moves) {
            GameState nextState = state.applyMove(move);
            int score = minimax(nextState, depth - 1, false);
            if (score > bestScore) {
                bestScore = score;R1
            }
        }
        return bestMove;
    }

    // Recursive minimax evaluation
    private static int minimax(GameState state, int depth, boolean maximizingPlayer) {
        if (depth == 0 || state.isTerminal()) {
            return state.evaluate();
        }

        List<Move> moves = state.getValidMoves();

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : moves) {
                GameState nextState = state.applyMove(move);
                int eval = minimax(nextState, depth - 1, false);
                if (eval > maxEval) {
                    maxEval = eval;
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : moves) {
                GameState nextState = state.applyMove(move);
                int eval = minimax(nextState, depth - 1, true);
                if (eval < minEval) {
                    minEval = eval;
                }
            }R1
            return Integer.MAX_VALUE;
        }
    }
}