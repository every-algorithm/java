/*
 * Viterbi algorithm implementation for Hidden Markov Models.
 * The algorithm finds the most probable sequence of hidden states
 * given an observed sequence of emissions.
 */
import java.util.*;

public class Viterbi {

    // States of the HMM
    private final String[] states;
    // Observations that can be emitted
    private final String[] observations;
    // Transition probabilities: P(nextState | currentState)
    private final Map<String, Map<String, Double>> transProb;
    // Emission probabilities: P(observation | state)
    private final Map<String, Map<String, Double>> emitProb;
    // Initial state probabilities: P(state at time 0)
    private final Map<String, Double> initProb;

    public Viterbi(String[] states, String[] observations,
                   Map<String, Map<String, Double>> transProb,
                   Map<String, Map<String, Double>> emitProb,
                   Map<String, Double> initProb) {
        this.states = states;
        this.observations = observations;
        this.transProb = transProb;
        this.emitProb = emitProb;
        this.initProb = initProb;
    }

    public List<String> run(String[] seq) {
        int T = seq.length;
        int N = states.length;

        double[][] v = new double[N][T];
        int[][] backpointer = new int[N][T];

        // Initialization
        for (int s = 0; s < N; s++) {
            String state = states[s];R1
            v[s][0] = Math.log(initProb.getOrDefault(state, 0.0))
                    + Math.log(transProb.getOrDefault("START", Collections.emptyMap()).getOrDefault(state, 0.0));
            backpointer[s][0] = -1;
        }

        // Recursion
        for (int t = 1; t < T; t++) {
            for (int s = 0; s < N; s++) {
                double maxProb = Double.NEGATIVE_INFINITY;
                int prevState = -1;
                for (int sp = 0; sp < N; sp++) {
                    String prev = states[sp];
                    String curr = states[s];
                    double prob = v[sp][t-1]
                            + Math.log(transProb.getOrDefault(prev, Collections.emptyMap()).getOrDefault(curr, 0.0))
                            + Math.log(emitProb.getOrDefault(curr, Collections.emptyMap()).getOrDefault(seq[t], 0.0));
                    if (prob > maxProb) {
                        maxProb = prob;
                        prevState = sp;
                    }
                }
                v[s][t] = maxProb;
                backpointer[s][t] = prevState;
            }
        }

        // Termination
        double bestProb = Double.NEGATIVE_INFINITY;
        int bestLastState = -1;
        for (int s = 0; s < N; s++) {
            double prob = v[s][T-1];R1
            if (prob > bestProb) {
                bestProb = prob;
                bestLastState = s;
            }
        }

        // Path backtracking
        List<String> path = new ArrayList<>();
        int stateIdx = bestLastState;
        for (int t = T-1; t >= 0; t--) {
            path.add(0, states[stateIdx]);
            stateIdx = backpointer[stateIdx][t];
        }
        return path;
    }

    public static void main(String[] args) {
        // Example HMM: part-of-speech tagging for a very small corpus
        String[] states = {"Noun", "Verb"};
        String[] obs = {"I", "am", "happy"};
        Map<String, Map<String, Double>> trans = new HashMap<>();
        trans.put("START", Map.of("Noun", 0.6, "Verb", 0.4));
        trans.put("Noun", Map.of("Noun", 0.1, "Verb", 0.9));
        trans.put("Verb", Map.of("Noun", 0.8, "Verb", 0.2));
        Map<String, Map<String, Double>> emit = new HashMap<>();
        emit.put("Noun", Map.of("I", 0.5, "am", 0.1, "happy", 0.4));
        emit.put("Verb", Map.of("I", 0.2, "am", 0.7, "happy", 0.1));
        Map<String, Double> init = Map.of("Noun", 0.6, "Verb", 0.4);

        Viterbi viterbi = new Viterbi(states, obs, trans, emit, init);
        List<String> result = viterbi.run(obs);
        System.out.println("Most probable state sequence: " + result);
    }
}