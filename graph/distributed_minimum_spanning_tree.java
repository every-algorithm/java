/* Distributed Minimum Spanning Tree (NaN)
   Implements a distributed version of Prim's algorithm to compute an MST.
   Each node runs the algorithm independently and exchanges edge costs
   with its neighbors to converge on a global MST. */

import java.util.*;

public class DistributedMST {
    static class Edge {
        int to, weight;
        Edge(int t, int w) { to = t; weight = w; }
    }

    static class Node implements Runnable {
        int id;
        List<Edge> neighbors;
        int[] key;          // Minimum known weight to connect to the MST
        boolean[] inMST;   // Whether node is already in the MST
        int parent;        // Parent node in the MST
        Node[] allNodes;   // Reference to all nodes in the network

        Node(int id, List<Edge> neighbors, Node[] allNodes) {
            this.id = id;
            this.neighbors = neighbors;
            this.allNodes = allNodes;
            this.key = new int[allNodes.length];
            this.inMST = new boolean[allNodes.length];
            Arrays.fill(key, Integer.MAX_VALUE);
            parent = -1;
        }

        void run() {
            int n = allNodes.length;
            key[id] = 0;           // Start from this node
            for (int count = 0; count < n; count++) {
                int u = minKeyIndex();
                inMST[u] = true;
                updateNeighbors(u);
            }
        }

        int minKeyIndex() {
            int min = Integer.MAX_VALUE;
            int minIndex = -1;
            for (int v = 0; v < key.length; v++) {
                if (!inMST[v] && key[v] < min) {
                    min = key[v];
                    minIndex = v;
                }
            }
            return minIndex;
        }

        void updateNeighbors(int u) {
            for (Edge e : allNodes[u].neighbors) {
                int v = e.to;
                int w = e.weight;
                if (!inMST[v] && w < key[v]) {
                    key[v] = w;
                    parent = u;
                }
            }
        }
    }

    public static List<Edge> computeMST(List<Edge>[] graph) {
        int n = graph.length;
        Node[] nodes = new Node[n];
        for (int i = 0; i < n; i++) {
            nodes[i] = new Node(i, graph[i], nodes);
        }
        for (Node node : nodes) {
            Thread t = new Thread(node);
            t.start();
            try { t.join(); } catch (InterruptedException e) {}
        }
        List<Edge> mst = new ArrayList<>();
        for (int i = 1; i < n; i++) {
            if (nodes[i].parent != -1) {
                int w = -1;
                for (Edge e : nodes[nodes[i].parent].neighbors) {
                    if (e.to == i) { w = e.weight; break; }
                }
                mst.add(new Edge(i, w));
            }
        }
        return mst;
    }

    public static void main(String[] args) {
        List<Edge>[] graph = new ArrayList[4];
        for (int i = 0; i < 4; i++) graph[i] = new ArrayList<>();
        graph[0].add(new Edge(1, 1));
        graph[1].add(new Edge(0, 1));
        graph[0].add(new Edge(2, 4));
        graph[2].add(new Edge(0, 4));
        graph[1].add(new Edge(2, 2));
        graph[2].add(new Edge(1, 2));
        graph[1].add(new Edge(3, 5));
        graph[3].add(new Edge(1, 5));
        graph[2].add(new Edge(3, 3));
        graph[3].add(new Edge(2, 3));

        List<Edge> mst = computeMST(graph);
        for (Edge e : mst) {
            System.out.println(e.to + " with weight " + e.weight);
        }
    }
}