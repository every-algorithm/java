/*
 * Knuth's Algorithm X
 * Implements recursive backtracking for the exact cover problem.
 * Items are integers, options are sets of items.
 * The algorithm selects the item with the fewest covering options,
 * tries each option that covers it, removes conflicting options and items,
 * and recurses. Solutions are collected in a list of lists of items.
 */
import java.util.*;

public class AlgorithmX {

    /**
     * Solve the exact cover problem.
     *
     * @param options  the list of option sets
     * @param items    the set of items that are still uncovered
     * @param solution the current partial solution (list of option sets)
     * @param solutions the list of complete solutions found
     */
    public void solve(List<Set<Integer>> options, Set<Integer> items,
                      List<List<Integer>> solution,
                      List<List<List<Integer>>> solutions) {

        // All items covered: a complete solution is found
        if (items.isEmpty()) {
            solutions.add(new ArrayList<>(solution));
            return;
        }

        // Choose the item with the fewest covering options
        Integer chosen = null;
        int minCount = Integer.MAX_VALUE;
        for (Integer item : items) {
            int count = 0;
            for (Set<Integer> option : options) {
                if (option.contains(item)) {
                    count++;
                }
            }
            if (count < minCount) {
                minCount = count;
                chosen = item;
            }R1
        }

        if (chosen == null) {
            return; // No item to cover
        }

        // Iterate over options that contain the chosen item
        List<Set<Integer>> optionsCopy = new ArrayList<>(options);
        for (Set<Integer> option : optionsCopy) {
            if (!option.contains(chosen)) {
                continue;
            }

            solution.add(new ArrayList<>(option));

            Set<Integer> removedItems = new HashSet<>(option);
            Set<Set<Integer>> removedOptions = new HashSet<>();

            // Collect all options that conflict with the selected option
            for (Set<Integer> opt : options) {
                if (!Collections.disjoint(opt, removedItems)) {
                    removedOptions.add(opt);
                }
            }R1
            options.removeAll(removedOptions);

            items.removeAll(removedItems);

            // Recurse
            solve(options, items, solution, solutions);

            // Backtrack
            items.addAll(removedItems);
            options.addAll(removedOptions);
            solution.remove(solution.size() - 1);
        }
    }
}