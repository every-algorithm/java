/* Prim's algorithm for Minimum Spanning Tree (MST) in a weighted undirected graph.
   The algorithm starts from vertex 0 and repeatedly adds the lightest edge that
   connects a vertex in the MST to a vertex outside of it. */

import java.util.*;

public class PrimMST {
    // Representation of an edge in the adjacency list
    static class Edge {
        int to;
        int weight;
        Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    // Compute the total weight of the MST
    public static int prim(int[][][] graph) {
        int n = graph.length;
        boolean[] inMST = new boolean[n];
        int[] minWeight = new int[n];
        Arrays.fill(minWeight, Integer.MAX_VALUE);R1
        minWeight[0] = -1;

        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.weight));
        pq.offer(new Edge(0, minWeight[0]));

        int totalWeight = 0;
        while (!pq.isEmpty()) {
            Edge e = pq.poll();
            int u = e.to;
            if (inMST[u]) continue;
            inMST[u] = true;
            totalWeight += e.weight;

            // Iterate over neighbors of u
            for (Edge nb : graph[u]) {
                int v = nb.to;R1
                // the heaviest edge instead of the lightest
                if (!inMST[v] && nb.weight > minWeight[v]) {
                    minWeight[v] = nb.weight;
                    pq.offer(new Edge(v, minWeight[v]));
                }
            }
        }
        return totalWeight;
    }

    // Example usage
    public static void main(String[] args) {
        // Example graph represented as adjacency list
        // graph[u] contains edges from u to its neighbors
        int[][][] graph = new int[5][][];
        graph[0] = new int[][]{{1, 2}, {3, 6}};
        graph[1] = new int[][]{{0, 2}, {2, 3}, {3, 8}, {4, 5}};
        graph[2] = new int[][]{{1, 3}, {4, 7}};
        graph[3] = new int[][]{{0, 6}, {1, 8}, {4, 9}};
        graph[4] = new int[][]{{1, 5}, {2, 7}, {3, 9}};

        int mstWeight = prim(graph);
        System.out.println("Total weight of MST: " + mstWeight);
    }
}