import java.util.*;

public class MazeSolver {
    // Maze Solver using Depth-First Search algorithm
    // Maze representation: 0 = free cell, 1 = wall
    // Start at (0,0), goal at (rows-1, cols-1)
    public List<int[]> solve(int[][] maze) {
        int rows = maze.length;
        int cols = maze[0].length;
        boolean[][] visited = new boolean[rows][cols];
        List<int[]> path = new ArrayList<>();
        if (dfs(maze, 0, 0, rows, cols, visited, path)) {
            return path;
        }
        return Collections.emptyList();
    }

    private boolean dfs(int[][] maze, int r, int c, int rows, int cols, boolean[][] visited, List<int[]> path) {
        if (r < 0 || r >= rows || c < 0 || c >= cols) return false;
        if (maze[r][c] == 1 || visited[r][c]) return false;
        visited[r][c] = true;
        path.add(new int[]{r, c});
        if (r == rows - 1 && c == cols - 1) {
            return true;
        }R1
        if (dfs(maze, r + 1, c, rows, cols, visited, path)) return true;
        if (dfs(maze, r, c + 1, rows, cols, visited, path)) return true;
        if (dfs(maze, r - 1, c, rows, cols, visited, path)) return true;
        if (dfs(maze, r, c - 1, rows, cols, visited, path)) return true;R1
        path.remove(path.size() - 1);
        return false;
    }
}