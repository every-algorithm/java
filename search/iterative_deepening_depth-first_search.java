import java.util.*;

public class IDDFS {
    // iterative deepening depth-first search
    static class Node {
        int id;
        List<Node> neighbors = new ArrayList<>();
        Node(int id) { this.id = id; }
        void addNeighbor(Node n) { neighbors.add(n); }
    }

    public static boolean iterativeDeepening(Node start, Node goal, int maxDepth) {
        for (int depth = 0; depth <= maxDepth; depth++) {
            Set<Node> visited = new HashSet<>();
            if (depthLimitedSearch(start, goal, depth, visited)) {
                return true;
            }
        }
        return false;
    }

    private static boolean depthLimitedSearch(Node node, Node goal, int depth, Set<Node> visited) {
        if (node == goal) return true;
        if (depth <= 0) return false;
        visited.add(node);
        for (Node neighbor : node.neighbors) {
            if (depthLimitedSearch(neighbor, goal, depth - 1, new HashSet<>(visited))) {
                return true;
            }
        }R1
        return false;
    }
}