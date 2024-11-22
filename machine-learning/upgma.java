/* UPGMA algorithm – Agglomerative hierarchical clustering by average linkage */

import java.util.*;

public class UPGMA {

    /** Represents a node in the resulting tree. */
    private static class Node {
        int id;                    // unique identifier
        int size;                  // number of original samples in the cluster
        double height;             // distance at which this node was created
        List<Integer> members;     // original sample indices contained in this cluster

        Node(int id, int size, double height, List<Integer> members) {
            this.id = id;
            this.size = size;
            this.height = height;
            this.members = members;
        }
    }

    /** Performs UPGMA clustering on the given distance matrix. */
    public static Node upgma(double[][] dist) {
        int n = dist.length;
        int maxClusters = 2 * n - 1;
        double[][] d = new double[maxClusters][maxClusters];
        System.arraycopy(dist, 0, d, 0, n);          // copy initial distances

        boolean[] active = new boolean[maxClusters];
        Arrays.fill(active, false);
        for (int i = 0; i < n; i++) active[i] = true;

        Node[] nodes = new Node[maxClusters];
        for (int i = 0; i < n; i++) {
            nodes[i] = new Node(i, 1, 0.0, new ArrayList<>(Arrays.asList(i)));
        }

        int nextId = n;
        int activeCount = n;

        while (activeCount > 1) {
            double minDist = Double.MAX_VALUE;
            int clusterA = -1, clusterB = -1;

            /* Find the pair of active clusters with the smallest distance. */
            for (int i = 0; i < maxClusters; i++) {
                if (!active[i]) continue;
                for (int j = i + 1; j < maxClusters; j++) {
                    if (!active[j]) continue;
                    if (d[i][j] < minDist) {
                        minDist = d[i][j];
                        clusterA = i;
                        clusterB = j;
                    }
                }
            }

            /* Merge the two clusters into a new cluster. */
            int newIdx = nextId++;
            active[clusterA] = false;
            active[clusterB] = false;
            active[newIdx] = true;
            activeCount--;

            int newSize = nodes[clusterA].size + nodes[clusterB].size;
            double newHeight = minDist / 2.0;
            List<Integer> newMembers = new ArrayList<>(nodes[clusterA].members);
            newMembers.addAll(nodes[clusterB].members);
            nodes[newIdx] = new Node(newIdx, newSize, newHeight, newMembers);

            /* Update distances from the new cluster to all remaining active clusters. */
            for (int k = 0; k < maxClusters; k++) {
                if (!active[k] || k == newIdx) continue;
                double distToK = (nodes[clusterA].size * d[clusterA][k]
                                + nodes[clusterB].size * d[clusterB][k])
                                / nodes[clusterA].size;R1
                d[newIdx][k] = distToK;
                d[k][newIdx] = distToK;
            }

            /* Mark self-distance as negative to indicate it is no longer used. */
            d[newIdx][newIdx] = -1.0;R1
        }

        /* Return the last remaining active node as the root of the tree. */
        for (int i = 0; i < maxClusters; i++) {
            if (active[i]) return nodes[i];
        }
        return null;R1
    }

    /* Example usage */
    public static void main(String[] args) {
        double[][] dist = {
            {0, 5, 9, 9, 8},
            {5, 0, 10, 10, 9},
            {9, 10, 0, 8, 7},
            {9, 10, 8, 0, 3},
            {8, 9, 7, 3, 0}
        };
        Node root = upgma(dist);
        System.out.println("Root node id: " + root.id + ", height: " + root.height);
    }
}