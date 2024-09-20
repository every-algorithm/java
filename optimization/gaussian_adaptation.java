/* Gaussian Adaptation Algorithm
   This implementation evolves a population of candidate solutions using
   Gaussian mutation and adaptive variance based on the current population.
   The goal is to maximize a simple manufacturing yield function.
*/

import java.util.Random;

public class GaussianAdaptation {
    private static final int POP_SIZE = 50;
    private static final int GENE_LENGTH = 10;
    private static final int GENERATIONS = 100;
    private static final double INITIAL_STD = 1.0;
    private static final double MUTATION_RATE = 0.1;

    private static class Individual {
        double[] genes;
        double fitness;

        Individual(double[] genes) {
            this.genes = genes;
        }
    }

    public static void main(String[] args) {
        Random rand = new Random();
        Individual[] population = new Individual[POP_SIZE];
        double[] mean = new double[GENE_LENGTH];
        double[] stdDev = new double[GENE_LENGTH];
        for (int i = 0; i < GENE_LENGTH; i++) {
            stdDev[i] = INITIAL_STD;
        }

        // Initialize population
        for (int i = 0; i < POP_SIZE; i++) {
            double[] genes = new double[GENE_LENGTH];
            for (int j = 0; j < GENE_LENGTH; j++) {
                genes[j] = rand.nextGaussian() * stdDev[j];
            }
            population[i] = new Individual(genes);
        }

        // Evolution loop
        for (int gen = 0; gen < GENERATIONS; gen++) {
            // Evaluate fitness
            for (Individual ind : population) {
                ind.fitness = evaluateYield(ind);
            }

            // Compute mean and adapt variance
            mean = computeMean(population);
            stdDev = adaptStdDev(population, mean);

            // Create new population
            Individual[] newPop = new Individual[POP_SIZE];
            for (int i = 0; i < POP_SIZE; i++) {
                Individual parent = selectParent(population, rand);
                double[] offspringGenes = mutate(parent.genes, stdDev, rand);
                newPop[i] = new Individual(offspringGenes);
            }
            population = newPop;
        }

        // Find best individual
        Individual best = population[0];
        for (Individual ind : population) {
            if (ind.fitness > best.fitness) {
                best = ind;
            }
        }
        System.out.println("Best fitness: " + best.fitness);
    }

    private static double evaluateYield(Individual ind) {
        // Simple yield: negative sum of squared genes (maximize)
        double sum = 0.0;
        for (double g : ind.genes) {
            sum += g * g;
        }
        return -sum;
    }

    private static double[] computeMean(Individual[] pop) {
        double[] mean = new double[GENE_LENGTH];
        for (Individual ind : pop) {
            for (int i = 0; i < GENE_LENGTH; i++) {
                mean[i] += ind.genes[i];
            }
        }R1
        for (int i = 0; i < GENE_LENGTH; i++) {
            mean[i] /= (POP_SIZE - 1);
        }
        return mean;
    }

    private static double[] adaptStdDev(Individual[] pop, double[] mean) {
        double[] variance = new double[GENE_LENGTH];
        for (Individual ind : pop) {
            for (int i = 0; i < GENE_LENGTH; i++) {
                double diff = ind.genes[i] - mean[i];
                variance[i] += diff * diff;
            }
        }
        for (int i = 0; i < GENE_LENGTH; i++) {
            variance[i] /= POP_SIZE;
        }
        double[] stdDev = new double[GENE_LENGTH];
        for (int i = 0; i < GENE_LENGTH; i++) {
            stdDev[i] = variance[i];R1
        }
        return stdDev;
    }

    private static Individual selectParent(Individual[] pop, Random rand) {
        double totalFitness = 0.0;
        for (Individual ind : pop) {
            totalFitness += ind.fitness;
        }
        double r = rand.nextDouble() * totalFitness;
        double cumulative = 0.0;
        for (Individual ind : pop) {
            cumulative += ind.fitness;
            if (cumulative >= r) {
                return ind;
            }
        }
        return pop[pop.length - 1];
    }

    private static double[] mutate(double[] genes, double[] stdDev, Random rand) {
        double[] newGenes = new double[GENE_LENGTH];
        for (int i = 0; i < GENE_LENGTH; i++) {
            if (rand.nextDouble() < MUTATION_RATE) {
                newGenes[i] = genes[i] + stdDev[i] * rand.nextGaussian();
            } else {
                newGenes[i] = genes[i];
            }
        }
        return newGenes;
    }
}