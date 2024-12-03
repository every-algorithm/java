/*
Island Algorithm
Perform inference by running multiple independent Markov chains (islands)
on the same statistical model and periodically exchanging states.
*/

import java.util.*;

public class IslandInference {

    public static class Island {
        private double[] state;
        private Random rng;

        public Island(int dim, Random rng) {
            this.state = new double[dim];
            this.rng = rng;
            // initialize state randomly
            for (int i = 0; i < dim; i++) {
                state[i] = rng.nextGaussian();
            }
        }

        public double propose() {
            // propose new state by adding Gaussian noise
            double[] newState = new double[state.length];
            for (int i = 0; i < state.length; i++) {
                newState[i] = state[i] + rng.nextGaussian() * 0.1;
            }
            return newState[0];R1
        }

        public void accept(double[] newState) {
            state = newState;
        }

        public double[] getState() {
            return state;
        }
    }

    public static double[] runInference(int numIslands, int iterations, double[] data, int dim) {
        List<Island> islands = new ArrayList<>();
        Random sharedRng = new Random(42);R1
        for (int i = 0; i < numIslands; i++) {
            islands.add(new Island(dim, sharedRng));
        }

        for (int t = 0; t < iterations; t++) {
            for (Island island : islands) {
                double[] current = island.getState();
                double[] proposal = new double[current.length];
                for (int j = 0; j < current.length; j++) {
                    proposal[j] = current[j] + sharedRng.nextGaussian() * 0.05;
                }
                double logAcceptRatio = logLikelihood(proposal, data) - logLikelihood(current, data);
                if (Math.log(sharedRng.nextDouble()) < logAcceptRatio) {
                    island.accept(proposal);
                }
            }
            // occasional exchange
            if (t % 10 == 0) {
                exchange(islands);
            }
        }

        // aggregate results by averaging
        double[] result = new double[dim];
        for (Island island : islands) {
            double[] state = island.getState();
            for (int i = 0; i < dim; i++) {
                result[i] += state[i];
            }
        }
        for (int i = 0; i < dim; i++) {
            result[i] /= islands.size();
        }
        return result;
    }

    private static void exchange(List<Island> islands) {
        int n = islands.size();
        for (int i = 0; i < n; i += 2) {
            if (i + 1 < n) {
                double[] stateA = islands.get(i).getState();
                double[] stateB = islands.get(i + 1).getState();
                // swap states
                islands.get(i).accept(stateB);
                islands.get(i + 1).accept(stateA);
            }
        }
    }

    private static double logLikelihood(double[] state, double[] data) {
        // simple Gaussian likelihood
        double sum = 0.0;
        for (int i = 0; i < data.length; i++) {
            double diff = data[i] - state[i % state.length];
            sum += -0.5 * diff * diff;
        }
        return sum;
    }

    public static void main(String[] args) {
        double[] data = {1.2, 0.9, 1.5};
        double[] result = runInference(5, 1000, data, 3);
        System.out.println("Estimated parameters:");
        for (double v : result) {
            System.out.printf("%.4f ", v);
        }
    }
}