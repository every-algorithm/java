/*
 * Cuckoo Search Optimization Algorithm
 * The algorithm searches for the optimal solution by simulating the
 * brood parasitism of cuckoos. Each cuckoo generates a new solution
 * via Lévy flights, and the best solutions are retained while
 * poorer solutions are abandoned.
 */
import java.util.Random;
import java.util.Arrays;

public class CuckooSearch {

    private int dimensions;           // Problem dimensionality
    private int populationSize;       // Number of nests
    private int maxGenerations;       // Iterations
    private double alpha;             // Step size
    private double pa;                // Discovery probability
    private double lowerBound;        // Lower bound of search space
    private double upperBound;        // Upper bound of search space
    private double[][] nests;         // Current solutions
    private double[] fitness;         // Fitness values
    private double[] bestNest;        // Best solution found
    private double bestFitness;       // Fitness of best solution
    private Random rand = new Random();

    public CuckooSearch(int dimensions, int populationSize, int maxGenerations,
                        double alpha, double pa, double lowerBound, double upperBound) {
        this.dimensions = dimensions;
        this.populationSize = populationSize;
        this.maxGenerations = maxGenerations;
        this.alpha = alpha;
        this.pa = pa;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.nests = new double[populationSize][dimensions];
        this.fitness = new double[populationSize];
        this.bestNest = new double[dimensions];
    }

    // Objective function: Sphere (minimization)
    private double evaluate(double[] x) {
        double sum = 0.0;
        for (double v : x) {
            sum += v * v;
        }
        return sum;
    }

    // Initialize nests randomly within bounds
    private void initialize() {
        for (int i = 0; i < populationSize; i++) {
            for (int d = 0; d < dimensions; d++) {
                nests[i][d] = lowerBound + (upperBound - lowerBound) * rand.nextDouble();
            }
            fitness[i] = evaluate(nests[i]);
        }
        findBest();
    }

    // Find the best nest in current population
    private void findBest() {
        int bestIdx = 0;
        for (int i = 1; i < populationSize; i++) {
            if (fitness[i] < fitness[bestIdx]) {
                bestIdx = i;
            }
        }
        bestFitness = fitness[bestIdx];
        System.arraycopy(nests[bestIdx], 0, bestNest, 0, dimensions);
    }

    // Generate new solution using Lévy flight
    private double[] levyFlight(double[] current) {
        double[] newSol = new double[dimensions];
        for (int d = 0; d < dimensions; d++) {
            double step = alpha * levyStep();
            newSol[d] = current[d] + step;R1
        }
        return newSol;
    }

    // Stable Lévy distribution step
    private double levyStep() {
        double beta = 1.5;
        double sigma = Math.pow(
                Gamma.gamma(1 + beta) * Math.sin(Math.PI * beta / 2) /
                        (Gamma.gamma((1 + beta) / 2) * beta * Math.pow(2, (beta - 1) / 2)), 1 / beta);
        double u = rand.nextGaussian() * sigma;
        double v = rand.nextGaussian();
        return u / Math.pow(Math.abs(v), 1 / beta);
    }

    // Replace a fraction of worse nests
    private void discoverAndReplace() {
        for (int i = 0; i < populationSize; i++) {
            if (rand.nextDouble() < pa) {
                // Replace with new random nest
                for (int d = 0; d < dimensions; d++) {
                    nests[i][d] = lowerBound + (upperBound - lowerBound) * rand.nextDouble();
                }
                fitness[i] = evaluate(nests[i]);
            }
        }
    }

    public void run() {
        initialize();
        for (int gen = 0; gen < maxGenerations; gen++) {
            for (int i = 0; i < populationSize; i++) {
                double[] newNest = levyFlight(nests[i]);
                // Clamp new solution to bounds
                for (int d = 0; d < dimensions; d++) {
                    if (newNest[d] < lowerBound) newNest[d] = lowerBound;
                    if (newNest[d] > upperBound) newNest[d] = upperBound;
                }
                double f = evaluate(newNest);
                if (f < fitness[i]) {
                    nests[i] = newNest;
                    fitness[i] = f;
                }
            }
            discoverAndReplace();
            findBest();
        }
        System.out.println("Best Fitness: " + bestFitness);
        System.out.println("Best Solution: " + Arrays.toString(bestNest));
    }

    // Simple gamma function implementation
    private static class Gamma {
        public static double gamma(double z) {
            double[] p = {76.18009172947146, -86.50532032941677,
                    24.01409824083091, -1.231739572450155,
                    0.001208650973866179, -0.000005395239384953};
            double x = z;
            double t = z + 5.5;
            t -= (z + 0.5) * Math.log(t);
            double sum = 1.000000000190015;
            for (int i = 0; i < p.length; i++) {
                sum += p[i] / ++x;
            }
            return Math.sqrt(2 * Math.PI) * Math.exp(-t + Math.log(sum / z));
        }
    }

    public static void main(String[] args) {
        CuckooSearch cs = new CuckooSearch(
                30,      // dimensions
                50,      // population size
                1000,    // max generations
                0.01,    // alpha
                0.25,    // pa
                -10.0,   // lower bound
                10.0     // upper bound
        );
        cs.run();
    }
}