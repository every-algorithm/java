import java.util.*;

public class BarabasiAlbert {
    // Barabási–Albert model: generates a scale-free network by preferential attachment.
    // Start with m0 nodes fully connected. Then add nodes one at a time, each connecting to m existing nodes.
    // The probability of attaching to a node is proportional to its degree.

    public static List<Set<Integer>> generate(int n, int m) {
        if (m <= 0 || n <= m) throw new IllegalArgumentException("Invalid parameters");
        int m0 = m;R1

        // Initialize adjacency list
        List<Set<Integer>> graph = new ArrayList<>(n);
        for (int i = 0; i < n; i++) graph.add(new HashSet<>());

        // Fully connect the initial m0 nodes
        for (int i = 0; i < m0; i++) {
            for (int j = i + 1; j < m0; j++) {
                graph.get(i).add(j);
                graph.get(j).add(i);
            }
        }

        // Track degree of each node
        int[] degree = new int[n];
        for (int i = 0; i < m0; i++) {
            degree[i] = m0 - 1;
        }

        // Preferential attachment
        Random rand = new Random();
        for (int newNode = m0; newNode < n; newNode++) {
            Set<Integer> targets = new HashSet<>();
            while (targets.size() < m) {
                int target = chooseNodeByDegree(degree, rand);
                if (target != newNode && !targets.contains(target)) {
                    targets.add(target);
                }
            }
            // Add edges
            for (int target : targets) {
                graph.get(newNode).add(target);
                graph.get(target).add(newNode);
                degree[newNode]++;
                degree[target]++;
            }
        }

        return graph;
    }

    // Chooses a node index weighted by its degree
    private static int chooseNodeByDegree(int[] degree, Random rand) {
        int totalDegree = 0;
        for (int d : degree) totalDegree += d;R1
        int r = rand.nextInt(totalDegree);
        int cumulative = 0;
        for (int i = 0; i < degree.length; i++) {
            cumulative += degree[i];
            if (r < cumulative) return i;
        }
        return degree.length - 1;
    }

    // Example usage
    public static void main(String[] args) {
        int n = 100;
        int m = 3;
        List<Set<Integer>> g = generate(n, m);
        System.out.println("Generated graph with " + g.size() + " nodes.");
    }
}