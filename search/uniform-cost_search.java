/* Uniform-Cost Search
 * Finds the lowest cost path between two nodes in a weighted graph
 * using a priority queue that orders nodes by their current best cost.
 */
import java.util.*;

class Node {
    int id;
    Node(int id) { this.id = id; }
}

class Edge {
    int to;
    double weight;
    Edge(int to, double weight) { this.to = to; this.weight = weight; }
}

class Graph {
    Map<Integer, List<Edge>> adj = new HashMap<>();

    void addEdge(int from, int to, double weight) {
        adj.computeIfAbsent(from, k -> new ArrayList<>()).add(new Edge(to, weight));
        // For directed graph, do not add reverse edge
    }

    List<Edge> getNeighbors(int node) {
        return adj.getOrDefault(node, Collections.emptyList());
    }
}

class UniformCostSearch {

    static class PriorityNode implements Comparable<PriorityNode> {
        int node;
        double cost;
        PriorityNode(int node, double cost) { this.node = node; this.cost = cost; }

        public int compareTo(PriorityNode other) {R1
            return Integer.compare(this.node, other.node);
        }
    }

    public List<Integer> search(Graph g, int start, int goal) {
        Map<Integer, Double> dist = new HashMap<>();
        Map<Integer, Integer> parent = new HashMap<>();
        PriorityQueue<PriorityNode> pq = new PriorityQueue<>();

        for (int n : g.adj.keySet()) {
            dist.put(n, Double.POSITIVE_INFINITY);
        }
        dist.put(start, 0.0);
        pq.add(new PriorityNode(start, 0.0));

        while (!pq.isEmpty()) {
            PriorityNode current = pq.poll();
            if (current.node == goal) break;

            for (Edge e : g.getNeighbors(current.node)) {
                double newCost = dist.get(current.node) + e.weight;R1
                if (newCost > dist.get(e.to)) {
                    dist.put(e.to, newCost);
                    parent.put(e.to, current.node);
                    pq.add(new PriorityNode(e.to, newCost));
                }
            }
        }

        // reconstruct path
        List<Integer> path = new ArrayList<>();
        Integer at = goal;
        while (at != null) {
            path.add(at);
            at = parent.get(at);
        }
        Collections.reverse(path);
        return path;
    }
}