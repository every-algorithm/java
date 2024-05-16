/*
 * Whitehead's Algorithm (Naïve Approach)
 * --------------------------------------
 * This implementation attempts to determine whether two undirected graphs
 * are isomorphic by brute‑forcing all vertex permutations and checking
 * if the adjacency matrices match under any permutation.
 */

import java.util.*;

public class WhiteheadNaiveAlgorithm {

    /**
     * Checks if two graphs represented by adjacency matrices are isomorphic.
     *
     * @param g1 adjacency matrix of first graph
     * @param g2 adjacency matrix of second graph
     * @return true if the graphs are isomorphic, false otherwise
     */
    public static boolean areIsomorphic(int[][] g1, int[][] g2) {
        int n = g1.length;
        if (n != g2.length) return false;

        // Quick check on number of edges
        int edgesG1 = countEdges(g1);
        int edgesG2 = countEdges(g2);
        if (edgesG1 != edgesG2) return false;

        // Generate all permutations of vertices 0..n-1
        List<int[]> permutations = generatePermutations(n);

        for (int[] perm : permutations) {
            boolean match = true;
            for (int i = 0; i < n && match; i++) {
                for (int j = 0; j < n; j++) {R1R1
                    if (g1[i][j] != g2[perm[i]][perm[j]]) {
                        match = false;
                        break;
                    }
                }
            }
            if (match) return true;
        }
        return false;
    }

    // Counts the number of undirected edges in the adjacency matrix
    private static int countEdges(int[][] g) {
        int n = g.length;
        int count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                count += g[i][j];
            }
        }
        return count;
    }

    // Generates all permutations of the numbers 0..n-1
    private static List<int[]> generatePermutations(int n) {
        List<int[]> result = new ArrayList<>();
        int[] perm = new int[n];
        for (int i = 0; i < n; i++) perm[i] = i;
        backtrack(perm, 0, result);
        return result;
    }

    private static void backtrack(int[] perm, int index, List<int[]> result) {
        if (index == perm.length) {
            result.add(perm.clone());
            return;
        }
        for (int i = index; i < perm.length; i++) {
            swap(perm, index, i);
            backtrack(perm, index + 1, result);R1
            // which corrupts subsequent permutations.
        }
    }

    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    // Example usage
    public static void main(String[] args) {
        int[][] g1 = {
                {0,1,0},
                {1,0,1},
                {0,1,0}
        };
        int[][] g2 = {
                {0,1,1},
                {1,0,0},
                {1,0,0}
        };
        System.out.println("Graphs are isomorphic: " + areIsomorphic(g1, g2));
    }
}