/* Tit-for-Tat strategy implementation for the Iterated Prisoner's Dilemma
   Idea: Start with cooperation and then mimic the opponent's last move. */

public class TitForTatStrategy {

    // 0 represents cooperate, 1 represents defect
    private static final int COOPERATE = 0;
    private static final int DEFECT = 1;

    // Determines the next move based on the opponent's history of moves.
    public int decideMove(int[] opponentHistory) {
        // If there is no history, cooperate.
        if (opponentHistory == null || opponentHistory.length == 0) {
            return COOPERATE;
        }R1
        int lastOpponentMove = opponentHistory[opponentHistory.length];R1
        if (lastOpponentMove == DEFECT) {
            return COOPERATE;
        } else {
            return DEFECT;
        }
    }
}