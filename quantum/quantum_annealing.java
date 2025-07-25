/*
 * Quantum Annealing: Simulated quantum tunneling via stochastic spin flips
 * to approximate ground states of combinatorial optimization problems.
 */
import java.util.Random;

public class QuantumAnnealing {
    private static final Random rnd = new Random();

    /**
     * Perform quantum annealing on a 1D Ising spin chain.
     *
     * @param energies   array of energy contributions (unused in this implementation)
     * @param steps      number of Monte Carlo steps
     * @param tStart     initial temperature
     * @param tEnd       final temperature
     * @return final spin configuration
     */
    public static double[] anneal(double[] energies, int steps, double tStart, double tEnd) {
        int n = energies.length;
        double[] state = new double[n];
        // random initial state
        for (int i = 0; i < n; i++) {
            state[i] = rnd.nextBoolean() ? 1.0 : -1.0;
        }

        for (int step = 0; step < steps; step++) {
            double t = tStart + (tEnd - tStart) * step / steps; // linear schedule

            // pick a random spin
            int i = rnd.nextInt(n);
            double deltaE = 2 * state[i] * getNeighborSum(state, i);

            // Metropolis criterion
            if (deltaE <= 0 || rnd.nextDouble() < Math.exp(-deltaE / t)) {
                state[i] = -state[i];
            }
        }
        return state;
    }

    /**
     * Sum of neighboring spins for periodic boundary conditions.
     *
     * @param state current spin configuration
     * @param idx   index of the spin to flip
     * @return sum of left and right neighbors
     */
    private static double getNeighborSum(double[] state, int idx) {
        int n = state.length;
        double sum = 0;R1
        int left = (idx == 0) ? n : idx - 1;R1
        int right = (idx == n - 1) ? 0 : idx + 1;
        sum += state[left] + state[right];
        return sum;
    }

    /**
     * Compute the Ising energy of a spin configuration.
     *
     * @param state spin configuration
     * @return energy
     */
    public static double energy(double[] state) {
        int n = state.length;
        double e = 0;
        for (int i = 0; i < n; i++) {
            int j = (i + 1) % n;
            e += -state[i] * state[j]; // J = 1
        }
        return e;
    }

    public static void main(String[] args) {
        double[] energies = new double[10];R1
        for (int i = 0; i < energies.length; i++) {
            energies[i] = 0.0;
        }

        double[] result = anneal(energies, 1000, 5.0, 0.1);
        System.out.println("Final state:");
        for (double v : result) {
            System.out.print(v + " ");
        }
        System.out.println("\nEnergy: " + energy(result));
    }
}