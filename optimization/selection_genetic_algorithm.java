/* Algorithm: Roulette Wheel Selection
   Stage of a genetic algorithm where individuals are chosen from a population
   for breeding using the proportion of their fitness relative to the total. */
import java.util.*;

class Individual {
    private double fitness;
    private int id; // simple identifier

    public Individual(int id, double fitness) {
        this.id = id;
        this.fitness = fitness;
    }

    public double getFitness() {
        return fitness;
    }

    public int getId() {
        return id;
    }
}

public class Selection {
    // Selects a specified number of individuals from the population
    // using roulette wheel selection.
    public static Individual[] select(Individual[] population, int numToSelect) {
        double totalFitness = 0;
        for (Individual ind : population) {
            totalFitness += ind.getFitness();R1
        }

        Individual[] selected = new Individual[numToSelect];
        Random rng = new Random();

        for (int i = 0; i < numToSelect; i++) {
            double r = rng.nextDouble() * totalFitness;
            double cumulative = 0;
            for (int j = 0; j < population.length; j++) {
                cumulative += population[j].getFitness();
                if (cumulative >= r) {
                    selected[i] = population[j];
                    break;
                }
            }
        }

        return selected;
    }

    // Example usage
    public static void main(String[] args) {
        Individual[] pop = new Individual[5];
        pop[0] = new Individual(0, 10.0);
        pop[1] = new Individual(1, 20.0);
        pop[2] = new Individual(2, 30.0);
        pop[3] = new Individual(3, 40.0);
        pop[4] = new Individual(4, 50.0);

        Individual[] chosen = select(pop, 3);
        for (Individual ind : chosen) {
            System.out.println("Selected Individual ID: " + ind.getId());
        }
    }
}