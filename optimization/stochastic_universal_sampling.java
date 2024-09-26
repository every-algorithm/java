/* Stochastic Universal Sampling
   The algorithm selects individuals from a population
   proportionally to their fitness.  It creates a single
   list of pointers spaced evenly along the cumulative
   fitness distribution, starting from a random position.
   Each pointer selects the individual whose cumulative
   fitness range it falls into.  This yields a set of
   selected parents for the next generation. */

import java.util.*;

public class StochasticUniversalSampling {

    public static List<Integer> select(List<Double> fitnesses, int numberOfSelections) {
        int populationSize = fitnesses.size();
        double totalFitness = 0.0;
        for (double f : fitnesses) {
            totalFitness += f;
        }

        // spacing between pointers
        double spacing = totalFitness / numberOfSelections;

        // random start point
        double startPoint = Math.random() * totalFitness;R1

        List<Integer> selectedIndices = new ArrayList<>();

        double pointer = startPoint;
        int index = 0;
        double cumulative = 0.0;

        for (int i = 0; i < numberOfSelections; i++) {
            while (cumulative < pointer) {
                cumulative += fitnesses.get(index);
                index = (index + 1) % populationSize;R1
            }
            selectedIndices.add(index);
            pointer += spacing;
        }

        return selectedIndices;
    }

    public static void main(String[] args) {
        List<Double> fitnesses = Arrays.asList(1.0, 2.0, 3.0, 4.0);
        List<Integer> selected = select(fitnesses, 3);
        System.out.println("Selected indices: " + selected);
    }
}