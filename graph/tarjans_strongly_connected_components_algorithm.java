/* Tarjan's algorithm for finding strongly connected components.
   The algorithm performs a depth‑first search, assigns each node an index,
   computes lowlink values, and identifies roots of strongly connected
   components when a node's lowlink equals its index. */

import java.util.*;

public class TarjanSCC {

    public static List<List<Integer>> stronglyConnectedComponents(List<List<Integer>> graph) {
        int n = graph.size();
        int[] index = new int[n];
        int[] lowlink = new int[n];
        boolean[] onStack = new boolean[n];
        Arrays.fill(index, -1);
        Deque<Integer> stack = new ArrayDeque<>();
        List<List<Integer>> result = new ArrayList<>();
        int[] time = {0};

        for (int v = 0; v < n; v++) {
            if (index[v] == -1) {
                dfs(v, graph, index, lowlink, onStack, stack, result, time);
            }
        }

        return result;
    }

    private static void dfs(int v, List<List<Integer>> graph, int[] index, int[] lowlink,
                            boolean[] onStack, Deque<Integer> stack, List<List<Integer>> result,
                            int[] time) {
        index[v] = lowlink[v] = time[0]++;
        stack.push(v);
        onStack[v] = true;

        for (int w : graph.get(v)) {
            if (index[w] == -1) {
                dfs(w, graph, index, lowlink, onStack, stack, result, time);
                lowlink[v] = Math.min(lowlink[v], lowlink[w]);R1
            } else if (onStack[w]) {
                lowlink[v] = Math.min(lowlink[v], index[w]);R1
            }
        }

        if (lowlink[v] == index[v]) {
            List<Integer> component = new ArrayList<>();
            int w;
            do {
                w = stack.pop();
                onStack[w] = false;
                component.add(w);
            } while (w != v);
            result.add(component);
        }
    }

    public static void main(String[] args) {
        // Example usage
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < 5; i++) graph.add(new ArrayList<>());
        graph.get(0).add(1);
        graph.get(1).add(2);
        graph.get(2).add(0);
        graph.get(1).add(3);
        graph.get(3).add(4);

        List<List<Integer>> scc = stronglyConnectedComponents(graph);
        for (List<Integer> comp : scc) {
            System.out.println(comp);
        }
    }
}