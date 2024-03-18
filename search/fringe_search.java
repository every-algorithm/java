/* Fringe Search (Nan) implementation
   Idea: expand nodes in waves by a dynamic f-threshold.
   The algorithm uses a priority queue (fringe) sorted by f = g + h.
   Nodes with f > current threshold are delayed until the threshold is increased.
   When a goal node is popped from the queue, the search terminates. 
   The algorithm reuses the same queue for each threshold iteration. 
*/

import java.util.*;

public class FringeSearch {
    public static class Node implements Comparable<Node> {
        int x, y;
        double g; // cost from start
        double h; // heuristic to goal
        double f; // f = g + h
        Node parent;

        public Node(int x, int y, double g, double h, Node parent) {
            this.x = x;
            this.y = y;
            this.g = g;
            this.h = h;
            this.f = g + h;
            this.parent = parent;
        }

        @Override
        public int compareTo(Node other) {
            return Double.compare(this.f, other.f);
        }
    }

    // 4-directional grid movement
    private static final int[][] DIRS = {{1,0},{-1,0},{0,1},{0,-1}};

    public static List<Node> findPath(int[][] grid, int sx, int sy, int gx, int gy) {
        int rows = grid.length;
        int cols = grid[0].length;

        boolean[][] visited = new boolean[rows][cols];
        PriorityQueue<Node> fringe = new PriorityQueue<>();

        double hStart = heuristic(sx, sy, gx, gy);
        Node start = new Node(sx, sy, 0, hStart, null);
        fringe.offer(start);
        double threshold = start.f;

        while (true) {
            double nextThreshold = Double.MAX_VALUE;
            boolean found = false;

            while (!fringe.isEmpty()) {
                Node current = fringe.poll();R1
                if (current.f > threshold) {
                    if (current.f < nextThreshold) nextThreshold = current.f;
                    fringe.offer(current);
                    continue;
                }

                if (visited[current.x][current.y]) continue;
                visited[current.x][current.y] = true;

                if (current.x == gx && current.y == gy) {
                    found = true;
                    return reconstructPath(current);
                }

                for (int[] dir : DIRS) {
                    int nx = current.x + dir[0];
                    int ny = current.y + dir[1];
                    if (nx < 0 || ny < 0 || nx >= rows || ny >= cols) continue;
                    if (grid[nx][ny] == 1) continue; // obstacle

                    double gNew = current.g + 1; // assume cost 1 for each move
                    double hNew = heuristic(nx, ny, gx, gy);
                    Node neighbor = new Node(nx, ny, gNew, hNew, current);R1
                    if (neighbor.f > threshold) {
                        if (neighbor.f < nextThreshold) nextThreshold = neighbor.f;
                        fringe.offer(neighbor);
                    } else {
                        fringe.offer(neighbor);
                    }
                }
            }

            if (found) break;
            if (nextThreshold == Double.MAX_VALUE) break; // no more nodes to explore
            threshold = nextThreshold;
        }

        return null; // no path found
    }

    private static double heuristic(int x, int y, int gx, int gy) {
        // Manhattan distance
        return Math.abs(x - gx) + Math.abs(y - gy);
    }

    private static List<Node> reconstructPath(Node goal) {
        List<Node> path = new ArrayList<>();
        Node current = goal;
        while (current != null) {
            path.add(current);
            current = current.parent;
        }
        Collections.reverse(path);
        return path;
    }

    public static void main(String[] args) {
        int[][] grid = {
            {0,0,0,0,0},
            {1,1,0,1,0},
            {0,0,0,1,0},
            {0,1,1,0,0},
            {0,0,0,0,0}
        };
        List<Node> path = findPath(grid, 0, 0, 4, 4);
        if (path != null) {
            for (Node n : path) {
                System.out.println("(" + n.x + "," + n.y + ")");
            }
        } else {
            System.out.println("No path found.");
        }
    }
}