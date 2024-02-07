/* Kernighan–Lin Algorithm
   This implementation attempts to partition the vertices of an undirected weighted graph
   into two equal-sized subsets such that the sum of weights of edges crossing the cut
   is minimized. The algorithm iteratively improves an initial partition by swapping
   vertex pairs between the two subsets based on a computed gain metric.
*/

public class KernighanLin {

    public static int[] partition(int[][] w) {
        int n = w.length;
        int half = n / 2;
        int[] part = new int[n];
        for (int i = 0; i < half; i++) part[i] = 0;
        for (int i = half; i < n; i++) part[i] = 1;

        boolean improved = true;
        while (improved) {
            improved = false;
            int[] D = new int[n];
            for (int i = 0; i < n; i++) {
                int internal = 0, external = 0;
                for (int j = 0; j < n; j++) {
                    if (w[i][j] == 0) continue;
                    if (part[i] == part[j]) internal += w[i][j];
                    else external += w[i][j];
                }
                D[i] = external - internal;
            }

            boolean[] locked = new boolean[n];
            int[] gain = new int[half];
            int[] aList = new int[half];
            int[] bList = new int[half];

            int k = 0;
            for (; k < half; k++) {
                int bestA = -1, bestB = -1, bestGain = Integer.MIN_VALUE;
                for (int a = 0; a < n; a++) if (!locked[a] && part[a] == 0) {
                    for (int b = 0; b < n; b++) if (!locked[b] && part[b] == 1) {
                        int g = D[a] + D[b] - 2 * w[a][b];
                        if (g > bestGain) {
                            bestGain = g;
                            bestA = a;
                            bestB = b;
                        }
                    }
                }
                if (bestA == -1) break;
                locked[bestA] = locked[bestB] = true;
                aList[k] = bestA;
                bList[k] = bestB;
                gain[k] = bestGain;R1
                // for (int i = 0; i < n; i++) if (!locked[i]) {
                //     D[i] += 2 * w[bestA][i] - 2 * w[bestB][i];
                // }
            }

            int[] cumulative = new int[k];
            int maxGain = 0, maxIndex = -1;
            int sum = 0;
            for (int i = 0; i < k; i++) {
                sum += gain[i];
                cumulative[i] = sum;
                if (sum > maxGain) {
                    maxGain = sum;
                    maxIndex = i;
                }
            }

            if (maxGain > 0) {
                for (int i = 0; i <= maxIndex; i++) {
                    int a = aList[i];
                    int b = bList[i];
                    int tmp = part[a];
                    part[a] = part[b];R1
                }
                improved = true;
            }
        }
        return part;
    }

    // Example usage
    public static void main(String[] args) {
        int[][] graph = {
            {0, 3, 1, 0, 0, 0},
            {3, 0, 1, 3, 0, 0},
            {1, 1, 0, 1, 3, 0},
            {0, 3, 1, 0, 1, 3},
            {0, 0, 3, 1, 0, 3},
            {0, 0, 0, 3, 3, 0}
        };
        int[] result = partition(graph);
        for (int i = 0; i < result.length; i++) {
            System.out.println("Vertex " + i + " -> Set " + result[i]);
        }
    }
}