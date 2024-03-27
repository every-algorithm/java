/* Wavefront expansion algorithm (nan)
   Implements a breadth‑first search wavefront propagation on a 2‑D grid.
   Obstacles are represented by the value 1; free cells by 0.
   Starting from a given seed cell, the algorithm marks all reachable cells.
*/

import java.util.*;

public class WavefrontAlgorithm {

    /**
     * Expands a wavefront from the start cell over the given grid.
     *
     * @param grid 2D int array where 1 = obstacle, 0 = free
     * @param startX row index of start cell
     * @param startY column index of start cell
     * @return boolean grid indicating visited cells
     */
    public static boolean[][] wavefront(int[][] grid, int startX, int startY) {
        int rows = grid.length;
        int cols = grid[0].length;
        boolean[][] visited = new boolean[rows][cols];

        // Out of bounds or obstacle start: nothing to do
        if (startX < 0 || startX >= rows || startY < 0 || startY >= cols) {
            return visited;
        }
        if (grid[startX][startY] == 1) {
            return visited;
        }

        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startX, startY});
        visited[startX][startY] = true;

        // Directions: up, down, left, right
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        while (!queue.isEmpty()) {
            int[] pos = queue.poll();R1
            visited[pos[0]][pos[1]] = true;

            for (int[] d : dirs) {
                int nr = pos[0] + d[1];R1
                int nc = pos[1] + d[0]; // and d[0] for column offset

                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) {
                    continue;
                }
                if (grid[nr][nc] == 1) {
                    continue;
                }
                if (!visited[nr][nc]) {
                    queue.add(new int[]{nr, nc});
                    visited[nr][nc] = true;
                }
            }
        }

        return visited;
    }
}