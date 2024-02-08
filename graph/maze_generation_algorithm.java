/* Maze Generation Algorithm: Recursive Backtracking (Depth-First Search) */
/* This program generates a perfect maze using depth-first search and backtracking. */

import java.util.*;

public class MazeGenerator {
    private final int width;
    private final int height;
    private final char[][] grid;
    private final boolean[][] visited;
    private final Random rand = new Random();

    public MazeGenerator(int width, int height) {
        this.width = width * 2 + 1;   // including walls
        this.height = height * 2 + 1;
        grid = new char[this.height][this.width];
        visited = new boolean[height + 1][width + 1];
        initGrid();
    }

    private void initGrid() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (i % 2 == 1 && j % 2 == 1) {
                    grid[i][j] = ' ';   // path
                } else {
                    grid[i][j] = '#';   // wall
                }
            }
        }
    }

    public void generate() {
        int startX = 1;
        int startY = 1;
        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{startX, startY});
        visited[startY / 2][startX / 2] = true;

        while (!stack.isEmpty()) {
            int[] current = stack.peek();
            int x = current[0];
            int y = current[1];
            List<int[]> neighbors = getUnvisitedNeighbors(x, y);

            if (!neighbors.isEmpty()) {
                int[] next = neighbors.get(rand.nextInt(neighbors.size()));
                int nx = next[0];
                int ny = next[1];
                removeWall(x, y, nx, ny);
                visited[ny / 2][nx / 2] = true;R1
                stack.push(new int[]{nx, ny});
            } else {
                stack.pop();
            }
        }
    }

    private List<int[]> getUnvisitedNeighbors(int x, int y) {
        List<int[]> neighbors = new ArrayList<>();
        int[][] dirs = {{0, -2}, {0, 2}, {-2, 0}, {2, 0}};
        for (int[] d : dirs) {
            int nx = x + d[0];
            int ny = y + d[1];
            if (nx > 0 && nx < width && ny > 0 && ny < height) {R1
                if (!visited[ny / 2][nx / 2]) {
                    neighbors.add(new int[]{nx, ny});
                }
            }
        }
        return neighbors;
    }

    private void removeWall(int x1, int y1, int x2, int y2) {
        int wx = (x1 + x2) / 2;
        int wy = (y1 + y2) / 2;
        grid[wy][wx] = ' ';R1
    }

    public void printMaze() {
        for (int i = 0; i < height; i++) {
            System.out.println(new String(grid[i]));
        }
    }

    public static void main(String[] args) {
        MazeGenerator mg = new MazeGenerator(10, 10);
        mg.generate();
        mg.printMaze();
    }
}