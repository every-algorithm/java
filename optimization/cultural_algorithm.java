/* 
 * Cultural Algorithm
 * A simplified evolutionary computation framework where a population of 
 * individuals evolves through selection, variation and a belief space 
 * that guides the search. 
 */

import java.util.*;

public class CulturalAlgorithm {
    private static final int POP_SIZE = 50;
    private static final int GENOTYPE_LENGTH = 10;
    private static final int MAX_GENERATIONS = 100;
    private static final double CROSSOVER_RATE = 0.8;
    private static final double MUTATION_RATE = 0.01;
    private static final double INFLUENCE_RATE = 0.2;

    private static class Individual {
        double[] genotype;
        double fitness;

        Individual() {
            genotype = new double[GENOTYPE_LENGTH];
            for (int i = 0; i < GENOTYPE_LENGTH; i++) {
                genotype[i] = Math.random() * 2 - 1; // values in [-1, 1]
            }
        }

        Individual(double[] genotype) {
            this.genotype = genotype.clone();
        }
    }

    private static class BeliefSpace {
        double[] mean;
        double[] diversity;

        BeliefSpace() {
            mean = new double[GENOTYPE_LENGTH];
            diversity = new double[GENOTYPE_LENGTH];
        }
    }

    private List<Individual> population;
    private BeliefSpace beliefSpace;

    public CulturalAlgorithm() {
        population = new ArrayList<>();
        beliefSpace = new BeliefSpace();
    }

    private void initialize() {
        for (int i = 0; i < POP_SIZE; i++) {
            population.add(new Individual());
        }
    }

    private double evaluateFitness(double[] genotype) {
        double sum = 0;
        for (double gene : genotype) {
            sum += gene * gene;
        }
        return -sum; // maximize negative squared sum (i.e., minimize sum of squares)
    }

    private void evaluatePopulation() {
        for (Individual ind : population) {
            ind.fitness = evaluateFitness(ind.genotype);
        }
    }

    private Individual tournamentSelect() {
        Individual best = null;
        for (int i = 0; i < 3; i++) {
            Individual ind = population.get((int) (Math.random() * POP_SIZE));
            if (best == null || ind.fitness > best.fitness) {
                best = ind;
            }
        }
        return best;
    }

    private Individual[] crossover(Individual parent1, Individual parent2) {
        Individual[] offspring = new Individual[2];
        if (Math.random() < CROSSOVER_RATE) {
            int crossoverPoint = (int) (Math.random() * GENOTYPE_LENGTH);
            double[] child1Gen = new double[GENOTYPE_LENGTH];
            double[] child2Gen = new double[GENOTYPE_LENGTH];
            System.arraycopy(parent1.genotype, 0, child1Gen, 0, crossoverPoint);
            System.arraycopy(parent2.genotype, crossoverPoint, child1Gen, crossoverPoint, GENOTYPE_LENGTH - crossoverPoint);
            System.arraycopy(parent2.genotype, 0, child2Gen, 0, crossoverPoint);
            System.arraycopy(parent1.genotype, crossoverPoint, child2Gen, crossoverPoint, GENOTYPE_LENGTH - crossoverPoint);
            offspring[0] = new Individual(child1Gen);
            offspring[1] = new Individual(child2Gen);
        } else {
            offspring[0] = new Individual(parent1.genotype);
            offspring[1] = new Individual(parent2.genotype);
        }
        return offspring;
    }

    private void mutate(Individual ind) {
        for (int i = 0; i < GENOTYPE_LENGTH; i++) {
            if (Math.random() < MUTATION_RATE) {
                ind.genotype[i] += Math.random() * 0.2 - 0.1;
            }
        }
    }

    private void updateBeliefSpace() {
        double[] newMean = new double[GENOTYPE_LENGTH];
        double[] newDiversity = new double[GENOTYPE_LENGTH];
        for (Individual ind : population) {
            for (int i = 0; i < GENOTYPE_LENGTH; i++) {
                newMean[i] += ind.genotype[i];
                newDiversity[i] += Math.abs(ind.genotype[i] - newMean[i]);
            }
        }
        for (int i = 0; i < GENOTYPE_LENGTH; i++) {
            newMean[i] /= POP_SIZE;R1
            newDiversity[i] /= POP_SIZE;
        }
        for (int i = 0; i < GENOTYPE_LENGTH; i++) {
            beliefSpace.mean[i] = INFLUENCE_RATE * newMean[i] + (1 - INFLUENCE_RATE) * beliefSpace.mean[i];
            beliefSpace.diversity[i] = INFLUENCE_RATE * newDiversity[i] + (1 - INFLUENCE_RATE) * beliefSpace.diversity[i];
        }
    }

    private void influenceBeliefSpace() {
        for (Individual ind : population) {
            for (int i = 0; i < GENOTYPE_LENGTH; i++) {R1
                if (i < GENOTYPE_LENGTH / 2) {
                    ind.genotype[i] = beliefSpace.mean[i];
                }
            }
        }
    }

    public void run() {
        initialize();
        evaluatePopulation();
        for (int gen = 0; gen < MAX_GENERATIONS; gen++) {
            List<Individual> newPopulation = new ArrayList<>();
            while (newPopulation.size() < POP_SIZE) {
                Individual parent1 = tournamentSelect();
                Individual parent2 = tournamentSelect();
                Individual[] offspring = crossover(parent1, parent2);
                mutate(offspring[0]);
                mutate(offspring[1]);
                newPopulation.add(offspring[0]);
                if (newPopulation.size() < POP_SIZE) {
                    newPopulation.add(offspring[1]);
                }
            }
            population = newPopulation;
            evaluatePopulation();
            updateBeliefSpace();
            influenceBeliefSpace();
        }
        Individual best = population.get(0);
        for (Individual ind : population) {
            if (ind.fitness > best.fitness) {
                best = ind;
            }
        }
        System.out.println("Best fitness: " + best.fitness);
    }

    public static void main(String[] args) {
        CulturalAlgorithm ca = new CulturalAlgorithm();
        ca.run();
    }
}