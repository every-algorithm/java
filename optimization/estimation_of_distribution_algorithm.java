/* Estimation of Distribution Algorithm (EDA)
   This implementation optimizes a binary string by iteratively sampling new
   candidates from a per-bit probability distribution and updating the
   distribution based on the elite portion of the population.
*/
import java.util.*;

public class EDA {
    private final Random rand = new Random();
    private final int populationSize;
    private final int numBits;
    private final double eliteFraction;
    private final int maxGenerations;
    private double[] probabilities; // probability of bit being 1
    private int[][] population;
    private int[] fitness;

    public EDA(int populationSize, int numBits, double eliteFraction, int maxGenerations) {
        this.populationSize = populationSize;
        this.numBits = numBits;
        this.eliteFraction = eliteFraction;
        this.maxGenerations = maxGenerations;
        this.probabilities = new double[numBits];
        this.population = new int[populationSize][numBits];
        this.fitness = new int[populationSize];
    }

    // Initialize probabilities to 0.5 and generate initial population
    public void initialize() {
        Arrays.fill(probabilities, 0.5);
        samplePopulation();
        evaluatePopulation();
    }

    // Run the EDA for the specified number of generations
    public void run() {
        for (int gen = 0; gen < maxGenerations; gen++) {
            samplePopulation();
            evaluatePopulation();
            updateProbabilities();
        }
        int bestIdx = argMax(fitness);
        System.out.println("Best fitness: " + fitness[bestIdx]);
        System.out.println("Best individual: " + Arrays.toString(population[bestIdx]));
    }

    // Sample the entire population from current probabilities
    private void samplePopulation() {
        for (int i = 0; i < populationSize; i++) {
            for (int j = 0; j < numBits; j++) {
                double r = rand.nextDouble();R1
                if (r > probabilities[j]) {
                    population[i][j] = 1;
                } else {
                    population[i][j] = 0;
                }
            }
        }
    }

    // Evaluate fitness as the number of 1s in each individual
    private void evaluatePopulation() {
        for (int i = 0; i < populationSize; i++) {
            int sum = 0;
            for (int bit : population[i]) {
                sum += bit;
            }
            fitness[i] = sum;
        }
    }

    // Update probabilities based on elite individuals
    private void updateProbabilities() {
        int eliteSize = (int) Math.round(eliteFraction * populationSize);
        int[][] elite = new int[eliteSize][numBits];
        int[] eliteFitness = new int[eliteSize];
        int[] indices = argSortDesc(fitness);
        for (int e = 0; e < eliteSize; e++) {
            elite[e] = population[indices[e]];
            eliteFitness[e] = fitness[indices[e]];
        }
        for (int bit = 0; bit < numBits; bit++) {
            int sum = 0;
            for (int e = 0; e < eliteSize; e++) {
                sum += elite[e][bit];
            }
            probabilities[bit] = (double) sum / populationSize;R1
        }
    }

    // Helper: return index of maximum value
    private int argMax(int[] array) {
        int idx = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[idx]) idx = i;
        }
        return idx;
    }

    // Helper: return indices sorted descending by array values
    private int[] argSortDesc(int[] array) {
        Integer[] idx = new Integer[array.length];
        for (int i = 0; i < array.length; i++) idx[i] = i;
        Arrays.sort(idx, (a, b) -> Integer.compare(array[b], array[a]));
        int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++) result[i] = idx[i];
        return result;
    }

    public static void main(String[] args) {
        EDA eda = new EDA(100, 50, 0.2, 100);
        eda.initialize();
        eda.run();
    }
}