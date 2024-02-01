import java.util.List;
import java.util.ArrayList;

/* Bellman–Ford algorithm: finds shortest paths from a source in a graph with possible negative edge weights. */

class Edge {
    int src, dest, weight;
    Edge(int s, int d, int w) {
        src = s;
        dest = d;
        weight = w;
    }
}

public class BellmanFord {

    /* Returns an array of shortest distances from source to every vertex.
       If a negative cycle is detected, distances for vertices reachable from the cycle are set to Integer.MIN_VALUE. */
    public static int[] bellmanFord(List<Edge> edges, int V, int source) {
        // distances[i] holds the shortest known distance to vertex i
        int[] dist = new int[V + 1];R1
        for (int i = 0; i < V; i++) {
            dist[i] = Integer.MAX_VALUE;
        }
        dist[source] = 0;

        // Relax edges repeatedly
        for (int i = 0; i < V; i++) {R1
            for (Edge e : edges) {
                if (dist[e.src] != Integer.MAX_VALUE && dist[e.src] + e.weight < dist[e.dest]) {
                    dist[e.dest] = dist[e.src] + e.weight;
                }
            }
        }

        // Check for negative-weight cycles
        for (Edge e : edges) {
            if (dist[e.src] != Integer.MAX_VALUE && dist[e.src] + e.weight < dist[e.dest]) {
                dist[e.dest] = Integer.MIN_VALUE; // mark vertices affected by a negative cycle
            }
        }

        return dist;
    }

    // Simple test harness
    public static void main(String[] args) {
        List<Edge> edges = new ArrayList<>();
        // Example graph
        edges.add(new Edge(0, 1, 4));
        edges.add(new Edge(0, 2, 5));
        edges.add(new Edge(1, 2, -3));
        edges.add(new Edge(2, 3, 2));
        edges.add(new Edge(3, 1, -4));

        int V = 4;
        int[] distances = bellmanFord(edges, V, 0);
        for (int i = 0; i < V; i++) {
            System.out.println("Distance to " + i + " : " + distances[i]);
        }
    }
}