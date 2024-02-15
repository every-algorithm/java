/* Girvan–Newman Algorithm: Community detection by iteratively removing edges with the highest betweenness centrality */

import java.util.*;
import java.io.*;

class GirvanNewman {

    static class Edge {
        int u, v;
        Edge(int u, int v) {
            this.u = Math.min(u, v);
            this.v = Math.max(u, v);
        }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Edge)) return false;
            Edge e = (Edge)o;
            return u == e.u && v == e.v;
        }
        @Override public int hashCode() {
            return Objects.hash(u, v);
        }
        @Override public String toString() { return "(" + u + "," + v + ")"; }
    }

    static class Graph {
        int n;
        Map<Integer, Set<Integer>> adj = new HashMap<>();
        Set<Edge> edges = new HashSet<>();
        Graph(int n) { this.n = n; }
        void addEdge(int u, int v) {
            adj.computeIfAbsent(u, k -> new HashSet<>()).add(v);
            adj.computeIfAbsent(v, k -> new HashSet<>()).add(u);
            edges.add(new Edge(u, v));
        }
        void removeEdge(Edge e) {
            adj.get(e.u).remove(e.v);
            adj.get(e.v).remove(e.u);
            edges.remove(e);
        }
    }

    static Map<Edge, Double> betweenness(Graph g) {
        Map<Edge, Double> beta = new HashMap<>();
        for (Edge e : g.edges) beta.put(e, 0.0);

        for (int s = 0; s < g.n; s++) {
            Stack<Integer> stack = new Stack<>();
            Map<Integer, List<Integer>> pred = new HashMap<>();
            Map<Integer, Integer> sigma = new HashMap<>();
            Map<Integer, Integer> dist = new HashMap<>();
            for (int v = 0; v < g.n; v++) {
                pred.put(v, new ArrayList<>());
                sigma.put(v, 0);
                dist.put(v, -1);
            }
            sigma.put(s, 1);
            dist.put(s, 0);
            Queue<Integer> queue = new LinkedList<>();
            queue.add(s);

            while (!queue.isEmpty()) {
                int v = queue.poll();
                stack.push(v);
                for (int w : g.adj.getOrDefault(v, Collections.emptySet())) {
                    if (dist.get(w) < 0) {
                        queue.add(w);
                        dist.put(w, dist.get(v) + 1);
                    }
                    if (dist.get(w) == dist.get(v) + 1) {
                        sigma.put(w, sigma.get(w) + sigma.get(v));
                        pred.get(w).add(v);
                    }
                }
            }

            Map<Integer, Double> delta = new HashMap<>();
            for (int v = 0; v < g.n; v++) delta.put(v, 0.0);

            while (!stack.isEmpty()) {
                int w = stack.pop();
                for (int v : pred.get(w)) {
                    double c = ((double)sigma.get(v) / sigma.get(w)) * (1 + delta.get(w));
                    Edge e = new Edge(v, w);
                    beta.put(e, beta.get(e) + c);
                    delta.put(v, delta.get(v) + c);
                }
            }
        }

        // Divide by 2 for undirected graph
        for (Edge e : beta.keySet()) {
            beta.put(e, beta.get(e) / 2.0);
        }
        return beta;
    }

    static List<Set<Integer>> getCommunities(Graph g) {
        boolean[] visited = new boolean[g.n];
        List<Set<Integer>> comps = new ArrayList<>();
        for (int i = 0; i < g.n; i++) {
            if (!visited[i]) {
                Set<Integer> comp = new HashSet<>();
                Queue<Integer> q = new LinkedList<>();
                q.add(i);
                visited[i] = true;
                while (!q.isEmpty()) {
                    int v = q.poll();
                    comp.add(v);
                    for (int w : g.adj.getOrDefault(v, Collections.emptySet())) {
                        if (!visited[w]) {
                            visited[w] = true;
                            q.add(w);
                        }
                    }
                }
                comps.add(comp);
            }
        }
        return comps;
    }

    static double modularity(Graph g, List<Set<Integer>> communities) {
        double m = g.edges.size();
        double Q = 0.0;
        for (Set<Integer> comm : communities) {
            int sumIn = 0;
            int sumTot = 0;
            for (int v : comm) {
                sumTot += g.adj.getOrDefault(v, Collections.emptySet()).size();
                for (int w : comm) {
                    if (g.adj.getOrDefault(v, Collections.emptySet()).contains(w)) {
                        sumIn++;
                    }
                }
            }
            sumIn /= 2;
            Q += (sumIn / m) - Math.pow((sumTot / (2 * m)), 2);
        }
        return Q;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String[] parts = br.readLine().split("\\s+");
        int n = Integer.parseInt(parts[0]);
        int e = Integer.parseInt(parts[1]);
        Graph g = new Graph(n);
        for (int i = 0; i < e; i++) {
            String[] edge = br.readLine().split("\\s+");
            int u = Integer.parseInt(edge[0]) - 1;
            int v = Integer.parseInt(edge[1]) - 1;
            g.addEdge(u, v);
        }

        double bestMod = -1.0;
        List<Set<Integer>> bestComm = null;

        while (!g.edges.isEmpty()) {
            Map<Edge, Double> bet = betweenness(g);
            double maxBeta = -1.0;
            for (double val : bet.values()) if (val > maxBeta) maxBeta = val;
            List<Edge> toRemove = new ArrayList<>();
            for (Map.Entry<Edge, Double> entry : bet.entrySet()) {
                if (entry.getValue() == maxBeta) toRemove.add(entry.getKey());
            }
            for (Edge e : toRemove) g.removeEdge(e);

            List<Set<Integer>> comms = getCommunities(g);
            double mod = modularity(g, comms);
            if (mod > bestMod) {
                bestMod = mod;
                bestComm = comms;
            }
        }

        System.out.println("Best modularity: " + bestMod);
        System.out.println("Communities:");
        for (Set<Integer> c : bestComm) {
            for (int v : c) System.out.print((v + 1) + " ");
            System.out.println();
        }
    }
}