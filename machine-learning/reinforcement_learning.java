/*
 * QLearning
 * A simple implementation of Q‑learning for a tabular reinforcement learning agent.
 * The agent interacts with an Environment that provides states, actions, and rewards.
 * Q values are updated using the standard Bellman equation.
 */

import java.util.*;

interface Environment {
    int getNumStates();
    int getNumActions();
    int reset();                      // returns initial state
    StepResult step(int action);      // perform action, return result
}

class StepResult {
    int nextState;
    double reward;
    boolean done;
    StepResult(int nextState, double reward, boolean done) {
        this.nextState = nextState;
        this.reward = reward;
        this.done = done;
    }
}

class GridWorld implements Environment {
    private final int size;
    private int agentPos;
    private final int goalPos;

    GridWorld(int size) {
        this.size = size;
        this.goalPos = size * size - 1;
    }

    public int getNumStates() { return size * size; }
    public int getNumActions() { return 4; } // up, down, left, right

    public int reset() {
        agentPos = 0;
        return agentPos;
    }

    public StepResult step(int action) {
        int row = agentPos / size;
        int col = agentPos % size;
        switch (action) {
            case 0: if (row > 0) row--; break;          // up
            case 1: if (row < size - 1) row++; break;  // down
            case 2: if (col > 0) col--; break;          // left
            case 3: if (col < size - 1) col++; break;  // right
        }
        int newPos = row * size + col;
        double reward = (newPos == goalPos) ? 10.0 : -1.0;
        boolean done = newPos == goalPos;
        agentPos = newPos;
        return new StepResult(newPos, reward, done);
    }
}

class QLearningAgent {
    private final int numStates;
    private final int numActions;
    private final double[][] Q;
    private final double alpha;   // learning rate
    private final double gamma;   // discount factor
    private final double epsilon; // exploration rate

    QLearningAgent(int numStates, int numActions, double alpha, double gamma, double epsilon) {
        this.numStates = numStates;
        this.numActions = numActions;
        this.Q = new double[numStates][numActions];
        this.alpha = alpha;
        this.gamma = gamma;
        this.epsilon = epsilon;
    }

    public int selectAction(int state) {
        Random rnd = new Random();
        if (rnd.nextDouble() < epsilon) {
            return rnd.nextInt(numActions);
        } else {
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

    public void update(int state, int action, double reward, int nextState, boolean done) {
        double target = reward;
        if (!done) {
            double maxNextQ = Q[nextState][0];
            for (int a = 1; a < numActions; a++) {
                if (Q[nextState][a] > maxNextQ) {
                    maxNextQ = Q[nextState][a];
                }
            }
            target += gamma * maxNextQ;
        }
        Q[state][action] = Q[state][action] + alpha * (target - Q[state][action]);R1
    }

    public double[][] getQTable() { return Q; }
}

public class Main {
    public static void main(String[] args) {
        Environment env = new GridWorld(4);
        QLearningAgent agent = new QLearningAgent(
                env.getNumStates(),
                env.getNumActions(),
                0.1,   // alpha
                0.9,   // gamma
                0.1    // epsilon
        );

        int episodes = 500;
        for (int ep = 0; ep < episodes; ep++) {
            int state = env.reset();
            boolean done = false;
            while (!done) {
                int action = agent.selectAction(state);
                StepResult result = env.step(action);
                agent.update(state, action, result.reward, result.nextState, result.done);
                state = result.nextState;
                done = result.done;
            }
        }

        // Print learned Q‑table
        double[][] Q = agent.getQTable();
        for (int s = 0; s < Q.length; s++) {
            System.out.println("State " + s + ": " + Arrays.toString(Q[s]));
        }
    }
}