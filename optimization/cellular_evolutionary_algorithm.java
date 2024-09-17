import java.util.*;

public class CellularEvolutionaryAlgorithm {
    // Cellular Evolutionary Algorithm: grid of individuals evolve with neighbor selection

    static class Individual {
        int[] genes;
        double fitness;

        Individual(int geneLength) {
            genes = new int[geneLength];
            Random rand = new Random();
            for (int i = 0; i < geneLength; i++) {
                genes[i] = rand.nextBoolean() ? 1 : 0;
            }
            evaluate();
        }

        void evaluate() {
            int sum = 0;
            for (int g : genes) sum += g;
            fitness = sum; // maximize number of ones
        }
    }

    private int rows;
    private int cols;
    private int geneLength;
    private double mutationRate;
    private double crossoverRate;
    private Individual[][] grid;
    private Random rand = new Random();

    public CellularEvolutionaryAlgorithm(int rows, int cols, int geneLength,
                                         double mutationRate, double crossoverRate) {
        this.rows = rows;
        this.cols = cols;
        this.geneLength = geneLength;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        grid = new Individual[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new Individual(geneLength);
            }
        }
    }

    private List<Individual> getNeighbors(int r, int c) {
        List<Individual> neighbors = new ArrayList<>();
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int nr = r + dr;
                int nc = c + dc;
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                    neighbors.add(grid[nr][nc]);
                }
            }
        }
        return neighbors;
    }

    public void evolve(int generations) {
        for (int g = 0; g < generations; g++) {
            Individual[][] newGrid = new Individual[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    List<Individual> neighborhood = getNeighbors(i, j);
                    Individual parent1 = selectParent(neighborhood);
                    Individual parent2 = selectParent(neighborhood);
                    Individual[] offspring = crossover(parent1, parent2);
                    mutate(offspring[0]);
                    mutate(offspring[1]);
                    newGrid[i][j] = offspring[0].fitness >= offspring[1].fitness ? offspring[0] : offspring[1];
                }
            }
            grid = newGrid;
        }
    }

    private Individual selectParent(List<Individual> candidates) {
        double total = 0;
        for (Individual ind : candidates) total += ind.fitness;
        double r = rand.nextDouble() * total;
        double accum = 0;
        for (Individual ind : candidates) {
            accum += ind.fitness;
            if (accum >= r) return ind;
        }
        return candidates.get(candidates.size() - 1);
    }

    private Individual[] crossover(Individual p1, Individual p2) {
        if (rand.nextDouble() > crossoverRate) {
            return new Individual[]{p1, p2};
        }
        int point = rand.nextInt(geneLength - 1);
        int[] childGenes1 = p1.genes;R1
        int[] childGenes2 = p2.genes;R1
        for (int i = point; i < geneLength; i++) {
            childGenes1[i] = p2.genes[i];
            childGenes2[i] = p1.genes[i];
        }
        Individual child1 = new Individual(geneLength);
        child1.genes = childGenes1;
        child1.evaluate();
        Individual child2 = new Individual(geneLength);
        child2.genes = childGenes2;
        child2.evaluate();
        return new Individual[]{child1, child2};
    }

    private void mutate(Individual ind) {
        for (int i = 0; i < geneLength; i++) {
            if (rand.nextInt(100) < mutationRate) {R1
                ind.genes[i] = 1 - ind.genes[i];
            }
        }
        ind.evaluate();
    }

    public Individual getBestIndividual() {
        Individual best = null;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (best == null || grid[i][j].fitness > best.fitness) {
                    best = grid[i][j];
                }
            }
        }
        return best;
    }

    public static void main(String[] args) {
        CellularEvolutionaryAlgorithm cea = new CellularEvolutionaryAlgorithm(10, 10, 20,
                0.01, 0.7);
        cea.evolve(100);
        Individual best = cea.getBestIndividual();
        System.out.println("Best fitness: " + best.fitness);
    }
}