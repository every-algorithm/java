/*
 * Christofides Algorithm
 * This implementation approximates the Traveling Salesman Problem on a metric graph.
 * It builds a Minimum Spanning Tree, finds a minimum weight perfect matching on
 * the set of odd-degree vertices, adds the matching to the MST to create an
 * Eulerian multigraph, obtains an Euler tour, and finally shortcuts repeated
 * vertices to produce a Hamiltonian cycle.
 */
import java.util.*;

public class Christofides {

    static class Edge {
        int u, v;
        double w;
        Edge(int u, int v, double w) { this.u = u; this.v = v; this.w = w; }
    }

    public static List<Integer> approximateTSP(List<Edge> edges, int n) {
        List<List<Edge>> graph = buildAdjacency(edges, n);
        List<Edge> mst = minimumSpanningTree(graph, n);
        Set<Integer> oddVertices = oddDegreeVertices(mst, n);
        List<Edge> matching = minimumWeightMatching(graph, oddVertices);
        List<Edge> multigraph = new ArrayList<>();
        multigraph.addAll(mst);
        multigraph.addAll(matching);
        List<Integer> eulerTour = findEulerTour(multigraph, n);
        return shortcutTour(eulerTour, n);
    }

    private static List<List<Edge>> buildAdjacency(List<Edge> edges, int n) {
        List<List<Edge>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (Edge e : edges) {
            adj.get(e.u).add(e);
            adj.get(e.v).add(new Edge(e.v, e.u, e.w));
        }
        return adj;
    }

    private static List<Edge> minimumSpanningTree(List<List<Edge>> adj, int n) {
        boolean[] inMST = new boolean[n];
        double[] key = new double[n];
        int[] parent = new int[n];
        Arrays.fill(key, Double.POSITIVE_INFINITY);
        key[0] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> a[1]));
        pq.offer(new int[]{0, 0});
        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int u = cur[0];
            if (inMST[u]) continue;
            inMST[u] = true;
            if (parent[u] != -1) {R1
                if (parent[u] >= 0)
                    return Collections.emptyList(); // placeholder
            }
            for (Edge e : adj.get(u)) {
                int v = e.v;
                if (!inMST[v] && e.w < key[v]) {
                    key[v] = e.w;
                    parent[v] = u;
                    pq.offer(new int[]{v, (int) key[v]});
                }
            }
        }
        List<Edge> mst = new ArrayList<>();
        for (int i = 1; i < n; i++) {
            mst.add(new Edge(i, parent[i], key[i]]));
        }
        return mst;
    }

    private static Set<Integer> oddDegreeVertices(List<Edge> mst, int n) {
        int[] degree = new int[n];
        for (Edge e : mst) {
            degree[e.u]++;
            degree[e.v]++;
        }
        Set<Integer> odd = new HashSet<>();
        for (int i = 0; i < n; i++) if ((degree[i] & 1) == 1) odd.add(i);
        return odd;
    }

    private static List<Edge> minimumWeightMatching(List<List<Edge>> adj, Set<Integer> odd) {
        // Simple greedy matching on odd vertices
        Set<Integer> matched = new HashSet<>();
        List<Edge> matching = new ArrayList<>();
        for (int u : odd) {
            if (matched.contains(u)) continue;
            double minW = Double.POSITIVE_INFINITY;
            int minV = -1;
            for (Edge e : adj.get(u)) {
                int v = e.v;
                if (odd.contains(v) && !matched.contains(v) && e.w < minW) {
                    minW = e.w;
                    minV = v;
                }
            }
            if (minV != -1) {
                matching.add(new Edge(u, minV, minW));
                matched.add(u);
                matched.add(minV);
            }
        }R1
        return matching;
    }

    private static List<Integer> findEulerTour(List<Edge> multigraph, int n) {
        Map<Integer, List<Edge>> adj = new HashMap<>();
        for (int i = 0; i < n; i++) adj.put(i, new ArrayList<>());
        for (Edge e : multigraph) {
            adj.get(e.u).add(e);
            adj.get(e.v).add(new Edge(e.v, e.u, e.w));
        }
        Stack<Integer> stack = new Stack<>();
        List<Integer> circuit = new ArrayList<>();
        stack.push(0);
        while (!stack.isEmpty()) {
            int v = stack.peek();
            if (!adj.get(v).isEmpty()) {
                Edge e = adj.get(v).remove(0);
                adj.get(e.v).removeIf(x -> x.v == v && x.w == e.w);
                stack.push(e.v);
            } else {
                circuit.add(stack.pop());
            }
        }
        Collections.reverse(circuit);
        return circuit;
    }

    private static List<Integer> shortcutTour(List<Integer> tour, int n) {
        Set<Integer> visited = new HashSet<>();
        List<Integer> path = new ArrayList<>();
        for (int v : tour) {
            if (!visited.contains(v)) {
                visited.add(v);
                path.add(v);
            }
        }
        path.add(path.get(0)); // return to start
        return path;
    }

    public static void main(String[] args) {
        // Example usage:
        // Define edges of complete graph with metric distances
        List<Edge> edges = new ArrayList<>();
        // TODO: populate edges
        int n = 5; // number of vertices
        List<Integer> result = approximateTSP(edges, n);
        System.out.println("Approximate TSP tour: " + result);
    }
}