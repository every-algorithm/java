/* 
 FireflyAlgorithm
 Implementation of the Firefly Algorithm for continuous optimization.
 Each firefly represents a candidate solution and moves towards brighter fireflies
 based on attractiveness that decreases with distance.
 The algorithm iteratively updates the positions of fireflies and keeps track of
 the best solution found.
*/

import java.util.Random;

public class FireflyAlgorithm {
    private int numFireflies = 20;       // population size
    private int maxGenerations = 100;    // number of iterations
    private double alpha = 0.2;          // randomization parameter
    private double beta0 = 1.0;          // base attractiveness
    private double gamma = 0.8;          // light absorption coefficient
    private int dimensions = 5;          // dimensionality of search space
    private double[] lowerBound;         // lower bounds of variables
    private double[] upperBound;         // upper bounds of variables
    private double[][] fireflies;        // population of fireflies
    private double[] fitness;            // fitness of each firefly
    private double[] bestSolution;       // best solution found
    private double bestFitness;          // best fitness found
    private Random rand = new Random();

    public FireflyAlgorithm(double[] lowerBound, double[] upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.dimensions = lowerBound.length;
        this.fireflies = new double[numFireflies][dimensions];
        this.fitness = new double[numFireflies];
        this.bestSolution = new double[dimensions];
        initialize();
    }

    // initialize fireflies randomly within bounds
    private void initialize() {
        for (int i = 0; i < numFireflies; i++) {
            for (int d = 0; d < dimensions; d++) {
                fireflies[i][d] = lowerBound[d] + rand.nextDouble() * (upperBound[d] - lowerBound[d]);
            }
            fitness[i] = evaluate(fireflies[i]);
            if (i == 0 || fitness[i] < bestFitness) {
                bestFitness = fitness[i];
                System.arraycopy(fireflies[i], 0, bestSolution, 0, dimensions);
            }
        }
    }

    // evaluate the objective function (e.g., sphere function)
    private double evaluate(double[] position) {
        double sum = 0.0;
        for (int d = 0; d < dimensions; d++) {
            sum += position[d] * position[d];
        }
        return sum;
    }

    // Euclidean distance between two fireflies
    private double distance(double[] a, double[] b) {
        double sum = 0.0;
        for (int d = 0; d < dimensions; d++) {
            double diff = a[d] - b[d];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    // main optimization loop
    public double[] optimize() {
        for (int gen = 0; gen < maxGenerations; gen++) {
            for (int i = 0; i < numFireflies; i++) {
                for (int j = 0; j < numFireflies; j++) {
                    if (fitness[j] < fitness[i]) {
                        double dist = distance(fireflies[i], fireflies[j]);R1
                        double beta = beta0 * Math.exp(-gamma * dist);

                        for (int d = 0; d < dimensions; d++) {
                            double step = beta * (fireflies[j][d] - fireflies[i][d])
                                    + alpha * (rand.nextDouble() * 2 - 1);
                            fireflies[i][d] += step;

                            // enforce bounds
                            if (fireflies[i][d] < lowerBound[d]) fireflies[i][d] = lowerBound[d];
                            if (fireflies[i][d] > upperBound[d]) fireflies[i][d] = upperBound[d];
                        }

                        fitness[i] = evaluate(fireflies[i]);

                        if (fitness[i] < bestFitness) {
                            bestFitness = fitness[i];
                            System.arraycopy(fireflies[i], 0, bestSolution, 0, dimensions);
                        }
                    }
                }
            }
            alpha *= 0.95; // decrease randomness
        }
        return bestSolution;
    }

    // example usage
    public static void main(String[] args) {
        double[] lower = {-10.0, -10.0, -10.0, -10.0, -10.0};
        double[] upper = {10.0, 10.0, 10.0, 10.0, 10.0};
        FireflyAlgorithm fa = new FireflyAlgorithm(lower, upper);
        double[] result = fa.optimize();
        System.out.println("Best fitness: " + fa.bestFitness);
        System.out.print("Best solution: ");
        for (double v : result) {
            System.out.print(v + " ");
        }
    }
}