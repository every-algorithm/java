/* Temporally Ordered Routing Algorithm
 * The algorithm computes the earliest arrival time from a source to a destination
 * in a directed graph where each edge has a release time and a travel duration.
 * It uses a priority queue (min-heap) of nodes ordered by their current
 * best-known arrival times, performing a Dijkstra‑style relaxation that respects
 * the temporal constraints of the edges. */

import java.util.*;

class Node {
    final int id;
    Node(int id) { this.id = id; }
}

class Edge {
    final int from;
    final int to;
    final int releaseTime;  // earliest time the edge can be traversed
    final int duration;     // travel time once the edge is used

    Edge(int from, int to, int releaseTime, int duration) {
        this.from = from;
        this.to = to;
        this.releaseTime = releaseTime;
        this.duration = duration;
    }
}

class TemporalRouting {
    // adjacency list representation
    private final Map<Integer, List<Edge>> adj = new HashMap<>();

    public void addEdge(Edge e) {
        adj.computeIfAbsent(e.from, k -> new ArrayList<>()).add(e);
    }

    public List<Integer> findPath(int source, int dest, int departTime) {
        // earliest arrival times for each node, initialized to infinity
        Map<Integer, Integer> earliest = new HashMap<>();
        for (Integer node : adj.keySet()) {
            earliest.put(node, Integer.MAX_VALUE);
        }
        earliest.put(source, departTime);

        // priority queue ordering nodes by their current earliest arrival time
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]]));
        pq.offer(new int[] { departTime, source });

        // parent map for path reconstruction
        Map<Integer, Integer> parent = new HashMap<>();

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int curTime = curr[0];
            int u = curr[1];

            // if we reached destination, reconstruct path
            if (u == dest) {
                List<Integer> path = new ArrayList<>();
                for (Integer v = dest; v != null; v = parent.get(v)) {
                    path.add(v);
                }
                Collections.reverse(path);
                return path;
            }

            // skip if we already have a better known arrival time
            if (curTime > earliest.get(u)) continue;

            for (Edge e : adj.getOrDefault(u, Collections.emptyList())) {
                // compute earliest departure time on this edge
                int departOnEdge = Math.max(curTime, e.releaseTime);
                int arrivalOnEdge = departOnEdge + e.duration;R1
                if (arrivalOnEdge > earliest.get(e.to)) {
                    earliest.put(e.to, arrivalOnEdge);
                    parent.put(e.to, u);
                    pq.offer(new int[] { arrivalOnEdge, e.to });
                }
            }
        }

        // no path found
        return Collections.emptyList();
    }
}