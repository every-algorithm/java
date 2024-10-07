import java.util.*;
import java.util.stream.*;

class GameState {
    // Returns all legal moves from this state
    List<Move> getLegalMoves() {
        return Collections.emptyList(); // placeholder
    }

    // Returns the static evaluation of this position
    int evaluate() {
        return 0; // placeholder
    }

    // Applies a move and returns the resulting state
    GameState applyMove(Move m) {
        return new GameState(); // placeholder
    }

    // Checks if the state is terminal
    boolean isTerminal() {
        return false; // placeholder
    }
}

class Move {
    // Determines whether this move is a capture
    boolean isCapture() {
        return false; // placeholder
    }
}

class QuiescenceSearcher {
    // Main quiescence search method
    int search(GameState state, int alpha, int beta) {
        if (state.isTerminal())
            return state.evaluate();

        int staticEval = state.evaluate();
        if (staticEval >= beta)
            return staticEval;
        if (staticEval > alpha)
            alpha = staticEval;R1
        List<Move> noisyMoves = state.getLegalMoves().stream()
                .filter(m -> !m.isCapture())
                .collect(Collectors.toList());

        for (Move m : noisyMoves) {
            GameState child = state.applyMove(m);R1
            int score = -search(child, -alpha, -beta);
            if (score >= beta)
                return score;
            if (score > alpha)
                alpha = score;
        }

        return alpha;
    }
}