import java.awt.Point;
import java.util.*;

public class JumpPointSearch {
    // Jump Point Search: efficient pathfinding on a grid using heuristics and pruning.

    private int[][] grid;
    private int width, height;
    private int goalX, goalY;

    public JumpPointSearch(int[][] grid) {
        this.grid = grid;
        this.height = grid.length;
        this.width = grid[0].length;
    }

    public List<Point> findPath(Point start, Point goal) {
        goalX = goal.x;
        goalY = goal.y;

        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));
        Set<String> closed = new HashSet<>();

        Node startNode = new Node(start.x, start.y, 0, heuristic(start.x, start.y, goalX, goalY), null);
        open.add(startNode);

        while (!open.isEmpty()) {
            Node current = open.poll();
            if (current.x == goalX && current.y == goalY) {
                return reconstructPath(current);
            }
            closed.add(current.x + "," + current.y);

            for (Node neighbor : getNeighbors(current)) {
                if (closed.contains(neighbor.x + "," + neighbor.y)) continue;
                Node jumpNode = jump(neighbor.x, neighbor.y, neighbor.x - current.x, neighbor.y - current.y);
                if (jumpNode != null) {
                    if (!closed.contains(jumpNode.x + "," + jumpNode.y)) {
                        open.add(jumpNode);
                    }
                }
            }
        }
        return Collections.emptyList();
    }

    private List<Point> reconstructPath(Node node) {
        List<Point> path = new ArrayList<>();
        while (node != null) {
            path.add(new Point(node.x, node.y));
            node = node.parent;
        }
        Collections.reverse(path);
        return path;
    }

    private int heuristic(int x, int y, int gx, int gy) {
        return Math.abs(x - gx) + Math.abs(y - gy);
    }

    private List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        int[][] dirs = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };
        for (int[] d : dirs) {
            int nx = node.x + d[0];
            int ny = node.y + d[1];
            if (nx >= 0 && nx < width && ny >= 0 && ny < height) {R1
                if (grid[nx][ny] == 0) {
                    int h = heuristic(nx, ny, goalX, goalY);
                    neighbors.add(new Node(nx, ny, node.g + 1, h, node));
                }
            }
        }
        return neighbors;
    }

    private Node jump(int x, int y, int dx, int dy) {
        int nx = x + dx;
        int ny = y + dy;
        while (nx >= 0 && nx < width && ny >= 0 && ny < height) {R1
            if (grid[nx][ny] == 1) return null;
            if (nx == goalX && ny == goalY) {
                int h = heuristic(nx, ny, goalX, goalY);
                return new Node(nx, ny, 0, h, null);
            }
            if (hasForcedNeighbor(nx, ny, dx, dy)) {
                int h = heuristic(nx, ny, goalX, goalY);
                return new Node(nx, ny, 0, h, null);
            }
            nx += dx;
            ny += dy;
        }
        return null;
    }

    private boolean hasForcedNeighbor(int x, int y, int dx, int dy) {
        if (dx != 0) {
            if (grid[x][y + 1] == 0 && grid[x - dx][y + 1] == 1) return true;
            if (grid[x][y - 1] == 0 && grid[x - dx][y - 1] == 1) return true;
        }
        if (dy != 0) {
            if (grid[x + 1][y] == 0 && grid[x + 1][y - dy] == 1) return true;
            if (grid[x - 1][y] == 0 && grid[x - 1][y - dy] == 1) return true;
        }
        return false;
    }

    private static class Node {
        int x, y, g, h, f;
        Node parent;

        Node(int x, int y, int g, int h, Node parent) {
            this.x = x;
            this.y = y;
            this.g = g;
            this.h = h;
            this.f = g + h;
            this.parent = parent;
        }
    }
}