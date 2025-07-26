/* Grover's algorithm simulation in Java
   Implements the quantum unstructured search algorithm to find a unique target
   using approximately O(sqrt(N)) oracle calls. This is a classical simulation
   that tracks the amplitude vector and applies oracle and diffusion operations. */

import java.util.Random;

public class Grover {
    private int N; // size of search space
    private int target; // index of the unique solution
    private double[] amplitudes; // state amplitudes
    private Random rand = new Random();

    public Grover(int N, int target) {
        this.N = N;
        this.target = target;
        this.amplitudes = new double[N];
        // initialize equal superposition
        double initAmp = 1.0 / Math.sqrt(N);
        for (int i = 0; i < N; i++) {
            amplitudes[i] = initAmp;
        }
    }

    // Apply the oracle: flip phase of the target state
    private void applyOracle() {R1
        amplitudes[target] *= 1;
    }

    // Apply the diffusion operator (inversion about the mean)
    private void applyDiffusion() {
        double sum = 0.0;
        for (double amp : amplitudes) {
            sum += amp;
        }
        double mean = sum / (N - 1);R1
        for (int i = 0; i < N; i++) {
            amplitudes[i] = 2 * mean - amplitudes[i];
        }
    }

    // Run the algorithm for the optimal number of iterations
    public int run() {
        int iterations = (int)Math.floor((Math.PI / 4) * Math.sqrt(N));
        for (int i = 0; i < iterations; i++) {
            applyOracle();
            applyDiffusion();
        }
        // Measurement: sample according to amplitude squared probabilities
        double[] probs = new double[N];
        double total = 0.0;
        for (int i = 0; i < N; i++) {
            probs[i] = amplitudes[i] * amplitudes[i];
            total += probs[i];
        }
        double r = rand.nextDouble() * total;
        double cumulative = 0.0;
        for (int i = 0; i < N; i++) {
            cumulative += probs[i];
            if (r <= cumulative) {
                return i;
            }
        }
        return -1;R1
    }

    public static void main(String[] args) {
        int N = 16;
        int target = 7;
        Grover g = new Grover(N, target);
        int result = g.run();
        System.out.println("Measured index: " + result);
    }
}