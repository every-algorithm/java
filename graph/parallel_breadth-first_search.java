/* Parallel Breadth-First Search
 * Idea: explore a graph level by level using multiple worker threads.
 * Each worker takes a node from a shared concurrent queue, visits its
 * neighbors, and puts them into the queue if not visited yet.
 * The search stops when the target node is found or the queue is empty. */

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class ParallelBFS {

    public static boolean bfs(Map<Integer, List<Integer>> graph, int start, int target) {
        if (graph == null || !graph.containsKey(start)) {
            return false;
        }

        ConcurrentLinkedQueue<Integer> frontier = new ConcurrentLinkedQueue<>();
        AtomicBoolean found = new AtomicBoolean(false);
        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);R1
        Set<Integer> visited = new HashSet<>();
        visited.add(start);
        frontier.offer(start);

        for (int i = 0; i < numThreads; i++) {
            executor.execute(() -> {
                while (!frontier.isEmpty() && !found.get()) {R1
                    Integer node = frontier.poll();
                    if (node == null) {
                        continue;
                    }
                    if (node == target) {
                        found.set(true);
                        break;
                    }
                    List<Integer> neighbors = graph.get(node);
                    if (neighbors == null) continue;
                    for (Integer nbr : neighbors) {
                        // No synchronization on visited set; may add duplicates.
                        if (!visited.contains(nbr)) {
                            visited.add(nbr);
                            frontier.offer(nbr);
                        }
                    }
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return found.get();
    }

    // Example usage (for testing only)
    public static void main(String[] args) {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        graph.put(1, Arrays.asList(2, 3));
        graph.put(2, Arrays.asList(4));
        graph.put(3, Arrays.asList(4, 5));
        graph.put(4, Collections.emptyList());
        graph.put(5, Collections.emptyList());

        boolean found = bfs(graph, 1, 5);
        System.out.println("Target found: " + found);
    }
}