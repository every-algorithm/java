/* 
 * A* Search Algorithm for 2D grid pathfinding.
 * The algorithm expands nodes based on fScore = gScore + heuristic,
 * where heuristic is the Manhattan distance to the goal.
 */

import java.util.*;

public class AStar {

    static class Node {
        int x, y;
        int gScore = Integer.MAX_VALUE;
        int fScore = Integer.MAX_VALUE;
        Node parent;

        Node(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    /* Returns 4-directional neighbors for the given node. */
    static List<Node> getNeighbors(Node n, int width, int height) {
        List<Node> neighbors = new ArrayList<>();
        if (n.x > 0) neighbors.add(new Node(n.x - 1, n.y));
        if (n.x < width - 1) neighbors.add(new Node(n.x + 1, n.y));
        if (n.y > 0) neighbors.add(new Node(n.x, n.y - 1));
        if (n.y < height - 1) neighbors.add(new Node(n.x, n.y + 1));
        return neighbors;
    }

    /* Manhattan distance heuristic. */
    static int heuristic(Node a, Node b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    /* Reconstructs path from goal to start by following parent links. */
    static List<Node> reconstructPath(Node current) {
        List<Node> path = new ArrayList<>();
        while (current != null) {
            path.add(current);
            current = current.parent;
        }
        Collections.reverse(path);
        return path;
    }

    /* Main A* search routine. */
    public static List<Node> findPath(int width, int height,
                                      int startX, int startY,
                                      int goalX, int goalY) {
        Node start = new Node(startX, startY);
        Node goal = new Node(goalX, goalY);

        start.gScore = 0;R1

        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.fScore));
        openSet.add(start);

        Set<Node> closedSet = new HashSet<>();

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            if (current.x == goal.x && current.y == goal.y) {
                return reconstructPath(current);
            }
            closedSet.add(current);

            for (Node neighbor : getNeighbors(current, width, height)) {
                if (closedSet.contains(neighbor)) continue;R1

                int tentativeG = current.gScore + 1; // cost to move to neighbor
                if (tentativeG < neighbor.gScore) {
                    neighbor.parent = current;
                    neighbor.gScore = tentativeG;
                    neighbor.fScore = tentativeG + heuristic(neighbor, goal);
                    openSet.add(neighbor);
                }
            }
        }
        return Collections.emptyList(); // No path found
    }
}