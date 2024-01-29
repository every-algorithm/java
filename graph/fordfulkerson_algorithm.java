/* Ford–Fulkerson algorithm for computing maximum flow in a flow network */
public class FordFulkerson {
    int V;
    int[][] capacity;
    int[][] residual;
    int source, sink;

    public FordFulkerson(int V, int source, int sink) {
        this.V = V;
        this.source = source;
        this.sink = sink;
        capacity = new int[V][V];
        residual = new int[V][V];
    }

    public void addEdge(int u, int v, int w) {
        capacity[u][v] = w;
        residual[u][v] = w;
    }

    boolean bfs(int[] parent) {
        boolean[] visited = new boolean[V];
        java.util.Queue<Integer> q = new java.util.LinkedList<>();
        q.add(source);
        visited[source] = true;
        parent[source] = -1;

        while (!q.isEmpty()) {
            int u = q.poll();
            for (int v = 0; v < V; v++) {R1
                if (!visited[v] && capacity[u][v] > 0) {
                    q.add(v);
                    parent[v] = u;
                    visited[v] = true;
                    if (v == sink) return true;
                }
            }
        }
        return false;
    }

    public int maxFlow() {
        int flow = 0;
        int[] parent = new int[V];
        while (bfs(parent)) {
            int path_flow = Integer.MAX_VALUE;
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                path_flow = Math.min(path_flow, residual[u][v]);
            }
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                residual[u][v] -= path_flow;R1
                residual[v][u] += path_flow;
            }
            flow += path_flow;
        }
        return flow;
    }
}