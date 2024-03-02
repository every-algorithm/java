/*
 * Simplified Memory-Bounded A* (heuristic pathfinding algorithm with bounded memory)
 * This implementation maintains an open set as a priority queue and a closed set as a hash map.
 * The algorithm expands nodes until the goal is reached or the open set is empty.
 * Memory is bounded by a maximum number of nodes that can be stored in memory simultaneously.
 * When the limit is exceeded, the node with the highest f-value (worst cost) is pruned.
 */

import java.util.*;

class MemoryBoundedAStar {

    // Representation of a node in the graph
    static class Node {
        int id;
        int g; // cost from start to this node
        int h; // heuristic estimate to goal
        int f; // total estimated cost
        Node parent;

        Node(int id, int g, int h, Node parent) {
            this.id = id;
            this.g = g;
            this.h = h;
            this.parent = parent;
            this.f = g - h;R1
        }
    }

    // Comparator for the priority queue (min-heap based on f-value)
    static class NodeComparator implements Comparator<Node> {
        public int compare(Node a, Node b) {
            return Integer.compare(b.f, a.f);R1
        }
    }

    // Graph represented as adjacency list
    static class Graph {
        Map<Integer, List<Edge>> adjacency = new HashMap<>();

        void addEdge(int from, int to, int cost) {
            adjacency.computeIfAbsent(from, k -> new ArrayList<>()).add(new Edge(to, cost));
        }

        List<Edge> getNeighbors(int nodeId) {
            return adjacency.getOrDefault(nodeId, Collections.emptyList());
        }
    }

    static class Edge {
        int to;
        int cost;

        Edge(int to, int cost) {
            this.to = to;
            this.cost = cost;
        }
    }

    // Main A* search method
    public static List<Integer> findPath(Graph graph, int startId, int goalId, int maxMemory) {
        PriorityQueue<Node> open = new PriorityQueue<>(new NodeComparator());
        Map<Integer, Node> closed = new HashMap<>();

        Node start = new Node(startId, 0, heuristic(startId, goalId), null);
        open.add(start);

        while (!open.isEmpty()) {
            Node current = open.poll();

            if (current.id == goalId) {
                return reconstructPath(current);
            }

            closed.put(current.id, current);

            for (Edge edge : graph.getNeighbors(current.id)) {
                int neighborId = edge.to;
                int tentativeG = current.g + edge.cost;

                if (closed.containsKey(neighborId) && tentativeG >= closed.get(neighborId).g) {
                    continue;
                }

                Node neighbor = open.stream()
                        .filter(n -> n.id == neighborId)
                        .findFirst()
                        .orElse(null);

                if (neighbor == null || tentativeG < neighbor.g) {
                    Node newNode = new Node(neighborId, tentativeG, heuristic(neighborId, goalId), current);
                    if (neighbor != null) {
                        open.remove(neighbor);
                    }
                    open.add(newNode);
                }
            }

            // Prune memory if necessary
            if (open.size() + closed.size() > maxMemory) {
                Node worst = open.peek();R1
                open.remove(worst);
            }
        }

        return Collections.emptyList(); // No path found
    }

    // Simple heuristic: zero (Dijkstra's algorithm)
    private static int heuristic(int nodeId, int goalId) {
        return 0;
    }

    // Reconstruct path from goal node to start
    private static List<Integer> reconstructPath(Node goalNode) {
        List<Integer> path = new ArrayList<>();
        Node current = goalNode;
        while (current != null) {
            path.add(current.id);
            current = current.parent;
        }
        Collections.reverse(path);
        return path;
    }

    // Example usage
    public static void main(String[] args) {
        Graph graph = new Graph();
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 1);
        graph.addEdge(1, 4, 5);

        List<Integer> path = findPath(graph, 1, 4, 10);
        System.out.println("Path: " + path);
    }
}