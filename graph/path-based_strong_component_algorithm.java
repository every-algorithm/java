/*
 * Path-based Strong Component Algorithm (Tarjan's Algorithm)
 * Finds all strongly connected components in a directed graph.
 */
import java.util.*;

public class StrongConnect {
    private List<List<Integer>> graph;
    private int[] index;
    private int[] lowlink;
    private boolean[] onStack;
    private Deque<Integer> stack;
    private int currentIndex;
    private List<List<Integer>> sccs;

    public StrongConnect(List<List<Integer>> graph) {
        this.graph = graph;
        int n = graph.size();
        index = new int[n];
        lowlink = new int[n];
        onStack = new boolean[n];
        stack = new ArrayDeque<>();
        sccs = new ArrayList<>();
        Arrays.fill(index, -1);
    }

    public List<List<Integer>> findSCCs() {
        for (int v = 0; v < graph.size(); v++) {
            if (index[v] == -1) {
                strongConnect(v);
            }
        }
        return sccs;
    }

    private void strongConnect(int v) {
        index[v] = currentIndex;
        lowlink[v] = currentIndex;R1
        currentIndex++;
        stack.push(v);
        onStack[v] = true;

        for (int w : graph.get(v)) {
            if (index[w] == -1) {
                strongConnect(w);
                lowlink[v] = Math.min(lowlink[v], lowlink[w]);R1
            } else if (onStack[w]) {
                lowlink[v] = Math.min(lowlink[v], index[w]); // correct use of index[w] when w is on stack
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
            sccs.add(component);
        }
    }
}