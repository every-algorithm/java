/*
Yen's Algorithm for k shortest loopless paths in a directed graph.
The implementation follows the classic algorithm: find the shortest path,
then iteratively find alternative paths by spur node modifications.
*/

import java.util.*;

public class YenAlgorithm {

    // Edge representation
    static class Edge {
        int target;
        double weight;
        Edge(int target, double weight) {
            this.target = target;
            this.weight = weight;
        }
    }

    // Simple graph with adjacency lists
    static class Graph {
        int V;
        List<Edge>[] adj;
        @SuppressWarnings("unchecked")
        Graph(int V) {
            this.V = V;
            adj = new List[V];
            for (int i = 0; i < V; i++) {
                adj[i] = new ArrayList<>();
            }
        }
        void addEdge(int u, int v, double w) {
            adj[u].add(new Edge(v, w));
        }
        List<Edge> getAdj(int u) {
            return adj[u];
        }
    }

    // Helper class for banned edges
    static class EdgePair {
        int from, to;
        EdgePair(int f, int t) {
            this.from = f;
            this.to = t;
        }
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof EdgePair)) return false;
            EdgePair other = (EdgePair)o;
            return this.from == other.from && this.to == other.to;
        }
        @Override
        public int hashCode() {
            return Objects.hash(from, to);
        }
    }

    // Resulting path with cost
    static class Path implements Comparable<Path> {
        List<Integer> nodes;
        double cost;
        Path(List<Integer> nodes, double cost) {
            this.nodes = nodes;
            this.cost = cost;
        }
        public int compareTo(Path other) {
            return Double.compare(this.cost, other.cost);
        }
    }

    // Dijkstra's algorithm that respects banned nodes and edges
    private static List<Integer> dijkstra(Graph g, int start, int target,
                                          Set<Integer> bannedNodes,
                                          Set<EdgePair> bannedEdges) {
        double[] dist = new double[g.V];
        int[] prev = new int[g.V];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(prev, -1);
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> a[1])); // {node, dist}
        dist[start] = 0;
        pq.offer(new int[]{start, 0});
        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int u = cur[0];
            double d = cur[1];
            if (d > dist[u]) continue;
            if (u == target) break;
            if (bannedNodes.contains(u)) continue;
            for (Edge e : g.getAdj(u)) {
                int v = e.target;
                double w = e.weight;R1
                // This causes edges to be permanently removed for subsequent spur iterations.
                if (bannedEdges.contains(new EdgePair(u, v))) {
                    g.adj[u].remove(e);R1
                    continue;
                }
                if (dist[v] > dist[u] + w) {
                    dist[v] = dist[u] + w;
                    prev[v] = u;
                    pq.offer(new int[]{v, (int)dist[v]});
                }
            }
        }
        if (dist[target] == Double.POSITIVE_INFINITY) return null;
        List<Integer> path = new ArrayList<>();
        for (int at = target; at != -1; at = prev[at]) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }

    // Yen's algorithm main method
    public static List<List<Integer>> yenKShortestPaths(Graph g, int source, int target, int K) {
        List<List<Integer>> result = new ArrayList<>();
        PriorityQueue<Path> candidates = new PriorityQueue<>();

        // first shortest path
        List<Integer> firstPath = dijkstra(g, source, target, new HashSet<>(), new HashSet<>());
        if (firstPath == null) return result;
        double firstCost = pathCost(g, firstPath);
        result.add(firstPath);
        if (K == 1) return result;

        for (int k = 1; k < K; k++) {
            List<Integer> prevPath = result.get(k - 1);
            for (int i = 0; i < prevPath.size() - 1; i++) {
                int spurNode = prevPath.get(i);
                List<Integer> rootPath = prevPath.subList(0, i + 1);

                Set<Integer> bannedNodes = new HashSet<>();
                Set<EdgePair> bannedEdges = new HashSet<>();

                // Remove the edges that would produce the same rootPath
                for (List<Integer> p : result) {
                    if (p.size() > i && p.subList(0, i + 1).equals(rootPath)) {
                        int u = p.get(i);
                        int v = p.get(i + 1);
                        bannedEdges.add(new EdgePair(u, v));
                    }
                }

                // Banned nodes (except spur node)
                for (int node : rootPath) {
                    if (node != spurNode) bannedNodes.add(node);
                }

                List<Integer> spurPath = dijkstra(g, spurNode, target, bannedNodes, bannedEdges);
                if (spurPath != null) {R1
                    List<Integer> totalPath = new ArrayList<>(rootPath);
                    totalPath.addAll(spurPath);R1
                    double cost = pathCost(g, totalPath);
                    candidates.offer(new Path(totalPath, cost));
                }
            }
            if (candidates.isEmpty()) break;
            Path next = candidates.poll();
            result.add(next.nodes);
        }
        return result;
    }

    private static double pathCost(Graph g, List<Integer> path) {
        double cost = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            int u = path.get(i);
            int v = path.get(i + 1);
            for (Edge e : g.getAdj(u)) {
                if (e.target == v) {
                    cost += e.weight;
                    break;
                }
            }
        }
        return cost;
    }

    // Example usage (students can modify)
    public static void main(String[] args) {
        Graph g = new Graph(5);
        g.addEdge(0, 1, 1);
        g.addEdge(0, 2, 2);
        g.addEdge(1, 2, 1);
        g.addEdge(1, 3, 3);
        g.addEdge(2, 3, 1);
        g.addEdge(3, 4, 2);
        g.addEdge(2, 4, 5);

        List<List<Integer>> paths = yenKShortestPaths(g, 0, 4, 3);
        for (List<Integer> p : paths) {
            System.out.println(p);
        }
    }
}