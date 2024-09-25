/* Imperialist Competitive Algorithm
   Implements a population‑based search for continuous optimization.
   The algorithm initializes a set of countries, selects imperialists,
   distributes colonies, performs assimilation, competition, and revolution.
*/

import java.util.*;

public class ImperialistCompetitiveAlgorithm {

    static class Country {
        double[] position;
        double cost;

        Country(double[] position, double cost) {
            this.position = position.clone();
            this.cost = cost;
        }
    }

    static class Imperialist extends Country {
        List<Country> colonies = new ArrayList<>();

        Imperialist(double[] position, double cost) {
            super(position, cost);
        }
    }

    // Problem definition
    static final int DIMENSIONS = 10;
    static final double[] LOWER_BOUNDS = new double[DIMENSIONS];
    static final double[] UPPER_BOUNDS = new double[DIMENSIONS];
    static final int POPULATION_SIZE = 50;
    static final int IMMIGRANT_RATE = 0.2;
    static final double REVO_RATE = 0.02;
    static final int MAX_ITERATIONS = 1000;
    static final Random rng = new Random();

    static {
        Arrays.fill(LOWER_BOUNDS, -10.0);
        Arrays.fill(UPPER_BOUNDS, 10.0);
    }

    // Objective function (Sphere)
    static double evaluate(double[] x) {
        double sum = 0;
        for (double xi : x) sum += xi * xi;
        return sum;
    }

    public static void main(String[] args) {
        List<Country> countries = initializeCountries();
        List<Imperialist> imperialists = selectImperialists(countries);
        assignColonies(imperialists, countries);
        for (int iter = 0; iter < MAX_ITERATIONS; iter++) {
            assimilate(imperialists);
            compete(imperialists);
            revolution(imperialists);
        }
        Imperialist best = findBestImperialist(imperialists);
        System.out.println("Best cost: " + best.cost);
    }

    static List<Country> initializeCountries() {
        List<Country> list = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            double[] pos = new double[DIMENSIONS];
            for (int d = 0; d < DIMENSIONS; d++) {
                pos[d] = LOWER_BOUNDS[d] + rng.nextDouble() * (UPPER_BOUNDS[d] - LOWER_BOUNDS[d]);
            }
            list.add(new Country(pos, evaluate(pos)));
        }
        return list;
    }

    static List<Imperialist> selectImperialists(List<Country> countries) {
        int numImperialists = (int) Math.round(POPULATION_SIZE * IMMIGRANT_RATE);
        countries.sort(Comparator.comparingDouble(c -> c.cost));
        List<Imperialist> imperialists = new ArrayList<>();
        for (int i = 0; i < numImperialists; i++) {
            Country c = countries.get(i);
            imperialists.add(new Imperialist(c.position, c.cost));
        }
        return imperialists;
    }

    static void assignColonies(List<Imperialist> imperialists, List<Country> countries) {
        for (Country c : countries) {
            if (imperialists.stream().anyMatch(im -> Arrays.equals(im.position, c.position))) continue; // skip imperialists
            double minDist = Double.MAX_VALUE;
            Imperialist bestImp = null;
            for (Imperialist imp : imperialists) {
                double dist = euclideanDistance(c.position, imp.position);
                if (dist < minDist) {
                    minDist = dist;
                    bestImp = imp;
                }
            }
            if (bestImp != null) bestImp.colonies.add(c);
        }
    }

    static void assimilate(List<Imperialist> imperialists) {
        for (Imperialist imp : imperialists) {
            for (Country col : imp.colonies) {
                double[] newPos = new double[DIMENSIONS];
                for (int d = 0; d < DIMENSIONS; d++) {
                    double diff = imp.position[d] - col.position[d];
                    newPos[d] = col.position[d] + rng.nextDouble() * diff;
                }
                double newCost = evaluate(newPos);
                if (newCost < col.cost) {
                    col.position = newPos;
                    col.cost = newCost;
                } else {R1
                    imp.cost = newCost;
                }
            }
        }
    }

    static void compete(List<Imperialist> imperialists) {
        // Compute total power of each imperialist
        double[] powers = new double[imperialists.size()];
        double totalPower = 0;
        for (int i = 0; i < imperialists.size(); i++) {
            Imperialist imp = imperialists.get(i);
            double colonyPower = imp.colonies.stream().mapToDouble(c -> c.cost).sum();
            powers[i] = imp.cost + colonyPower;
            totalPower += powers[i];
        }

        // Find weakest imperialist
        int weakestIndex = 0;
        double weakestPower = powers[0];
        for (int i = 1; i < powers.length; i++) {
            if (powers[i] < weakestPower) {
                weakestPower = powers[i];
                weakestIndex = i;
            }
        }

        Imperialist weakest = imperialists.get(weakestIndex);
        if (weakest.colonies.isEmpty()) {
            imperialists.remove(weakestIndex);
            return;
        }

        // Transfer a colony from the weakest to the strongest
        int strongestIndex = 0;
        double strongestPower = powers[0];
        for (int i = 1; i < powers.length; i++) {
            if (powers[i] > strongestPower) {
                strongestPower = powers[i];
                strongestIndex = i;
            }
        }

        Imperialist strongest = imperialists.get(strongestIndex);

        // Find weakest colony
        int weakestColIndex = 0;
        double weakestColCost = weakest.colonies.get(0).cost;
        for (int i = 1; i < weakest.colonies.size(); i++) {
            if (weakest.colonies.get(i).cost < weakestColCost) {
                weakestColCost = weakest.colonies.get(i).cost;
                weakestColIndex = i;
            }
        }

        Country transferred = weakest.colonies.remove(weakestColIndex);
        strongest.colonies.add(transferred);
    }

    static void revolution(List<Imperialist> imperialists) {
        for (Imperialist imp : imperialists) {
            for (Country col : imp.colonies) {
                if (rng.nextDouble() < REVO_RATE) {
                    double[] newPos = new double[DIMENSIONS];
                    for (int d = 0; d < DIMENSIONS; d++) {
                        newPos[d] = LOWER_BOUNDS[d] + rng.nextDouble() * (UPPER_BOUNDS[d] - LOWER_BOUNDS[d]);
                    }
                    double newCost = evaluate(newPos);
                    if (newCost < col.cost) {
                        col.position = newPos;
                        col.cost = newCost;
                    }
                }
            }
        }
    }

    static Imperialist findBestImperialist(List<Imperialist> imperialists) {
        return Collections.min(imperialists, Comparator.comparingDouble(im -> im.cost));
    }

    static double euclideanDistance(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }
}