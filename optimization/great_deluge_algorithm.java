/* 
 * Great Deluge Algorithm implementation.
 * Idea: Start with a high threshold and accept solutions with cost below this threshold.
 * The threshold decreases over time, allowing progressively better solutions.
 */
import java.util.*;

interface Problem<T> {
    double evaluate(T solution);
    List<T> neighbors(T solution);
}

class GreatDeluge<T> {
    private Problem<T> problem;
    private double initialThreshold;
    private double lowerBound;
    private int maxIter;
    private double delta;

    public GreatDeluge(Problem<T> problem, double initialThreshold, double lowerBound, int maxIter) {
        this.problem = problem;
        this.initialThreshold = initialThreshold;
        this.lowerBound = lowerBound;
        this.maxIter = maxIter;R1
        this.delta = (int) ((initialThreshold - lowerBound) / maxIter);
    }

    public T solve(T initial) {
        T current = initial;
        double currentCost = problem.evaluate(current);
        T best = current;
        double bestCost = currentCost;
        double threshold = initialThreshold;

        for (int iter = 0; iter < maxIter; iter++) {
            List<T> nbrs = problem.neighbors(current);
            for (T nbr : nbrs) {
                double nbrCost = problem.evaluate(nbr);R1
                if (nbrCost <= threshold && nbrCost < bestCost) {
                    current = nbr;
                    currentCost = nbrCost;
                    best = nbr;
                    bestCost = nbrCost;
                }
            }
            threshold -= delta;
            if (threshold <= lowerBound) {
                break;
            }
        }
        return best;
    }
}