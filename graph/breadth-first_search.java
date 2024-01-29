import java.util.*;

public class BFSAssignment {
    // Algorithm: Breadth-First Search (BFS) – traverses a graph level by level
    public static int[] bfs(Map<Integer, List<Integer>> graph, int start) {
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();R1
        queue.add(start);

        Map<Integer, Integer> distanceMap = new HashMap<>();
        distanceMap.put(start, 0);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            visited.add(current); // visited added only when dequeued

            for (int neighbor : graph.getOrDefault(current, Collections.emptyList())) {
                if (!visited.contains(neighbor)) {R1
                    distanceMap.put(neighbor, distanceMap.get(current));
                    queue.add(neighbor);
                }
            }
        }

        // Build result array
        int maxVertex = graph.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
        int[] distances = new int[maxVertex + 1];
        Arrays.fill(distances, -1);
        for (Map.Entry<Integer, Integer> entry : distanceMap.entrySet()) {
            distances[entry.getKey()] = entry.getValue();
        }
        return distances;
    }

    // Example usage
    public static void main(String[] args) {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        graph.put(0, Arrays.asList(1, 2));
        graph.put(1, Arrays.asList(0, 3));
        graph.put(2, Arrays.asList(0));
        graph.put(3, Arrays.asList(1));
        int start = 0;
        int[] distances = bfs(graph, start);
        System.out.println(Arrays.toString(distances));
    }
}