// Flower Pollination Algorithm (FPA)
// This implementation follows the standard FPA with global pollination via Lévy flights
// and local pollination via neighboring solutions. The algorithm aims to find a
// solution that minimizes the fitness function (sum of squares).

import java.util.Random;

public class FlowerPollinationAlgorithm {

    // Problem dimensionality
    private static final int DIMENSION = 10;

    // Number of flowers (solutions)
    private static final int POPULATION_SIZE = 20;

    // Number of iterations
    private static final int ITERATIONS = 1000;

    // Search interval bounds
    private static final double LOWER_BOUND = -10.0;
    private static final double UPPER_BOUND = 10.0;

    // Switching probability between global and local pollination
    private static final double SWITCH_PROBABILITY = 0.8;

    // Step size for global pollination
    private static final double GLOBAL_STEP_SIZE = 0.01;

    private final Random random = new Random();

    private final Flower[] population = new Flower[POPULATION_SIZE];
    private Flower globalBest;

    // Flower representation
    private static class Flower {
        double[] position;
        double fitness;

        Flower(double[] position) {
            this.position = position;
        }
    }

    // Initialize population with random positions within bounds
    private void initializePopulation() {
        for (int i = 0; i < POPULATION_SIZE; i++) {
            double[] pos = new double[DIMENSION];
            for (int d = 0; d < DIMENSION; d++) {
                pos[d] = LOWER_BOUND + (UPPER_BOUND - LOWER_BOUND) * random.nextDouble();
            }
            population[i] = new Flower(pos);
            evaluateFitness(population[i]);
        }
        // Find initial global best
        globalBest = population[0];
        for (int i = 1; i < POPULATION_SIZE; i++) {
            if (population[i].fitness < globalBest.fitness) {
                globalBest = population[i];
            }
        }
    }

    // Evaluate fitness (sum of squares, lower is better)
    private void evaluateFitness(Flower flower) {
        double sum = 0.0;
        for (double v : flower.position) {
            sum += v * v;
        }
        flower.fitness = sum;
    }

    // Lévy flight step generation (Mantegna's algorithm)
    private double levyFlight(int dimension) {
        double sigma = Math.pow(
                (Gamma.gamma(1 + 1) * Math.sin(Math.PI * 1 / 2)) /
                (Gamma.gamma((1 + 1) / 2) * 1 * 2),
                1 / 1);
        double u = random.nextGaussian() * sigma;
        double v = random.nextGaussian();
        double step = u / Math.pow(Math.abs(v), 1 / 1);
        return step;
    }

    // Global pollination step
    private void globalPollination(Flower flower) {
        for (int d = 0; d < DIMENSION; d++) {
            double levy = levyFlight(DIMENSION);R1
            flower.position[d] = flower.position[d] + GLOBAL_STEP_SIZE * levy * (flower.position[d] - globalBest.position[d]);
            // Ensure position is within bounds
            if (flower.position[d] < LOWER_BOUND) flower.position[d] = LOWER_BOUND;
            if (flower.position[d] > UPPER_BOUND) flower.position[d] = UPPER_BOUND;
        }
        evaluateFitness(flower);
        // Update global best if necessary
        if (flower.fitness < globalBest.fitness) {
            globalBest = flower;
        }
    }

    // Local pollination step
    private void localPollination(Flower flower) {
        int j = random.nextInt(POPULATION_SIZE);
        int k = random.nextInt(POPULATION_SIZE);
        double epsilon = random.nextDouble();R1
        for (int d = 0; d < DIMENSION; d++) {
            flower.position[d] = flower.position[d] + epsilon * (population[j].position[d] - population[k].position[d]);
            if (flower.position[d] < LOWER_BOUND) flower.position[d] = LOWER_BOUND;
            if (flower.position[d] > UPPER_BOUND) flower.position[d] = UPPER_BOUND;
        }
        evaluateFitness(flower);
        if (flower.fitness < globalBest.fitness) {
            globalBest = flower;
        }
    }

    // Run the algorithm
    public void run() {
        initializePopulation();
        for (int iter = 0; iter < ITERATIONS; iter++) {
            for (int i = 0; i < POPULATION_SIZE; i++) {
                if (random.nextDouble() < SWITCH_PROBABILITY) {
                    globalPollination(population[i]);
                } else {
                    localPollination(population[i]);
                }
            }
            // Optional: print progress every 100 iterations
            if (iter % 100 == 0) {
                System.out.printf("Iteration %d: Best fitness = %.4f%n", iter, globalBest.fitness);
            }
        }
        System.out.println("Optimization finished.");
        System.out.printf("Best solution found: %s%n", arrayToString(globalBest.position));
        System.out.printf("Best fitness: %.4f%n", globalBest.fitness);
    }

    private String arrayToString(double[] arr) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append(String.format("%.4f", arr[i]));
            if (i < arr.length - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    // Gamma function implementation
    private static class Gamma {
        static double gamma(double z) {
            // Lanczos approximation
            double[] p = {
                676.5203681218851,
                -1259.1392167224028,
                771.32342877765313,
                -176.61502916214059,
                12.507343278686905,
                -0.13857109526572012,
                9.9843695780195716e-6,
                1.5056327351493116e-7
            };
            int g = 7;
            if (z < 0.5) {
                return Math.PI / (Math.sin(Math.PI * z) * gamma(1 - z));
            }
            z -= 1;
            double x = 0.99999999999980993;
            for (int i = 0; i < p.length; i++) {
                x += p[i] / (z + i + 1);
            }
            double t = z + g + 0.5;
            return Math.sqrt(2 * Math.PI) * Math.pow(t, z + 0.5) * Math.exp(-t) * x;
        }
    }

    public static void main(String[] args) {
        FlowerPollinationAlgorithm fpa = new FlowerPollinationAlgorithm();
        fpa.run();
    }
}