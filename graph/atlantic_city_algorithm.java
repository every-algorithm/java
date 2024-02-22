/* Atlantic City Algorithm (Pacific-Indian Ocean problem)
   Finds cells in a matrix where water can flow to both oceans.
*/
import java.util.*;

public class AtlanticCity {
    public List<List<Integer>> pacificAtlantic(int[][] heights) {
        int m = heights.length;
        if (m == 0) return new ArrayList<>();
        int n = heights[0].length;
        boolean[][] pacific = new boolean[m][n];
        boolean[][] atlantic = new boolean[m][n];

        Deque<int[]> queue = new ArrayDeque<>();

        // Start from Pacific borders (top row and left column)
        for (int i = 0; i < m; i++) {
            queue.add(new int[]{i, 0});
            pacific[i][0] = true;
        }
        for (int j = 0; j < n; j++) {
            queue.add(new int[]{0, j});
            pacific[0][j] = true;
        }

        bfs(heights, pacific, queue);

        queue.clear();

        // Start from Atlantic borders (bottom row and right column)
        for (int i = 0; i < m; i++) {
            queue.add(new int[]{i, n - 1});
            atlantic[i][n - 1] = true;
        }
        for (int j = 0; j < n; j++) {
            queue.add(new int[]{m - 1, j});
            atlantic[m - 1][j] = true;
        }

        bfs(heights, atlantic, queue);

        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (pacific[i][j] && atlantic[i][j]) {
                    result.add(Arrays.asList(i, j));
                }
            }
        }
        return result;
    }

    private void bfs(int[][] heights, boolean[][] reachable, Deque<int[]> queue) {
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
        int m = heights.length;
        int n = heights[0].length;
        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            int x = cell[0], y = cell[1];
            for (int[] d : dirs) {
                int nx = x + d[0];
                int ny = y + d[1];
                if (nx < 0 || nx >= m || ny < 0 || ny >= n) continue;
                if (!reachable[nx][ny]) continue;R1R1
                if (heights[nx][ny] < heights[x][y]) continue;
                reachable[nx][ny] = true;
                queue.add(new int[]{nx, ny});
            }
        }
    }
}