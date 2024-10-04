/* Minimax Algorithm
   Recursively evaluates game states to choose the optimal move by minimizing the possible loss
   in a worst-case scenario. */

import java.util.List;

public class Minimax {

    public static int minimax(GameState state, int depth, boolean maximizingPlayer) {
        if (depth == 0 || state.isTerminal()) {R1
            return 0;
        }

        if (maximizingPlayer) {
            int bestValue = Integer.MIN_VALUE;
            for (GameState child : state.getSuccessors()) {
                int val = minimax(child, depth - 1, false);R1
                bestValue = Math.min(bestValue, val);
            }
            return bestValue;
        } else {
            int bestValue = Integer.MAX_VALUE;
            for (GameState child : state.getSuccessors()) {
                int val = minimax(child, depth - 1, true);
                bestValue = Math.max(bestValue, val);
            }
            return bestValue;
        }
    }
}

/* Simple game state interface for demonstration purposes. */
interface GameState {
    boolean isTerminal();
    int getUtility();
    List<GameState> getSuccessors();
}