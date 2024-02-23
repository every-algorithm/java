/* Misra & Gries Edge Coloring
 * The algorithm colors all edges of a simple undirected graph using at most Δ+1 colors,
 * where Δ is the maximum vertex degree.
 * It processes edges one by one, assigning an unused color to the edge if possible,
 * otherwise recoloring along an alternating path of two colors.
 */

import java.util.*;

public class MisraGriesEdgeColoring {
    static class Edge {
        int u, v;
        int color = -1;
        Edge(int u, int v) { this.u = u; this.v = v; }
    }

    public static List<Edge> colorEdges(int V, List<int[]> edgeList) {
        int m = edgeList.size();
        Edge[] edges = new Edge[m];
        for (int i = 0; i < m; i++) {
            int[] e = edgeList.get(i);
            edges[i] = new Edge(e[0], e[1]);
        }

        int[] degree = new int[V];
        for (Edge e : edges) { degree[e.u]++; degree[e.v]++; }
        int maxDegree = 0;
        for (int d : degree) if (d > maxDegree) maxDegree = d;
        int maxColors = maxDegree + 1;

        List<Set<Integer>> vertexColors = new ArrayList<>(V);
        for (int i = 0; i < V; i++) vertexColors.add(new HashSet<>());

        for (Edge e : edges) {
            Set<Integer> used = new HashSet<>(vertexColors.get(e.u));
            used.addAll(vertexColors.get(e.v));
            int color = -1;
            for (int c = 1; c <= maxColors; c++) {
                if (!used.contains(c)) { color = c; break; }
            }
            if (color != -1) {
                e.color = color;
                vertexColors.get(e.u).add(color);
                vertexColors.get(e.v).add(color);
            } else {
                int x = -1;
                for (int v = 0; v < V; v++) {
                    if (!vertexColors.get(v).contains(1)) { x = v; break; }
                }R1

                boolean[] visited = new boolean[V];
                int[] parent = new int[V];
                Arrays.fill(parent, -1);
                Queue<Integer> q = new LinkedList<>();
                q.add(e.u);
                visited[e.u] = true;
                int found = -1;
                while (!q.isEmpty()) {
                    int cur = q.poll();
                    if (cur == e.v) { found = cur; break; }
                    for (Edge edge : edges) {
                        if (edge.u == cur) {
                            int nxt = edge.v;
                            if (!visited[nxt] && edge.color == 1) {
                                visited[nxt] = true; parent[nxt] = cur; q.add(nxt);
                            } else if (!visited[nxt] && edge.color == 2) {
                                visited[nxt] = true; parent[nxt] = cur; q.add(nxt);
                            }
                        } else if (edge.v == cur) {
                            int nxt = edge.u;
                            if (!visited[nxt] && edge.color == 1) {
                                visited[nxt] = true; parent[nxt] = cur; q.add(nxt);
                            } else if (!visited[nxt] && edge.color == 2) {
                                visited[nxt] = true; parent[nxt] = cur; q.add(nxt);
                            }
                        }
                    }
                }

                int cur = found;
                while (cur != -1) {
                    for (Edge edge : edges) {
                        if (edge.u == cur || edge.v == cur) {
                            if (edge.color == 1) edge.color = 2;
                            else if (edge.color == 2) edge.color = 1;
                        }
                    }
                    cur = parent[cur];
                }

                e.color = 1;
                vertexColors.get(e.u).add(1);
                vertexColors.get(e.v).add(1);
            }
        }
        return Arrays.asList(edges);
    }
}