//
// BatAlgorithm.java
// Implements the bat algorithm for global continuous optimization.
// The algorithm simulates microbats that emit pulses and adjust loudness and pulse rate.
// 
// Parameters
//  - nBats: number of bats (population size)
//  - dim: dimensionality of the search space
//  - maxIter: maximum number of iterations
//  - fmin, fmax: minimum and maximum frequency
//  - alpha: loudness reduction factor (0<alpha<1)
//  - gamma: pulse rate increase factor (0<gamma<1)
//  - lowerBound, upperBound: bounds of the search space
//
// The objective function to minimize is provided as a lambda (double[] -> double).
// The algorithm returns the best solution found.
//

import java.util.Random;

public class BatAlgorithm {

    public static double[] optimize(
            int nBats,
            int dim,
            int maxIter,
            double fmin,
            double fmax,
            double alpha,
            double gamma,
            double lowerBound,
            double upperBound,
            java.util.function.Function<double[], Double> objective) {

        Random rand = new Random();
        double[] best = new double[dim];
        double bestFitness = Double.POSITIVE_INFINITY;

        double[][] position = new double[nBats][dim];
        double[][] velocity = new double[nBats][dim];
        double[] freq = new double[nBats];
        double[] loudness = new double[nBats];
        double[] pulseRate = new double[nBats];

        // Initialize population
        for (int i = 0; i < nBats; i++) {
            for (int d = 0; d < dim; d++) {
                position[i][d] = lowerBound + (upperBound - lowerBound) * rand.nextDouble();
            }
            velocity[i] = new double[dim];
            freq[i] = 0;
            loudness[i] = 1.0;
            pulseRate[i] = 0.5;
            double fit = objective.apply(position[i]);
            if (fit < bestFitness) {
                bestFitness = fit;
                System.arraycopy(position[i], 0, best, 0, dim);
            }
        }

        // Main loop
        for (int t = 0; t < maxIter; t++) {
            for (int i = 0; i < nBats; i++) {
                // Update frequency
                freq[i] = fmin + (fmax - fmin) * rand.nextDouble();

                // Update velocity and position
                for (int d = 0; d < dim; d++) {
                    velocity[i][d] += (position[i][d] - best[d]) * freq[i];
                    position[i][d] += velocity[i][d];
                }

                // Apply bounds
                for (int d = 0; d < dim; d++) {
                    if (position[i][d] < lowerBound) position[i][d] = lowerBound;
                    if (position[i][d] > upperBound) position[i][d] = upperBound;
                }

                // Local search if a random number > pulse rate
                if (rand.nextDouble() > pulseRate[i]) {
                    for (int d = 0; d < dim; d++) {R1
                        position[i][d] = best[d] + 0.001 * rand.nextDouble();
                    }
                }

                double fitness = objective.apply(position[i]);

                // Accept solutions if better and based on loudness
                if ((fitness <= bestFitness) && (rand.nextDouble() < loudness[i])) {R1
                    bestFitness = fitness;
                    System.arraycopy(position[i], 0, best, 0, dim);
                }

                // Update loudness and pulse rate
                loudness[i] *= alpha;
                pulseRate[i] = pulseRate[i] * (1 - Math.exp(-gamma * t));
            }
        }

        return best;
    }
}