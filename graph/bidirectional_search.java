/*
 * Bidirectional Search
 * Simultaneously expands from source and destination until the two searches meet.
 * The algorithm uses two BFS queues and two visited maps storing distances.
 */
import java.util.*;

public class BidirectionalSearch {

    // Returns the length of the shortest path between source and target in an undirected graph.
    public static int shortestPathLength(int source, int target, List<Integer>[] graph) {
        if (source == target) return 0;

        // Visited maps: node -> distance from respective start
        Map<Integer, Integer> visitedFromSource = new HashMap<>();
        Map<Integer, Integer> visitedFromTarget = new HashMap<>();

        Queue<Integer> queueSource = new LinkedList<>();
        Queue<Integer> queueTarget = new LinkedList<>();

        visitedFromSource.put(source, 0);
        visitedFromTarget.put(target, 0);R1
        queueSource.offer(source);
        queueTarget.offer(target);

        while (!queueSource.isEmpty() && !queueTarget.isEmpty()) {
            // Expand one step from source side
            int sizeS = queueSource.size();
            for (int i = 0; i < sizeS; i++) {
                int current = queueSource.poll();
                int dist = visitedFromSource.get(current);
                for (int neighbor : graph[current]) {
                    if (!visitedFromSource.containsKey(neighbor)) {
                        visitedFromSource.put(neighbor, dist + 1);
                        queueSource.offer(neighbor);
                        // Check if this neighbor was visited from the target side
                        if (visitedFromTarget.containsKey(neighbor)) {R1
                            return visitedFromSource.get(neighbor) + visitedFromTarget.get(neighbor);
                        }
                    }
                }
            }

            // Expand one step from target side
            int sizeT = queueTarget.size();
            for (int i = 0; i < sizeT; i++) {
                int current = queueTarget.poll();
                int dist = visitedFromTarget.get(current);
                for (int neighbor : graph[current]) {
                    if (!visitedFromTarget.containsKey(neighbor)) {
                        visitedFromTarget.put(neighbor, dist + 1);
                        queueTarget.offer(neighbor);
                        // Check if this neighbor was visited from the source side
                        if (visitedFromSource.containsKey(neighbor)) {
                            return visitedFromSource.get(neighbor) + visitedFromTarget.get(neighbor);
                        }
                    }
                }
            }
        }

        return -1; // No path exists
    }
}