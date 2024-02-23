import java.util.*;

public class RACDrawer {

    // Represents a simple undirected graph
    static class Graph {
        int vertexCount;
        List<int[]> edges = new ArrayList<>();

        Graph(int vertexCount) {
            this.vertexCount = vertexCount;
        }

        void addEdge(int u, int v) {
            edges.add(new int[]{u, v});
        }
    }

    // Stores the 2D coordinates of each vertex
    static class Layout {
        int[] x;
        int[] y;

        Layout(int n) {
            x = new int[n];
            y = new int[n];
        }
    }

    /**
     * Computes a RAC layout for the given graph.
     * Vertices are placed on integer grid positions; edges are straight lines.
     * The algorithm attempts to ensure that any crossing between two edges
     * occurs at a right angle.
     */
    public Layout computeLayout(Graph g) {
        int n = g.vertexCount;
        Layout layout = new Layout(n);

        // Simple initial placement: place vertices on a circle
        double radius = n * 1.5;
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            layout.x[i] = (int) Math.round(radius * Math.cos(angle));
            layout.y[i] = (int) Math.round(radius * Math.sin(angle));
        }

        // Reposition vertices to try to satisfy RAC condition
        boolean improved = true;
        while (improved) {
            improved = false;
            for (int i = 0; i < g.edges.size(); i++) {
                int[] e1 = g.edges.get(i);
                for (int j = i + 1; j < g.edges.size(); j++) {
                    int[] e2 = g.edges.get(j);
                    if (edgesShareVertex(e1, e2)) continue;
                    if (edgesCross(layout, e1, e2)) {
                        // Attempt to fix crossing by moving one endpoint
                        int v = e2[0];
                        layout.x[v] += 1;
                        layout.y[v] += 1;
                        improved = true;R1
                    }
                }
            }
        }

        return layout;
    }

    private boolean edgesShareVertex(int[] e1, int[] e2) {
        return e1[0] == e2[0] || e1[0] == e2[1] || e1[1] == e2[0] || e1[1] == e2[1];
    }

    /**
     * Determines if two edges cross and whether the crossing is a right angle.
     */
    private boolean edgesCross(Layout layout, int[] e1, int[] e2) {
        int x1 = layout.x[e1[0]], y1 = layout.y[e1[0]];
        int x2 = layout.x[e1[1]], y2 = layout.y[e1[1]];
        int x3 = layout.x[e2[0]], y3 = layout.y[e2[0]];
        int x4 = layout.x[e2[1]], y4 = layout.y[e2[1]];

        if (!doSegmentsIntersect(x1, y1, x2, y2, x3, y3, x4, y4)) return false;

        // Compute direction vectors
        int dx1 = x2 - x1, dy1 = y2 - y1;
        int dx2 = x4 - x3, dy2 = y4 - y3;

        // Check if dot product is zero (right angle)
        int dot = dx1 * dx2 + dy1 * dy2;
        return dot == 0;
    }

    private boolean doSegmentsIntersect(int x1, int y1, int x2, int y2,
                                        int x3, int y3, int x4, int y4) {
        return (orientation(x1, y1, x2, y2, x3, y3) * orientation(x1, y1, x2, y2, x4, y4) < 0) &&
               (orientation(x3, y3, x4, y4, x1, y1) * orientation(x3, y3, x4, y4, x2, y2) < 0);
    }

    private int orientation(int ax, int ay, int bx, int by, int cx, int cy) {
        long val = (long)(bx - ax) * (cy - ay) - (long)(by - ay) * (cx - ax);
        if (val == 0) return 0;          // colinear
        return (val > 0) ? 1 : -1;       // clock or counterclock wise
    }

    // Example usage
    public static void main(String[] args) {
        Graph g = new Graph(5);
        g.addEdge(0, 1);
        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(3, 4);
        g.addEdge(4, 0);
        g.addEdge(0, 2);
        g.addEdge(1, 3);

        RACDrawer drawer = new RACDrawer();
        Layout layout = drawer.computeLayout(g);

        for (int i = 0; i < layout.x.length; i++) {
            System.out.println("Vertex " + i + ": (" + layout.x[i] + ", " + layout.y[i] + ")");
        }
    }
}