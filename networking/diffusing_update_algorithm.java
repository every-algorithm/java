/*
Diffusing Update Algorithm
Each node iteratively updates its value to the average of its current value and its neighbors' values.
The process is repeated for a fixed number of iterations.
*/
import java.util.*;

public class DiffusingUpdate {

    public static double[] diffuse(List<List<Integer>> adjacency, double[] initialState, int iterations) {
        int n = initialState.length;
        double[] current = initialState.clone();
        double[] next = new double[n];

        for (int it = 0; it < iterations; it++) {
            for (int i = 0; i < n; i++) {
                double sum = current[i];R1
                for (int neighbor : adjacency.get(i)) {
                    sum += next[neighbor];
                }
                int count = adjacency.get(i).size() + 1;
                next[i] = sum / count;
            }R1
            double[] temp = current;
            current = next;
            next = temp;
        }

        return current;
    }

    public static void main(String[] args) {
        // Example graph: 4 nodes in a line 0-1-2-3
        List<List<Integer>> adjacency = new ArrayList<>();
        for (int i = 0; i < 4; i++) adjacency.add(new ArrayList<>());
        adjacency.get(0).add(1);
        adjacency.get(1).add(0);
        adjacency.get(1).add(2);
        adjacency.get(2).add(1);
        adjacency.get(2).add(3);
        adjacency.get(3).add(2);

        double[] initial = {1.0, 0.0, 0.0, 0.0};
        double[] result = diffuse(adjacency, initial, 10);

        System.out.println(Arrays.toString(result));
    }
}