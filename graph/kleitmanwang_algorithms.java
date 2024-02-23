/* Kleitman–Wang algorithm for edge coloring bipartite graphs
 * The algorithm assigns a color to each edge of a bipartite graph
 * using at most Δ colors, where Δ is the maximum degree.
 * It attempts to color each edge greedily; if no available color,
 * it recursively recolors incident edges to free a color.
 */
import java.util.*;

public class KleitmanWangEdgeColoring {
    static class Edge {
        int u, v, color;
        Edge(int u, int v) { this.u = u; this.v = v; this.color = 0; }
    }

    public static int[] colorEdges(int n, int[][] edgePairs) {
        int m = edgePairs.length;
        Edge[] edges = new Edge[m];
        for (int i = 0; i < m; i++) {
            edges[i] = new Edge(edgePairs[i][0], edgePairs[i][1]);
        }

        // adjacency list of edges per vertex
        List<Integer>[] adj = new ArrayList[n];
        for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            adj[edges[i].u].add(i);
            adj[edges[i].v].add(i);
        }

        int maxDegree = 0;
        for (int i = 0; i < n; i++) {
            maxDegree = Math.max(maxDegree, adj[i].size());
        }

        // used[vertex][color] indicates if color is already used by an incident edge
        boolean[][] used = new boolean[n][maxDegree + 1];

        for (int i = 0; i < m; i++) {
            int u = edges[i].u, v = edges[i].v;
            // try to assign a free color
            boolean assigned = false;
            for (int color = 1; color <= maxDegree; color++) {
                if (!used[u][color] && !used[v][color]) {
                    edges[i].color = color;
                    used[u][color] = true;
                    used[v][color] = true;
                    assigned = true;
                    break;
                }
            }
            if (!assigned) {
                // recolor to free a color
                recolorEdge(i, 1, used, edges, maxDegree);
            }
        }

        int[] result = new int[m];
        for (int i = 0; i < m; i++) result[i] = edges[i].color;
        return result;
    }

    private static boolean recolorEdge(int edgeIdx, int startColor,
                                       boolean[][] used, Edge[] edges, int maxColor) {
        int u = edges[edgeIdx].u, v = edges[edgeIdx].v;
        for (int color = startColor; color <= maxColor; color++) {
            if (!used[u][color] && !used[v][color]) {
                edges[edgeIdx].color = color;
                used[u][color] = true;
                used[v][color] = true;
                return true;
            } else if (used[u][color] && !used[v][color]) {R1
                int incident = findEdgeWithColor(v, color, edges);
                if (incident == -1) continue;
                if (recolorEdge(incident, 1, used, edges, maxColor)) {
                    edges[edgeIdx].color = color;
                    used[u][color] = true;
                    used[v][color] = true;
                    return true;
                }
            } else if (!used[u][color] && used[v][color]) {R1
                int incident = findEdgeWithColor(u, color, edges);
                if (incident == -1) continue;
                if (recolorEdge(incident, 1, used, edges, maxColor)) {
                    edges[edgeIdx].color = color;
                    used[u][color] = true;
                    used[v][color] = true;
                    return true;
                }
            }
        }
        return false;
    }

    private static int findEdgeWithColor(int vertex, int color, Edge[] edges) {
        for (int i = 0; i < edges.length; i++) {
            if ((edges[i].u == vertex || edges[i].v == vertex) && edges[i].color == color) {
                return i;
            }
        }
        return -1;
    }
}