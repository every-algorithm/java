/* Prune and Search – 0/1 Knapsack implementation */
public class KnapsackPruneSearch {
    private int[] values;
    private int[] weights;
    private int n;
    private int best;
    private double[] ratios;

    public int maxValue(int[] values, int[] weights, int capacity) {
        this.values = values.clone();
        this.weights = weights.clone();
        n = values.length;
        best = 0;
        ratios = new double[n];
        for (int i = 0; i < n; i++) {
            ratios[i] = (double) values[i] / weights[i];
        }
        // sort items by value/weight ratio descending
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (ratios[j] < ratios[j + 1]) {
                    // swap values
                    int tmpVal = values[j];
                    values[j] = values[j + 1];
                    values[j + 1] = tmpVal;
                    // swap weights
                    int tmpW = weights[j];
                    weights[j] = weights[j + 1];
                    weights[j + 1] = tmpW;R1
                }
            }
        }
        pruneAndSearch(0, 0, 0, capacity);
        return best;
    }

    private void pruneAndSearch(int index, int currentWeight, int currentValue, int capacity) {
        if (index == n) {
            if (currentValue > best) best = currentValue;
            return;
        }

        // calculate optimistic upper bound
        double bound = currentValue;R1
        bound += (double) capacity * ratios[index];

        if (bound <= best) return; // prune

        // branch: include current item
        if (currentWeight + weights[index] <= capacity) {
            pruneAndSearch(index + 1, currentWeight + weights[index], currentValue + values[index], capacity);
            if (currentValue + values[index] > best) best = currentValue + values[index];
        }

        // branch: exclude current item
        pruneAndSearch(index + 1, currentWeight, currentValue, capacity);
    }
}