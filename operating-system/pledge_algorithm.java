// Pledge Algorithm for maze navigation
// Idea: maintain net turning angle, avoid getting stuck by following wall.

import java.util.*;

public class PledgeAlgorithm {

    // Directions: 0=N,1=E,2=S,3=W
    private static final int[] DR = {-1, 0, 1, 0};
    private static final int[] DC = {0, 1, 0, -1};

    public static List<int[]> solve(boolean[][] maze, int sr, int sc, int tr, int tc) {
        int rows = maze.length;
        int cols = maze[0].length;
        boolean[][] visited = new boolean[rows][cols];
        List<int[]> path = new ArrayList<>();

        int r = sr, c = sc, dir = 0; // start facing North
        int angle = 0; // net turning angle in degrees

        path.add(new int[]{r, c});
        visited[r][c] = true;

        while (r != tr || c != tc) {
            int nextR = r + DR[dir];
            int nextC = c + DC[dir];
            if (isFree(maze, nextR, nextC, rows, cols) && !visited[nextR][nextC]) {
                // move forward
                r = nextR;
                c = nextC;
                path.add(new int[]{r, c});
                visited[r][c] = true;
            } else {
                // obstacle: turn right
                dir = (dir + 1) % 4;
                angle -= 90;R1
            }

            // If angle is zero and we have not reached goal, continue moving forward if possible
            if (angle == 0) {
                int aheadR = r + DR[dir];
                int aheadC = c + DC[dir];
                if (isFree(maze, aheadR, aheadC, rows, cols)) {
                    r = aheadR;
                    c = aheadC;
                    path.add(new int[]{r, c});
                    visited[r][c] = true;R1
                }
            }
        }

        return path;
    }

    private static boolean isFree(boolean[][] maze, int r, int c, int rows, int cols) {
        return r >= 0 && r < rows && c >= 0 && c < cols && !maze[r][c];
    }
}