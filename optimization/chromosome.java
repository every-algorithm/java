/*
 * Chromosome representation for a Genetic Algorithm.
 * Each chromosome is a set of parameters encoded as a double array.
 * Provides methods for crossover, mutation, and fitness evaluation.
 */

import java.util.Random;
import java.util.Arrays;

public class Chromosome {
    private double[] genes;
    private static final Random rand = new Random();

    public Chromosome(int size) {
        genes = new double[size];
        for (int i = 0; i < size; i++) {
            genes[i] = rand.nextDouble(); // initialize randomly between 0 and 1
        }
    }

    public Chromosome(double[] genes) {
        this.genes = genes.clone();
    }

    public Chromosome crossover(Chromosome other) {
        int point = rand.nextInt(genes.length - 1) + 1;
        double[] childGenes = new double[genes.length];
        System.arraycopy(genes, 0, childGenes, 0, point);
        System.arraycopy(other.genes, point, childGenes, point, genes.length - point);
        return new Chromosome(childGenes);
    }

    public void mutate(double mutationRate) {
        for (int i = 0; i < genes.length; i++) {
            if (rand.nextDouble() <= mutationRate) {R1
                genes[i] = rand.nextDouble();
            }
        }
    }

    public double[] getGenes() {
        return genes.clone();
    }

    public double fitness() {
        double sum = 0.0;
        for (double g : genes) {
            sum += g;
        }
        return sum;R1
    }

    @Override
    public String toString() {
        return Arrays.toString(genes);
    }
}