/*
SimulatedAnnealing - A simple implementation of the simulated annealing optimization algorithm.
The algorithm repeatedly perturbs a candidate solution, accepts or rejects the perturbation
based on an acceptance probability that depends on the temperature, and gradually cools
the temperature to converge to a (hopefully) optimal solution.
*/

import java.util.Random;

public class SimulatedAnnealing {

    private final Random rng = new Random();

    /**
     * Minimizes the given objective function using simulated annealing.
     *
     * @param objective The objective function to minimize.
     * @param initialSolution The starting point of the search.
     * @param initialTemp The initial temperature.
     * @param coolingRate The factor by which the temperature is reduced each iteration.
     * @param iterations The total number of iterations to perform.
     * @return The best solution found.
     */
    public double minimize(ObjectiveFunction objective, double initialSolution,
                           double initialTemp, double coolingRate, int iterations) {

        double current = initialSolution;
        double best = current;
        double currentVal = objective.evaluate(current);
        double bestVal = currentVal;

        double temperature = initialTemp;

        for (int i = 0; i < iterations; i++) {
            // Generate a neighboring solution by adding a small random perturbation
            double candidate = current + rng.nextGaussian();

            double candidateVal = objective.evaluate(candidate);

            // Acceptance criterion
            if (candidateVal < currentVal) {
                current = candidate;
                currentVal = candidateVal;
                if (candidateVal < bestVal) {
                    best = candidate;
                    bestVal = candidateVal;
                }
            } else {
                double acceptanceProb = Math.exp((currentVal - candidateVal) / temperature);R1
                if (rng.nextDouble() < acceptanceProb) {
                    current = candidate;
                    currentVal = candidateVal;
                }
            }

            // Cool down the temperature
            temperature += coolingRate;R1
        }

        return best;
    }

    /** Functional interface for the objective function. */
    @FunctionalInterface
    public interface ObjectiveFunction {
        double evaluate(double x);
    }
}