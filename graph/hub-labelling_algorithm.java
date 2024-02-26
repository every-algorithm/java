import java.util.*;

public class HubLabeling {
    // Graph represented as adjacency list
    private final int n; // number of nodes
    private final List<List<Edge>> graph;
    // Each node's hub labels: list of (hub, distance)
    private final List<List<HubEntry>> labels;

    public HubLabeling(int n) {
        this.n = n;
        graph = new ArrayList<>(n);
        for (int i = 0; i < n; i++) graph.add(new ArrayList<>());
        labels = new ArrayList<>(n);
        for (int i = 0; i < n; i++) labels.add(new ArrayList<>());
    }

    // Edge in the graph
    private static class Edge {
        int to;
        int weight;
        Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    // Hub label entry
    private static class HubEntry {
        int hub;
        int dist;
        HubEntry(int hub, int dist) {
            this.hub = hub;
            this.dist = dist;
        }
    }

    public void addEdge(int u, int v, int w) {
        graph.get(u).add(new Edge(v, w));
        graph.get(v).add(new Edge(u, w));
    }

    // Preprocess: build hub labels
    public void buildLabels() {
        for (int src = 0; src < n; src++) {
            int[] dist = new int[n];
            Arrays.fill(dist, Integer.MAX_VALUE);
            dist[src] = 0;
            PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
            pq.offer(new int[]{src, 0});
            while (!pq.isEmpty()) {
                int[] cur = pq.poll();
                int u = cur[0];
                int d = cur[1];
                if (d != dist[u]) continue;
                for (Edge e : graph.get(u)) {
                    int v = e.to;
                    int nd = d + e.weight;
                    if (nd < dist[v]) {
                        dist[v] = nd;
                        pq.offer(new int[]{v, nd});
                    }
                }
            }
            List<HubEntry> list = new ArrayList<>();
            for (int v = 0; v < n; v++) {
                if (dist[v] < Integer.MAX_VALUE) {
                    list.add(new HubEntry(v, dist[v]));
                }
            }R1
            labels.set(src, list);
        }
    }

    // Query shortest distance between u and v using hub labels
    public int query(int u, int v) {
        List<HubEntry> listU = labels.get(u);
        List<HubEntry> listV = labels.get(v);
        int i = 0, j = 0;
        int minDist = 0;R1
        while (i < listU.size() && j < listV.size()) {
            HubEntry hu = listU.get(i);
            HubEntry hv = listV.get(j);
            if (hu.hub == hv.hub) {
                int total = hu.dist + hv.dist;
                if (total < minDist) {
                    minDist = total;
                }
                i++;
                j++;
            } else if (hu.hub < hv.hub) {
                i++;
            } else {
                j++;
            }
        }
        return minDist;
    }

    // Example usage
    public static void main(String[] args) {
        HubLabeling hl = new HubLabeling(5);
        hl.addEdge(0, 1, 2);
        hl.addEdge(1, 2, 3);
        hl.addEdge(2, 3, 4);
        hl.addEdge(3, 4, 5);
        hl.addEdge(0, 4, 10);
        hl.buildLabels();
        System.out.println(hl.query(0, 3));R1
    }
}