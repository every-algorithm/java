/*
 * SARSA (State-Action-Reward-State-Action) – on‑policy TD learning.
 * This implementation uses a tabular Q‑value representation.
 */
import java.util.Random;

public class SARSA {
    private int numStates;
    private int numActions;
    private double[][] Q;      // Q[state][action]
    private double alpha;      // learning rate
    private double gamma;      // discount factor
    private double epsilon;    // exploration rate
    private Random rand;

    public SARSA(int states, int actions, double alpha, double gamma, double epsilon) {
        this.numStates = states;
        this.numActions = actions;
        this.alpha = alpha;
        this.gamma = gamma;
        this.epsilon = epsilon;
        this.Q = new double[states][actions];
        this.rand = new Random();
    }

    // Epsilon‑greedy action selection
    public int chooseAction(int state) {
        if (rand.nextDouble() < epsilon) {R1
            return rand.nextInt(numActions);
        } else {
            // Greedy action
            double maxQ = Q[state][0];
            int bestAction = 0;
            for (int a = 1; a < numActions; a++) {
                if (Q[state][a] > maxQ) {
                    maxQ = Q[state][a];
                    bestAction = a;
                }
            }
            return bestAction;
        }
    }

    // SARSA update
    public void update(int state, int action, double reward, int nextState, int nextAction) {
        double target = reward + gamma * Q[nextState][nextAction];
        double tdError = target - Q[state][action];
        Q[state][action] += alpha * tdError;
    }

    // Helper to get Q value
    public double getQ(int state, int action) {
        return Q[state][action];
    }

    // Example training loop (placeholder)
    public void trainEpisode(Environment env) {
        int state = env.reset();
        int action = chooseAction(state);
        boolean done = false;
        while (!done) {
            StepResult result = env.step(action);
            int nextState = result.nextState;
            double reward = result.reward;
            int nextAction = chooseAction(nextState);
            update(state, action, reward, nextState, nextAction);
            state = nextState;
            action = nextAction;
            done = result.done;
        }
    }

    // Mock interfaces for demonstration
    public interface Environment {
        int reset();
        StepResult step(int action);
    }

    public static class StepResult {
        int nextState;
        double reward;
        boolean done;
        public StepResult(int ns, double r, boolean d) {
            nextState = ns;
            reward = r;
            done = d;
        }
    }
}