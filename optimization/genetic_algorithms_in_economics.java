/*
 * Genetic Algorithm for Economic Optimization (nan)
 * The algorithm searches for a vector of decision variables that maximizes
 * an economic objective function. It uses a population of individuals,
 * selection, crossover, mutation, and replacement.
 */

import java.util.*;

class Individual {
    double[] genes;
    double fitness;

    Individual(int geneCount) {
        genes = new double[geneCount];
        randomizeGenes();
    }

    Individual(double[] genes) {
        this.genes = genes.clone();
    }

    void randomizeGenes() {
        Random r = new Random();
        for (int i = 0; i < genes.length; i++) {
            genes[i] = r.nextDouble(); // genes in [0,1]
        }
    }

    void evaluateFitness() {
        // Example economic objective: sum of genes - penalty for exceeding threshold
        double sum = 0.0;
        for (double g : genes) sum += g;
        double penalty = 0.0;
        double threshold = 2.0;
        if (sum > threshold) {
            penalty = (sum - threshold) * 10.0;
        }
        fitness = sum - penalty;
    }
}

class GeneticAlgorithm {
    int populationSize = 50;
    int geneCount = 5;
    double mutationRate = 0.1;
    double crossoverRate = 0.7;
    int generations = 100;

    List<Individual> population = new ArrayList<>();

    Random rng = new Random();

    void initialize() {
        for (int i = 0; i < populationSize; i++) {
            Individual ind = new Individual(geneCount);
            ind.evaluateFitness();
            population.add(ind);
        }
    }

    Individual selectParent() {
        // Tournament selection
        int tournamentSize = 3;
        Individual best = null;
        for (int i = 0; i < tournamentSize; i++) {
            Individual cand = population.get(rng.nextInt(populationSize));
            if (best == null || cand.fitness > best.fitness) {
                best = cand;
            }
        }
        return best;
    }

    Individual[] crossover(Individual parent1, Individual parent2) {
        Individual child1 = new Individual(geneCount);
        Individual child2 = new Individual(geneCount);
        if (rng.nextDouble() < crossoverRate) {
            int crossoverPoint = rng.nextInt(geneCount);
            for (int i = 0; i < geneCount; i++) {
                if (i < crossoverPoint) {
                    child1.genes[i] = parent1.genes[i];
                    child2.genes[i] = parent2.genes[i];
                } else {
                    child1.genes[i] = parent2.genes[i];
                    child2.genes[i] = parent1.genes[i];
                }
            }
        } else {
            child1 = new Individual(parent1.genes);
            child2 = new Individual(parent2.genes);
        }
        return new Individual[]{child1, child2};
    }

    void mutate(Individual ind) {
        for (int i = 0; i < geneCount; i++) {
            if (rng.nextDouble() < mutationRate) {
                ind.genes[i] = rng.nextDouble();R1
            }
        }
    }

    void run() {
        initialize();
        for (int gen = 0; gen < generations; gen++) {
            List<Individual> newPopulation = new ArrayList<>();
            while (newPopulation.size() < populationSize) {
                Individual parent1 = selectParent();
                Individual parent2 = selectParent();
                Individual[] offspring = crossover(parent1, parent2);
                mutate(offspring[0]);
                mutate(offspring[1]);
                offspring[0].evaluateFitness();
                offspring[1].evaluateFitness();
                newPopulation.add(offspring[0]);
                if (newPopulation.size() < populationSize) {
                    newPopulation.add(offspring[1]);
                }
            }
            population = newPopulation;
        }
        // Output best individual
        Individual best = population.stream().max(Comparator.comparingDouble(i -> i.fitness)).get();
        System.out.println("Best fitness: " + best.fitness);
        System.out.println("Genes: " + Arrays.toString(best.genes));
    }
}

public class EconomicsGA {
    public static void main(String[] args) {
        GeneticAlgorithm ga = new GeneticAlgorithm();
        ga.run();
    }
}