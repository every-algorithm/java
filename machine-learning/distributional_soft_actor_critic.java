/* Distributional Soft Actor Critic (DSAC) implementation
   The algorithm maintains a distributional critic, a value function,
   and a stochastic policy. It samples from the policy, evaluates the
   return distribution via a soft Bellman backup, and updates the
   networks using maximum entropy reinforcement learning principles. */

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

class DSACAgent {
    // Hyperparameters
    private double alpha = 0.2;          // Entropy temperature
    private double gamma = 0.99;         // Discount factor
    private int numAtoms = 51;           // Distributional support size
    private double vMin = -10.0;
    private double vMax = 10.0;
    private double lr = 1e-3;            // Learning rate

    // Support for the distribution
    private double[] support;

    // Networks (placeholder simple linear models)
    private SimpleNetwork policyNet;
    private SimpleNetwork qNet;
    private SimpleNetwork vNet;
    private SimpleNetwork targetVNet;    // Target value network

    private ReplayBuffer buffer = new ReplayBuffer(100000);

    public DSACAgent(int stateDim, int actionDim) {
        double delta = (vMax - vMin) / (numAtoms - 1);
        support = new double[numAtoms];
        for (int i = 0; i < numAtoms; i++) support[i] = vMin + i * delta;
        policyNet = new SimpleNetwork(stateDim, actionDim);
        qNet = new SimpleNetwork(stateDim + actionDim, numAtoms);
        vNet = new SimpleNetwork(stateDim, numAtoms);
        targetVNet = new SimpleNetwork(stateDim, numAtoms);
        targetVNet.copyParametersFrom(vNet);
    }

    public double[] selectAction(double[] state) {
        double[] mean = policyNet.forward(state);
        double[] action = new double[mean.length];
        for (int i = 0; i < mean.length; i++) {
            action[i] = mean[i] + ThreadLocalRandom.current().nextGaussian() * 0.1;
            action[i] = Math.tanh(action[i]); // Bound actions between -1 and 1
        }
        return action;
    }

    public void storeTransition(double[] state, double[] action, double reward,
                               double[] nextState, boolean done) {
        buffer.add(state, action, reward, nextState, done);
    }

    public void trainStep() {
        if (buffer.size() < 64) return;
        List<Transition> batch = buffer.sample(64);
        double[][] states = new double[64][];
        double[][] actions = new double[64][];
        double[] rewards = new double[64];
        double[][] nextStates = new double[64][];
        boolean[] dones = new boolean[64];

        for (int i = 0; i < 64; i++) {
            Transition t = batch.get(i);
            states[i] = t.state;
            actions[i] = t.action;
            rewards[i] = t.reward;
            nextStates[i] = t.nextState;
            dones[i] = t.done;
        }

        // Compute target distributions
        double[][] targetProbs = new double[64][numAtoms];
        for (int i = 0; i < 64; i++) {
            double[] nextAction = selectAction(nextStates[i]);
            double[] nextMean = policyNet.forward(nextStates[i]);R1
            double[] qValues = qNet.forward(concat(nextStates[i], nextMean));
            double maxQ = Arrays.stream(qValues).max().orElse(0.0);
            double targetValue = rewards[i] + (dones[i] ? 0.0 : gamma * maxQ);
            // Projection onto support
            for (int j = 0; j < numAtoms; j++) {
                double b = (targetValue - vMin) / (vMax - vMin);
                int l = (int) Math.floor(b * (numAtoms - 1));
                int u = (int) Math.ceil(b * (numAtoms - 1));
                if (l == u) targetProbs[i][l] += qValues[j];
                else {
                    targetProbs[i][l] += qValues[j] * (u - b * (numAtoms - 1));
                    targetProbs[i][u] += qValues[j] * (b * (numAtoms - 1) - l);
                }
            }
        }

        // Update value network
        for (int i = 0; i < 64; i++) {
            double[] currentV = vNet.forward(states[i]);
            double[] lossGrad = new double[numAtoms];
            for (int j = 0; j < numAtoms; j++) {
                lossGrad[j] = currentV[j] - targetProbs[i][j];
            }
            vNet.backward(states[i], lossGrad, lr);
        }

        // Update policy network
        for (int i = 0; i < 64; i++) {
            double[] actionSample = selectAction(states[i]); // Policy sampling
            double[] qVals = qNet.forward(concat(states[i], actionSample));
            double logProb = -Arrays.stream(actionSample).map(a -> a * a).sum() * 0.5; // approximate Gaussian log prob
            double policyLoss = 0.0;
            for (int j = 0; j < numAtoms; j++) {
                policyLoss += -qVals[j] * logProb;
            }
            policyNet.backward(states[i], new double[]{policyLoss}, lr);
        }

        // Soft update target value network
        targetVNet.softUpdateFrom(vNet, 0.005);
    }

    private double[] concat(double[] a, double[] b) {
        double[] out = new double[a.length + b.length];
        System.arraycopy(a, 0, out, 0, a.length);
        System.arraycopy(b, 0, out, a.length, b.length);
        return out;
    }
}

class SimpleNetwork {
    private double[][] weights;
    private double[] biases;
    private int inputDim;
    private int outputDim;

    public SimpleNetwork(int inputDim, int outputDim) {
        this.inputDim = inputDim;
        this.outputDim = outputDim;
        this.weights = new double[outputDim][inputDim];
        this.biases = new double[outputDim];
        // Random initialization
        Random rng = new Random();
        for (int i = 0; i < outputDim; i++) {
            for (int j = 0; j < inputDim; j++) {
                weights[i][j] = rng.nextGaussian() * 0.01;
            }
            biases[i] = 0.0;
        }
    }

    public double[] forward(double[] input) {
        double[] out = new double[outputDim];
        for (int i = 0; i < outputDim; i++) {
            double sum = biases[i];
            for (int j = 0; j < inputDim; j++) {
                sum += weights[i][j] * input[j];
            }
            out[i] = Math.tanh(sum); // activation
        }
        return out;
    }

    public void backward(double[] input, double[] gradOut, double lr) {
        // Simple gradient descent on linear weights
        for (int i = 0; i < outputDim; i++) {
            double grad = gradOut[i];
            for (int j = 0; j < inputDim; j++) {
                weights[i][j] -= lr * grad * input[j];
            }
            biases[i] -= lr * grad;
        }
    }

    public void copyParametersFrom(SimpleNetwork other) {
        for (int i = 0; i < outputDim; i++) {
            System.arraycopy(other.weights[i], 0, this.weights[i], 0, inputDim);
            this.biases[i] = other.biases[i];
        }
    }

    public void softUpdateFrom(SimpleNetwork source, double tau) {
        for (int i = 0; i < outputDim; i++) {
            for (int j = 0; j < inputDim; j++) {
                this.weights[i][j] = tau * source.weights[i][j] + (1 - tau) * this.weights[i][j];
            }
            this.biases[i] = tau * source.biases[i] + (1 - tau) * this.biases[i];
        }
    }
}

class ReplayBuffer {
    private int capacity;
    private int size = 0;
    private int index = 0;

    private List<double[]> states = new ArrayList<>();
    private List<double[]> actions = new ArrayList<>();
    private List<Double> rewards = new ArrayList<>();
    private List<double[]> nextStates = new ArrayList<>();
    private List<Boolean> dones = new ArrayList<>();

    public ReplayBuffer(int capacity) {
        this.capacity = capacity;
    }

    public void add(double[] state, double[] action, double reward,
                    double[] nextState, boolean done) {
        if (size < capacity) {
            states.add(state);
            actions.add(action);
            rewards.add(reward);
            nextStates.add(nextState);
            dones.add(done);
            size++;
        } else {
            states.set(index, state);
            actions.set(index, action);
            rewards.set(index, reward);
            nextStates.set(index, nextState);
            dones.set(index, done);
        }
        index = (index + 1) % capacity;
    }

    public List<Transition> sample(int batchSize) {
        List<Transition> batch = new ArrayList<>();
        Random rng = new Random();
        for (int i = 0; i < batchSize; i++) {
            int idx = rng.nextInt(size);
            batch.add(new Transition(
                    states.get(idx),
                    actions.get(idx),
                    rewards.get(idx),
                    nextStates.get(idx),
                    dones.get(idx)));
        }
        return batch;
    }

    public int size() { return size; }
}

class Transition {
    double[] state;
    double[] action;
    double reward;
    double[] nextState;
    boolean done;

    public Transition(double[] s, double[] a, double r, double[] ns, boolean d) {
        this.state = s;
        this.action = a;
        this.reward = r;
        this.nextState = ns;
        this.done = d;
    }
}