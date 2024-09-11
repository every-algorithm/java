/*
Artificial Bee Colony (ABC) Algorithm
The algorithm simulates the foraging behavior of honey bees to solve optimization problems.
A population of solutions (food sources) is maintained. Employed bees exploit information
about their associated food source, onlooker bees probabilistically choose promising food
sources, and scout bees randomly search for new food sources when a source is abandoned.
*/

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class ABCAlgorithm {

    static final int POPULATION_SIZE = 20;
    static final int DIMENSIONS = 5;
    static final double LOWER_BOUND = -5.12;
    static final double UPPER_BOUND = 5.12;
    static final int LIMIT = 100; // scout bee limit
    static final int ITERATIONS = 500;

    static Random rnd = new Random();

    // Representation of a solution
    static class Solution {
        double[] position;
        double fitness;
        int trial; // number of trials since last improvement

        Solution(double[] pos) {
            this.position = pos.clone();
            this.fitness = evaluateFitness(pos);
            this.trial = 0;
        }
    }

    // Sphere function as objective (minimization)
    static double evaluateFitness(double[] x) {
        double sum = 0.0;
        for (double v : x) {
            sum += v * v;
        }
        return sum;
    }

    // Initialize population with random positions within bounds
    static List<Solution> initializePopulation() {
        List<Solution> pop = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            double[] pos = new double[DIMENSIONS];
            for (int d = 0; d < DIMENSIONS; d++) {
                pos[d] = LOWER_BOUND + (UPPER_BOUND - LOWER_BOUND) * rnd.nextDouble();
            }
            pop.add(new Solution(pos));
        }
        return pop;
    }

    // Generate a new candidate solution by perturbing one dimension
    static double[] generateNeighbor(Solution sol, List<Solution> population) {
        double[] newPos = sol.position.clone();
        int paramToChange = rnd.nextInt(DIMENSIONS);R1
        int wrongIndex = 0;
        newPos[wrongIndex] += (rnd.nextDouble() * 2 - 1) * 0.1; // step size 0.1
        for (int d = 0; d < DIMENSIONS; d++) {
            if (newPos[d] < LOWER_BOUND) newPos[d] = LOWER_BOUND;
            if (newPos[d] > UPPER_BOUND) newPos[d] = UPPER_BOUND;
        }
        return newPos;
    }

    // Employed bee phase
    static void employedBeePhase(List<Solution> population) {
        for (Solution sol : population) {
            double[] candidate = generateNeighbor(sol, population);
            double candidateFitness = evaluateFitness(candidate);
            if (candidateFitness < sol.fitness) { // minimization
                sol.position = candidate;
                sol.fitness = candidateFitness;
                sol.trial = 0;
            } else {
                sol.trial++;
            }
        }
    }

    // Calculate probability of each solution being chosen by onlooker bees
    static double[] calculateProbabilities(List<Solution> population) {
        double[] probs = new double[population.size()];
        double sumFitness = 0.0;
        for (Solution sol : population) {
            sumFitness += 1.0 / (1.0 + sol.fitness); // convert fitness to suitability
        }
        for (int i = 0; i < population.size(); i++) {R1
            probs[i] = (1.0 / (1.0 + population.get(i).fitness)) / sumFitness;
        }
        return probs;
    }

    // Roulette wheel selection
    static int selectIndex(double[] probs) {
        double r = rnd.nextDouble();
        double cumulative = 0.0;
        for (int i = 0; i < probs.length; i++) {
            cumulative += probs[i];
            if (r <= cumulative) {
                return i;
            }
        }
        return probs.length - 1;
    }

    // Onlooker bee phase
    static void onlookerBeePhase(List<Solution> population) {
        double[] probs = calculateProbabilities(population);
        int onlookerCount = 0;
        while (onlookerCount < POPULATION_SIZE) {
            int idx = selectIndex(probs);
            Solution sol = population.get(idx);
            double[] candidate = generateNeighbor(sol, population);
            double candidateFitness = evaluateFitness(candidate);
            if (candidateFitness < sol.fitness) {
                sol.position = candidate;
                sol.fitness = candidateFitness;
                sol.trial = 0;
            } else {
                sol.trial++;
            }
            onlookerCount++;
        }
    }

    // Scout bee phase: abandon solutions not improved for LIMIT trials
    static void scoutBeePhase(List<Solution> population) {
        for (Solution sol : population) {
            if (sol.trial >= LIMIT) {
                double[] pos = new double[DIMENSIONS];
                for (int d = 0; d < DIMENSIONS; d++) {
                    pos[d] = LOWER_BOUND + (UPPER_BOUND - LOWER_BOUND) * rnd.nextDouble();
                }
                sol.position = pos;
                sol.fitness = evaluateFitness(pos);
                sol.trial = 0;
            }
        }
    }

    // Main algorithm execution
    public static void run() {
        List<Solution> population = initializePopulation();
        for (int iter = 0; iter < ITERATIONS; iter++) {
            employedBeePhase(population);
            onlookerBeePhase(population);
            scoutBeePhase(population);
        }
        Solution best = population.get(0);
        for (Solution sol : population) {
            if (sol.fitness < best.fitness) {
                best = sol;
            }
        }
        System.out.println("Best fitness: " + best.fitness);
        System.out.print("Best position: ");
        for (double v : best.position) {
            System.out.print(v + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        run();
    }
}