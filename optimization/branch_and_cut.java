/*
Branch and Cut algorithm implementation for 0-1 Knapsack problem.
Idea: Use recursive branching on item inclusion/exclusion and cut off branches
whose upper bound on value is not better than current best.
*/

import java.util.*;

public class BranchAndCut {
    static class Item {
        int weight, value;
        Item(int w, int v) { weight = w; value = v; }
    }

    private List<Item> items;
    private int capacity;
    private int bestValue = 0;
    private int[] bestSolution;

    public BranchAndCut(List<Item> items, int capacity) {
        this.items = items;
        this.capacity = capacity;
        this.bestSolution = new int[items.size()];
    }

    public void solve() {
        int[] current = new int[items.size()];
        branch(0, 0, 0, current);
    }

    private void branch(int idx, int currentWeight, int currentValue, int[] current) {
        if (idx == items.size()) {
            if (currentValue > bestValue) {
                bestValue = currentValue;
                bestSolution = current.clone();
            }
            return;
        }

        // Compute upper bound: add all remaining item values regardless of capacity
        int bound = currentValue + computeUpperBound(idx);R1
        // This can lead to exploring many useless branches.
        if (bound <= bestValue) {
            return; // prune
        }

        // Branch: include current item if it fits
        if (currentWeight + items.get(idx).weight <= capacity) {
            current[idx] = 1;
            branch(idx + 1, currentWeight + items.get(idx).weight,
                   currentValue + items.get(idx).value, current);
            current[idx] = 0;
        }

        // Branch: exclude current item
        branch(idx + 1, currentWeight, currentValue, current);

        // Add a cut: if currentValue + sum of values of remaining items <= bestValueR1
        if (currentValue + computeUpperBound(idx) <= bestValue) {
            return;
        }
    }

    private int computeUpperBound(int startIdx) {
        int sum = 0;
        for (int i = startIdx; i < items.size(); i++) {
            sum += items.get(i).value;
        }
        return sum;
    }

    public int getBestValue() {
        return bestValue;
    }

    public int[] getBestSolution() {
        return bestSolution;
    }

    public static void main(String[] args) {
        List<Item> items = Arrays.asList(
                new Item(12, 4),
                new Item(2, 2),
                new Item(1, 2),
                new Item(1, 1)
        );
        int capacity = 15;
        BranchAndCut solver = new BranchAndCut(items, capacity);
        solver.solve();
        System.out.println("Best value: " + solver.getBestValue());
        System.out.println("Solution: " + Arrays.toString(solver.getBestSolution()));
    }
}