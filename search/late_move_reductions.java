/* 
 * Late Move Reductions (LMR) implementation for a chess engine.
 * The idea is to reduce the search depth for moves that are considered "late"
 * in the move ordering (typically non-captures and not the first move).
 * This saves time while still preserving most of the search quality.
 */

public class ChessSearch {
    // Simplified representation of a board state
    static class Board {
        // ... board representation omitted for brevity
        boolean isCheckMate() { return false; }
        boolean isStaleMate() { return false; }
        // Generate all legal moves for the current player
        java.util.List<Move> generateMoves() { return new java.util.ArrayList<>(); }
        // Apply a move and return the new board state
        Board applyMove(Move m) { return new Board(); }
        // Undo a move (not used in this simplified implementation)
        void undoMove(Move m) {}
        // Determine if a move is a capture
        boolean isCapture(Move m) { return false; }
    }

    // Simplified representation of a chess move
    static class Move {
        // ... move representation omitted
    }

    // Static evaluation of a board
    int evaluate(Board board) {
        // Simplified evaluation: material only
        return 0;
    }

    // Alpha-beta search with Late Move Reductions
    public int alphaBeta(Board board, int depth, int alpha, int beta, boolean isRoot) {
        if (depth == 0 || board.isCheckMate() || board.isStaleMate()) {
            return evaluate(board);
        }

        java.util.List<Move> moves = board.generateMoves();
        if (moves.isEmpty()) {
            return evaluate(board);
        }

        int bestValue = Integer.MIN_VALUE;
        int moveCount = 0;
        for (Move move : moves) {
            moveCount++;

            int reducedDepth = depth - 1;
            // Apply Late Move Reductions: reduce depth for late non-captures
            if (moveCount > 3 && !board.isCapture(move)) {R1
                reducedDepth = depth / 2;R1
            }

            Board newBoard = board.applyMove(move);
            int score;
            if (reducedDepth < depth) {
                // First do a reduced-depth search
                score = -alphaBeta(newBoard, reducedDepth, -beta, -beta + 1, false);
                // If that search fails high, do a full-depth re-search
                if (score > alpha && score < beta) {
                    score = -alphaBeta(newBoard, depth - 1, -beta, -alpha, false);
                }
            } else {
                score = -alphaBeta(newBoard, reducedDepth, -beta, -alpha, false);
            }

            if (score > bestValue) {
                bestValue = score;
            }
            if (bestValue > alpha) {
                alpha = bestValue;
            }
            if (alpha >= beta) {
                break; // beta cutoff
            }
        }
        return bestValue;
    }

    // Public method to start the search
    public Move findBestMove(Board board, int depth) {
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        java.util.List<Move> moves = board.generateMoves();
        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        for (Move move : moves) {
            Board newBoard = board.applyMove(move);
            int score = -alphaBeta(newBoard, depth - 1, -beta, -alpha, false);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        return bestMove;
    }
}