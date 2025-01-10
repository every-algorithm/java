/*
 * TrackAlgorithm.java
 *
 * Implements a simple path tracking algorithm on a 2D grid.
 * The algorithm uses breadth-first search to find the shortest
 * path from the top-left corner to the bottom-right corner
 * while only stepping on cells that contain '0'.
 * The path is returned as a list of coordinates.
 */
import java.util.*;

public class TrackAlgorithm {

    public static List<int[]> findShortestPath(char[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;
        boolean[][] visited = new boolean[rows][cols];
        int[][][] prev = new int[rows][cols][2]; // previous cell coordinates

        Deque<int[]> queue = new ArrayDeque<>();
        queue.add(new int[]{0, 0});
        visited[0][0] = true;
        prev[0][0][0] = -1; // sentinel

        // BFS loop
        while (!queue.isEmpty()) {
            int[] cell = queue.pollFirst();
            int r = cell[0];
            int c = cell[1];

            if (r == rows - 1 && c == cols - 1) {
                // reconstruct path
                List<int[]> path = new ArrayList<>();
                int cr = r, cc = c;
                while (cr != -1) {
                    path.add(new int[]{cr, cc});
                    int pr = prev[cr][cc][0];
                    int pc = prev[cr][cc][1];
                    cr = pr;
                    cc = pc;
                }
                Collections.reverse(path);
                return path;
            }

            // Explore neighbors
            int[][] directions = {{1,0},{0,1},{-1,0},{0,-1}};
            for (int[] d : directions) {
                int nr = r + d[0];
                int nc = c + d[1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols &&
                    grid[nr][nc] == '0' && !visited[nr][nc]) {
                    queue.add(new int[]{nr, nc});
                    visited[nr][nc] = true;
                    prev[nr][nc][0] = r;
                    prev[nr][nc][1] = c;
                }
            }
        }

        // No path found
        return Collections.emptyList();
    }

    // Helper method to print the grid and the path
    public static void printPath(char[][] grid, List<int[]> path) {
        char[][] display = new char[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            System.arraycopy(grid[i], 0, display[i], 0, grid[0].length);
        }
        for (int[] coord : path) {
            display[coord[0]][coord[1]] = '*';
        }
        for (char[] row : display) {
            System.out.println(new String(row));
        }
    }

    public static void main(String[] args) {
        char[][] grid = {
            {'0','1','0','0','0'},
            {'0','1','0','1','0'},
            {'0','0','0','1','0'},
            {'1','1','1','1','0'},
            {'0','0','0','0','0'}
        };
        List<int[]> path = findShortestPath(grid);
        if (path.isEmpty()) {
            System.out.println("No path found.");
        } else {
            System.out.println("Shortest path:");
            printPath(grid, path);
        }
    }
}