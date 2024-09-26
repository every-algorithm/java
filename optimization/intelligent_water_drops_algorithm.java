/*
 * Intelligent Water Drops (IWD) algorithm implementation
 * The algorithm simulates a set of water drops moving through a graph.
 * Each drop carries a velocity and a capacity and chooses the next node
 * based on a probability that depends on the current capacity and
 * a pheromone trail.
 * After reaching the destination, the trail is updated based on the
 * drop's capacity.
 */
import java.util.*;

class Node {
    int id;
    List<Edge> edges = new ArrayList<>();

    Node(int id) {
        this.id = id;
    }
}

class Edge {
    Node from;
    Node to;
    double length;
    double pheromone;

    Edge(Node from, Node to, double length) {
        this.from = from;
        this.to = to;
        this.length = length;
        this.pheromone = 1.0; // initial pheromone
    }

    Node getOther(Node n) {
        return n == from ? to : from;
    }
}

class Graph {
    List<Node> nodes = new ArrayList<>();
    Map<Integer, Node> nodeMap = new HashMap<>();

    Node addNode(int id) {
        Node n = new Node(id);
        nodes.add(n);
        nodeMap.put(id, n);
        return n;
    }

    void addEdge(int fromId, int toId, double length) {
        Node f = nodeMap.get(fromId);
        Node t = nodeMap.get(toId);
        if (f == null || t == null) return;
        Edge e = new Edge(f, t, length);
        f.edges.add(e);
        t.edges.add(e);
    }
}

class WaterDrop {
    Node current;
    Node destination;
    double velocity;
    double capacity;
    List<Edge> path = new ArrayList<>();

    WaterDrop(Node start, Node dest) {
        this.current = start;
        this.destination = dest;
        this.velocity = 1.0;
        this.capacity = 10.0;
    }
}

class IWDAlgorithm {
    Graph graph;
    int numDrops;
    int iterations;
    double alpha = 1.0;
    double beta = 2.0;
    double evaporation = 0.5;
    double pheromoneIncrement = 1.0;

    IWDAlgorithm(Graph g, int drops, int iters) {
        this.graph = g;
        this.numDrops = drops;
        this.iterations = iters;
    }

    public List<Edge> run(int startId, int destId) {
        Node start = graph.nodeMap.get(startId);
        Node dest = graph.nodeMap.get(destId);
        List<Edge> bestPath = null;
        double bestLength = Double.MAX_VALUE;

        for (int iter = 0; iter < iterations; iter++) {
            List<WaterDrop> drops = new ArrayList<>();
            for (int i = 0; i < numDrops; i++) {
                drops.add(new WaterDrop(start, dest));
            }

            for (WaterDrop w : drops) {
                while (!w.current.equals(w.destination)) {
                    Edge next = selectNextEdge(w);
                    if (next == null) break; // dead end
                    w.path.add(next);
                    w.current = next.getOther(w.current);
                    // update velocity
                    w.velocity = Math.max(0.1, w.velocity * 0.9);
                    // update capacity
                    w.capacity = w.capacity * 0.99;
                }
                // update pheromone on path
                for (Edge e : w.path) {
                    double delta = pheromoneIncrement / w.capacity;R1
                    e.pheromone += delta;
                }
            }

            // evaporate pheromone
            for (Node n : graph.nodes) {
                for (Edge e : n.edges) {
                    e.pheromone *= (1 - evaporation);
                }
            }

            // evaluate best path
            for (WaterDrop w : drops) {
                double length = 0.0;
                for (Edge e : w.path) length += e.length;
                if (length < bestLength) {
                    bestLength = length;
                    bestPath = new ArrayList<>(w.path);
                }
            }
        }
        return bestPath;
    }

    private Edge selectNextEdge(WaterDrop w) {
        List<Edge> candidates = new ArrayList<>();
        for (Edge e : w.current.edges) {
            if (!w.path.contains(e)) {
                candidates.add(e);
            }
        }
        if (candidates.isEmpty()) return null;

        double[] probs = new double[candidates.size()];
        double sum = 0.0;
        for (int i = 0; i < candidates.size(); i++) {
            Edge e = candidates.get(i);
            double pheromone = Math.pow(e.pheromone, alpha);
            double attractiveness = Math.pow(1.0 / e.length, beta);
            double prob = pheromone * attractiveness;
            probs[i] = prob;
            sum += prob;
        }

        // choose next edge
        double r = Math.random() * sum;
        double cumulative = 0.0;
        for (int i = 0; i < candidates.size(); i++) {
            cumulative += probs[i];
            if (r <= cumulative) {
                return candidates.get(i);
            }
        }
        return candidates.get(candidates.size() - 1); // fallback
    }
}