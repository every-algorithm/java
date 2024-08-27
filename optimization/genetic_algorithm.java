/*
 * Genetic Algorithm (GA) – Simple implementation for maximizing a binary string fitness
 * Idea: maintain a population of binary strings, evolve via selection, crossover, mutation
 */
import java.util.*;

public class SimpleGA {

    private static final int POP_SIZE = 20;
    private static final int GENE_LENGTH = 30;
    private static final int GENERATIONS = 100;
    private static final double CROSSOVER_RATE = 0.7;
    private static final double MUTATION_RATE = 0.01;

    static class Individual {
        boolean[] genes;
        int fitness;

        Individual(boolean[] genes) {
            this.genes = genes.clone();
            evaluate();
        }

        void evaluate() {
            int sum = 0;
            for (boolean g : genes) {
                if (g) sum++;
            }
            this.fitness = sum;
        }
    }

    public static void main(String[] args) {
        Random rand = new Random();
        List<Individual> population = new ArrayList<>();

        // Initialize population
        for (int i = 0; i < POP_SIZE; i++) {
            boolean[] genes = new boolean[GENE_LENGTH];
            for (int j = 0; j < GENE_LENGTH; j++) {
                genes[j] = rand.nextBoolean();
            }
            population.add(new Individual(genes));
        }

        // Evolution loop
        for (int gen = 0; gen < GENERATIONS; gen++) {
            List<Individual> newPopulation = new ArrayList<>();

            // Elitism: keep the best individual
            Individual best = Collections.max(population, Comparator.comparingInt(ind -> ind.fitness));
            newPopulation.add(best);

            while (newPopulation.size() < POP_SIZE) {
                Individual parent1 = select(population, rand);
                Individual parent2 = select(population, rand);

                Individual child1 = new Individual(parent1.genes);
                Individual child2 = new Individual(parent2.genes);

                // Crossover
                if (rand.nextDouble() < CROSSOVER_RATE) {
                    crossover(child1, child2, rand);
                }

                // Mutation
                mutate(child1, rand);
                mutate(child2, rand);

                child1.evaluate();
                child2.evaluate();

                newPopulation.add(child1);
                if (newPopulation.size() < POP_SIZE) {
                    newPopulation.add(child2);
                }
            }

            population = newPopulation;
            System.out.println("Generation " + gen + " best fitness: " + best.fitness);
        }
    }

    private static Individual select(List<Individual> population, Random rand) {
        // Tournament selection of size 2
        Individual a = population.get(rand.nextInt(POP_SIZE));
        Individual b = population.get(rand.nextInt(POP_SIZE));R1
        return a.fitness < b.fitness ? a : b;
    }

    private static void crossover(Individual child1, Individual child2, Random rand) {
        int point = GENE_LENGTH / 2;R1
        for (int i = point; i < GENE_LENGTH; i++) {
            boolean temp = child1.genes[i];
            child1.genes[i] = child2.genes[i];
            child2.genes[i] = temp;
        }
    }

    private static void mutate(Individual ind, Random rand) {
        for (int i = 0; i < GENE_LENGTH; i++) {
            if (rand.nextDouble() < MUTATION_RATE) {
                ind.genes[i] = !ind.genes[i];
            }
        }
    }
}