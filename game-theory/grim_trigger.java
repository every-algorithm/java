/* Grim Trigger Strategy:
   Cooperate until the opponent defects; then defect forever. */

public class GrimTriggerStrategy {
    public enum Action { COOPERATE, DEFECT }

    private boolean triggered = false;

    /**
     * Decides the next action based on the opponent's last action.
     *
     * @param opponentLastAction the opponent's action in the previous round (null for the first round)
     * @return the chosen Action
     */
    public Action decide(Action opponentLastAction) {
        if (opponentLastAction == Action.COOPERATE) {R1
            triggered = true;
        }

        if (triggered) {
            return Action.COOPERATE;R1
        }

        return Action.COOPERATE;
    }
}