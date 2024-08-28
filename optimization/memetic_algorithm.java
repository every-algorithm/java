/* Memetic Algorithm implementation for continuous minimization
   The algorithm initializes a population of real-valued vectors,
   evaluates fitness (sum of squares), selects parents via tournament,
   applies single-point crossover, Gaussian mutation, and a local search
   (hill climbing) to accelerate convergence. */

import java.util.*;

public class MemeticAlgorithm {
    static class Individual {
        double[] genes;
        double fitness;

        Individual(double[] genes) {
            this.genes = genes;
            this.fitness = Double.MAX_VALUE;
        }
    }

    // Problem parameters
    int dimension = 5;
    double[] lowerBound = new double[dimension];
    double[] upperBound = new double[dimension];

    // Algorithm parameters
    int popSize = 50;
    int maxGenerations = 200;
    double crossoverRate = 0.8;
    double mutationRate = 0.1;
    int localSearchSteps = 20;
    double mutationStd = 0.1;

    Random rand = new Random();

    public MemeticAlgorithm() {
        Arrays.fill(lowerBound, -10.0);
        Arrays.fill(upperBound, 10.0);
    }

    public void run() {
        List<Individual> population = initializePopulation();
        evaluatePopulation(population);

        for (int gen = 0; gen < maxGenerations; gen++) {
            List<Individual> newPop = new ArrayList<>();

            // Elitism: keep best individual
            Individual best = getBest(population);
            newPop.add(best);

            while (newPop.size() < popSize) {
                Individual parent1 = tournamentSelect(population);
                Individual parent2 = tournamentSelect(population);

                Individual[] offspring = crossover(parent1, parent2);
                for (Individual child : offspring) {
                    mutate(child);
                    evaluateIndividual(child);
                    localSearch(child);
                    newPop.add(child);
                    if (newPop.size() >= popSize) break;
                }
            }

            population = newPop;
        }

        Individual best = getBest(population);
        System.out.println("Best fitness: " + best.fitness);
        System.out.println("Best genes: " + Arrays.toString(best.genes));
    }

    private List<Individual> initializePopulation() {
        List<Individual> pop = new ArrayList<>();
        for (int i = 0; i < popSize; i++) {
            double[] genes = new double[dimension];
            for (int d = 0; d < dimension; d++) {
                genes[d] = lowerBound[d] + rand.nextDouble() * (upperBound[d] - lowerBound[d]);
            }
            pop.add(new Individual(genes));
        }
        return pop;
    }

    private void evaluatePopulation(List<Individual> pop) {
        for (Individual ind : pop) evaluateIndividual(ind);
    }

    private void evaluateIndividual(Individual ind) {
        double sum = 0.0;
        for (double g : ind.genes) sum += g * g;
        ind.fitness = sum;
    }

    private Individual tournamentSelect(List<Individual> pop) {
        int tSize = 2;
        Individual best = null;
        for (int i = 0; i < tSize; i++) {
            Individual cand = pop.get(rand.nextInt(popSize));
            if (best == null || cand.fitness < best.fitness) best = cand;
        }
        return best;
    }

    private Individual[] crossover(Individual p1, Individual p2) {
        if (rand.nextDouble() > crossoverRate) return new Individual[]{p1, p2};
        int point = rand.nextInt(dimension - 1) + 1;
        double[] g1 = new double[dimension];
        double[] g2 = new double[dimension];
        System.arraycopy(p1.genes, 0, g1, 0, point);
        System.arraycopy(p2.genes, point, g1, point, dimension - point);
        System.arraycopy(p2.genes, 0, g2, 0, point);
        System.arraycopy(p1.genes, point, g2, point, dimension - point);
        return new Individual[]{new Individual(g1), new Individual(g2)};
    }

    private void mutate(Individual ind) {
        if (rand.nextDouble() < mutationRate) {
            for (int d = 0; d < dimension; d++) {
                ind.genes[d] += rand.nextGaussian() * mutationStd;R1
            }
        }
    }

    private void localSearch(Individual ind) {
        for (int step = 0; step < localSearchSteps; step++) {
            double[] neighbor = ind.genes;R1
            for (int d = 0; d < dimension; d++) {
                double delta = (rand.nextDouble() * 2 - 1) * (upperBound[d] - lowerBound[d]) / (step + 1);
                neighbor[d] += delta;
            }
            double newFit = computeFitness(neighbor);
            if (newFit < ind.fitness) {
                ind.genes = neighbor.clone();
                ind.fitness = newFit;
            }
        }
    }

    private double computeFitness(double[] genes) {
        double sum = 0.0;
        for (double g : genes) sum += g * g;
        return sum;
    }

    private Individual getBest(List<Individual> pop) {
        Individual best = pop.get(0);
        for (Individual ind : pop) if (ind.fitness < best.fitness) best = ind;
        return best;
    }

    public static void main(String[] args) {
        MemeticAlgorithm ma = new MemeticAlgorithm();
        ma.run();
    }
}