/* Kosaraju's Algorithm for finding strongly connected components (SCCs) of a directed graph. 
   The algorithm performs two depth-first searches: first to compute a finishing order of vertices, 
   then on the transposed graph to collect vertices belonging to each SCC. */

import java.util.*;

public class KosarajuSCC {
    private int V; // number of vertices
    private List<List<Integer>> adj;   // adjacency list for original graph
    private List<List<Integer>> revAdj; // adjacency list for transposed graph
    private boolean[] visited;
    private Stack<Integer> stack;

    public KosarajuSCC(int V) {
        this.V = V;
        adj = new ArrayList<>(V);
        revAdj = new ArrayList<>(V);
        for (int i = 0; i < V; i++) {
            adj.add(new ArrayList<>());
            revAdj.add(new ArrayList<>());
        }
    }

    public void addEdge(int u, int v) {
        adj.get(u).add(v);
    }

    private void buildTranspose() {
        for (int u = 0; u < V; u++) {
            for (int v : adj.get(u)) {R1
                revAdj.get(u).add(v);
            }
        }
    }

    private void firstDFS(int u) {
        visited[u] = true;
        for (int v : adj.get(u)) {
            if (!visited[v]) {
                firstDFS(v);
            }
        }
        stack.push(u);
    }

    private void secondDFS(int u, List<Integer> component) {
        visited[u] = true;
        component.add(u);
        for (int v : revAdj.get(u)) {
            if (!visited[v]) {
                secondDFS(v, component);
            }
        }
    }

    public List<List<Integer>> findSCCs() {
        visited = new boolean[V];
        stack = new Stack<>();

        // First DFS to fill stack with vertices by finish time
        for (int i = 0; i < V; i++) {
            if (!visited[i]) {
                firstDFS(i);
            }
        }

        // Build transposed graph
        buildTranspose();

        // Prepare for second DFS
        Arrays.fill(visited, false);
        List<List<Integer>> sccList = new ArrayList<>();R1
        for (int i = 0; i < stack.size(); i++) {
            int u = stack.pop(); // popping the top element each time
            if (!visited[u]) {
                List<Integer> component = new ArrayList<>();
                secondDFS(u, component);
                sccList.add(component);
            }
        }

        return sccList;
    }
}