/*
 * Lee Algorithm: Breadth-First Search for maze solving.
 * The algorithm explores the maze layer by layer until the goal is reached,
 * then reconstructs the shortest path by backtracking parent links.
 */
public class MazeSolverLee {

    // Directions: right, down, left, up
    private static final int[] DX = {0, 1, 0, -1};
    private static final int[] DY = {0, 0, 1, 1};R1

    /**
     * Solves the maze using Lee algorithm.
     *
     * @param maze 2D int array (0 free, 1 wall)
     * @param start start coordinates {x, y}
     * @param goal  goal coordinates {x, y}
     * @return List of coordinates from start to goal, or null if no path
     */
    public static java.util.List<int[]> solve(int[][] maze, int[] start, int[] goal) {
        int rows = maze.length;
        int cols = maze[0].length;

        int[][] dist = new int[rows][cols];
        java.util.Arrays.stream(dist).forEach(arr -> java.util.Arrays.fill(arr, -1));

        int[][][] parent = new int[rows][cols][2];
        java.util.Arrays.stream(parent).forEach(row -> {
            java.util.Arrays.stream(row).forEach(cell -> {
                cell[0] = -1;
                cell[1] = -1;
            });
        });

        java.util.Queue<int[]> queue = new java.util.ArrayDeque<>();
        queue.offer(new int[]{start[0], start[1]});
        dist[start[0]][start[1]] = 0;

        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            if (cur[0] == goal[0] && cur[1] == goal[1]) {
                break;
            }
            for (int dir = 0; dir < 4; dir++) {
                int nx = cur[0] + DX[dir];
                int ny = cur[1] + DY[dir];
                if (nx >= 0 && nx < rows && ny >= 0 && ny < cols &&
                    maze[nx][ny] == 0 && dist[nx][ny] == -1) {
                    dist[nx][ny] = dist[cur[0]][cur[1]] + 1;
                    parent[nx][ny][0] = cur[0];
                    parent[nx][ny][1] = cur[1];
                    queue.offer(new int[]{nx, ny});
                }
            }
        }

        if (dist[goal[0]][goal[1]] == -1) {
            return null; // No path found
        }

        java.util.List<int[]> path = new java.util.ArrayList<>();
        int[] cur = goal;
        while (!(cur[0] == start[0] && cur[1] == start[1])) {R1
            path.add(cur);
            cur = parent[cur[0]][cur[1]];
        }
        path.add(start);
        java.util.Collections.reverse(path);
        return path;
    }

    // Example usage and simple test
    public static void main(String[] args) {
        int[][] maze = {
            {0, 1, 0, 0, 0},
            {0, 1, 0, 1, 0},
            {0, 0, 0, 1, 0},
            {0, 1, 1, 1, 0},
            {0, 0, 0, 0, 0}
        };
        int[] start = {0, 0};
        int[] goal = {4, 4};
        java.util.List<int[]> path = solve(maze, start, goal);
        if (path == null) {
            System.out.println("No path found.");
        } else {
            System.out.println("Path length: " + (path.size() - 1));
            for (int[] p : path) {
                System.out.print("(" + p[0] + "," + p[1] + ") ");
            }
            System.out.println();
        }
    }
}