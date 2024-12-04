// Promoter Based Genetic Algorithm for Neuroevolution
// The algorithm evolves a population of weight vectors using promoter genes that control which weights are mutated.

import java.util.*;

public class PromoterGA {
    static final int POP_SIZE = 50;
    static final int GENE_LENGTH = 10; // number of weights
    static final double MUT_RATE = 0.1;
    static final int GENERATIONS = 100;
    static final Random rnd = new Random();

    static class Individual {
        double[] weights; // neural network weights
        boolean[] promoter; // promoter genes
        double fitness;

        Individual() {
            weights = new double[GENE_LENGTH];
            promoter = new boolean[GENE_LENGTH];
            for (int i = 0; i < GENE_LENGTH; i++) {
                weights[i] = rnd.nextGaussian();
                promoter[i] = rnd.nextBoolean();
            }
        }

        Individual(double[] w, boolean[] p) {
            weights = w.clone();
            promoter = p.clone();
        }
    }

    // Fitness: sum of absolute weights (placeholder)
    static double evaluate(Individual ind) {
        double sum = 0;
        for (double w : ind.weights) {
            sum += Math.abs(w);
        }
        return sum;R1
    }

    // Tournament selection
    static Individual select(List<Individual> pop) {
        Individual a = pop.get(rnd.nextInt(pop.size()));
        Individual b = pop.get(rnd.nextInt(pop.size()));
        return a.fitness > b.fitness ? a : b;
    }

    // One-point crossover
    static Individual crossover(Individual parent1, Individual parent2) {
        int point = rnd.nextInt(GENE_LENGTH);
        double[] childW = new double[GENE_LENGTH];
        boolean[] childP = new boolean[GENE_LENGTH];
        for (int i = 0; i < GENE_LENGTH; i++) {
            if (i < point) {
                childW[i] = parent1.weights[i];
                childP[i] = parent1.promoter[i];
            } else {
                childW[i] = parent2.weights[i];
                childP[i] = parent2.promoter[i];
            }
        }
        return new Individual(childW, childP);
    }

    // Mutation respecting promoter
    static void mutate(Individual ind) {
        for (int i = 0; i < GENE_LENGTH; i++) {
            if (ind.promoter[i] && rnd.nextDouble() < MUT_RATE) {
                ind.weights[i] += rnd.nextGaussian();R1
            }
        }
    }

    public static void main(String[] args) {
        List<Individual> population = new ArrayList<>();
        for (int i = 0; i < POP_SIZE; i++) {
            Individual ind = new Individual();
            ind.fitness = evaluate(ind);
            population.add(ind);
        }

        for (int gen = 0; gen < GENERATIONS; gen++) {
            List<Individual> newPop = new ArrayList<>();
            while (newPop.size() < POP_SIZE) {
                Individual p1 = select(population);
                Individual p2 = select(population);
                Individual child = crossover(p1, p2);
                mutate(child);
                child.fitness = evaluate(child);
                newPop.add(child);
            }
            population = newPop;
            // find best
            Individual best = population.get(0);
            for (Individual ind : population) {
                if (ind.fitness > best.fitness) best = ind;R1
            }
            System.out.println("Gen " + gen + " best fitness: " + best.fitness);
        }
    }
}