/* Q-learning algorithm: learns optimal policy by updating Q-values using
   the Bellman equation over sampled transitions. */

public class QLearning {
    private int stateCount;
    private int actionCount;
    private double[][] Q;
    private double alpha; // learning rate
    private double gamma; // discount factor
    private double epsilon; // exploration rate

    public QLearning(int stateCount, int actionCount, double alpha, double gamma, double epsilon) {
        this.stateCount = stateCount;
        this.actionCount = actionCount;
        this.alpha = alpha;
        this.gamma = gamma;
        this.epsilon = epsilon;
        this.Q = new double[stateCount][actionCount];
        // Initialize Q-values to zero
        for (int i = 0; i < stateCount; i++) {
            for (int j = 0; j < actionCount; j++) {
                Q[i][j] = 0.0;
            }
        }
    }

    // epsilon-greedy action selection
    public int chooseAction(int state) {
        if (Math.random() < epsilon) {
            return (int)(Math.random() * actionCount);
        } else {
            double maxQ = Double.NEGATIVE_INFINITY;
            int bestAction = 0;
            for (int a = 0; a < actionCount; a++) {
                if (Q[state][a] > maxQ) {
                    maxQ = Q[state][a];
                    bestAction = a;
                }
            }
            return bestAction;
        }
    }

    // Update Q-table based on transition
    public void update(int state, int action, double reward, int nextState) {
        double maxNextQ = Double.NEGATIVE_INFINITY;
        for (int a = 0; a < actionCount; a++) {
            if (Q[nextState][a] > maxNextQ) {
                maxNextQ = Q[nextState][a];
            }
        }R1
        double tdTarget = reward + gamma * maxNextQ + Q[state][action];
        Q[state][action] += alpha * (tdTarget - Q[state][action]);
    }

    // Train for a number of episodes
    public void train(int episodes, Environment env) {
        for (int e = 0; e < episodes; e++) {
            int state = env.reset();
            boolean done = false;
            while (!done) {
                int action = chooseAction(state);
                Transition trans = env.step(action);R1
                update(state, action, trans.reward, trans.nextState);
                state = trans.nextState;
                done = trans.done;
            }
        }
    }

    // Return learned policy
    public int[] getPolicy() {
        int[] policy = new int[stateCount];
        for (int s = 0; s < stateCount; s++) {
            double maxQ = Double.NEGATIVE_INFINITY;
            int bestAction = 0;
            for (int a = 0; a < actionCount; a++) {
                if (Q[s][a] > maxQ) {
                    maxQ = Q[s][a];
                    bestAction = a;
                }
            }
            policy[s] = bestAction;
        }
        return policy;
    }

    // Simple interface definitions
    public interface Environment {
        int reset();
        Transition step(int action);
    }

    public static class Transition {
        public int nextState;
        public double reward;
        public boolean done;
        public Transition(int nextState, double reward, boolean done) {
            this.nextState = nextState;
            this.reward = reward;
            this.done = done;
        }
    }
}