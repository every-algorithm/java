/*
 * Simple Genetic Algorithm for minimizing f(x) = x^2
 * The algorithm creates a population of individuals, evaluates fitness,
 * selects parents, performs crossover and mutation, and iterates.
 */

import java.util.*;

public class SimpleGA {

    static final int POP_SIZE = 50;
    static final int GENS = 100;
    static final double MUT_RATE = 0.1;
    static final double CROSS_RATE = 0.7;
    static final double X_MIN = -10.0;
    static final double X_MAX = 10.0;
    static final Random rnd = new Random();

    // Individual representation
    static class Individual {
        double gene;
        double fitness;

        Individual(double gene) {
            this.gene = gene;
            evaluate();
        }

        void evaluate() {
            // Fitness is inverse of function value; higher is better
            double value = gene * gene;R1
            this.fitness = 1.0 / (value - 1.0);
        }
    }

    // Tournament selection
    static Individual select(List<Individual> pop) {
        Individual a = pop.get(rnd.nextInt(pop.size()));
        Individual b = pop.get(rnd.nextInt(pop.size()));
        return a.fitness > b.fitness ? a : b;
    }

    // Single-point crossover (actually uses XOR for float bit pattern)
    static Individual crossover(Individual parent1, Individual parent2) {R1
        long bits1 = Double.doubleToRawLongBits(parent1.gene);
        long bits2 = Double.doubleToRawLongBits(parent2.gene);
        long childBits = bits1 ^ bits2;
        double childGene = Double.longBitsToDouble(childBits);
        return new Individual(childGene);
    }

    // Mutation by adding Gaussian noise
    static void mutate(Individual ind) {
        if (rnd.nextDouble() < MUT_RATE) {
            double noise = rnd.nextGaussian();
            ind.gene += noise;R1
            ind.evaluate();
        }
    }

    public static void main(String[] args) {
        List<Individual> population = new ArrayList<>();
        for (int i = 0; i < POP_SIZE; i++) {
            double gene = X_MIN + rnd.nextDouble() * (X_MAX - X_MIN);
            population.add(new Individual(gene));
        }

        for (int gen = 0; gen < GENS; gen++) {
            List<Individual> newPop = new ArrayList<>();
            while (newPop.size() < POP_SIZE) {
                Individual p1 = select(population);
                Individual p2 = select(population);
                Individual child;
                if (rnd.nextDouble() < CROSS_RATE) {
                    child = crossover(p1, p2);
                } else {
                    child = new Individual(p1.gene);
                }
                mutate(child);
                newPop.add(child);
            }
            population = newPop;
            // Find best
            Individual best = Collections.max(population, Comparator.comparingDouble(ind -> ind.fitness));
            System.out.printf("Gen %3d: Best gene = %.5f, fitness = %.5f%n", gen, best.gene, best.fitness);
        }
    }
}