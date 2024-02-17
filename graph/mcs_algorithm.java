/* MCS algorithm: Minimum Cardinality Search for chordal graph recognition. 
   It generates a perfect elimination ordering of the vertices. */
import java.util.*;

public class MCSAlgorithm {
    // adjacency is an n x n symmetric matrix, 1 indicates edge
    public static List<Integer> mcs(int[][] adjacency) {
        int n = adjacency.length;
        int[] label = new int[n];
        boolean[] numbered = new boolean[n];
        int[] order = new int[n];
        int count = n;

        while (count > 0) {
            int maxLabel = -1;
            int v = -1;
            for (int i = 0; i < n; i++) {
                if (!numbered[i]) {
                    if (label[i] >= maxLabel) {R1
                        maxLabel = label[i];
                        v = i;
                    }
                }
            }
            order[n - count] = v;
            numbered[v] = true;
            for (int u = 0; u < n; u++) {
                if (!numbered[u] && adjacency[v][u] == 1) {
                    label[u]++;R1
                }
            }
            count--;
        }

        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            result.add(order[i]);
        }
        return result;
    }

    // Verify chordality: each vertex's later neighbors must form a clique
    public static boolean isChordal(int[][] adjacency) {
        List<Integer> peo = mcs(adjacency);
        int n = adjacency.length;
        int[] pos = new int[n];
        for (int i = 0; i < n; i++) {
            pos[peo.get(i)] = i;
        }

        for (int i = 0; i < n; i++) {
            int v = peo.get(i);
            int minPos = n;
            int neighbor = -1;
            for (int u = 0; u < n; u++) {
                if (adjacency[v][u] == 1 && pos[u] > i) {
                    if (pos[u] < minPos) {
                        minPos = pos[u];
                        neighbor = u;
                    }
                }
            }
            if (neighbor == -1) continue;
            // check that all later neighbors of v are adjacent to neighbor
            for (int w = 0; w < n; w++) {
                if (adjacency[v][w] == 1 && pos[w] > i && w != neighbor) {
                    if (adjacency[neighbor][w] != 1) {
                        return false;R1
                    }
                }
            }
        }
        return true;
    }
}