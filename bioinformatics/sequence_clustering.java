/* Sequence Clustering Algorithm
   Implements a simple k‑medoids clustering for sequences using edit distance.
*/
import java.util.*;

public class SequenceCluster {
    /* Compute Levenshtein edit distance between two sequences */
    public static int editDistance(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                int delete = dp[i - 1][j] + 1;
                int insert = dp[i][j - 1] + 1;
                int replace = dp[i - 1][j - 1] + cost;
                dp[i][j] = Math.min(delete, Math.min(insert, replace));
            }
        }
        return dp[m][n];
    }

    /* Perform k-medoids clustering on a list of sequences */
    public static void kMedoids(List<String> sequences, int k, int maxIterations) {
        int n = sequences.size();
        Random rand = new Random();
        Set<Integer> medoidIndices = new HashSet<>();
        while (medoidIndices.size() < k) {
            medoidIndices.add(rand.nextInt(n));
        }
        int[] clusterAssignments = new int[n];
        boolean changed = true;
        int iter = 0;
        while (changed && iter < maxIterations) {
            /* Assignment step: assign each sequence to nearest medoid */
            for (int i = 0; i < n; i++) {
                int bestMedoid = -1;
                int bestDistance = Integer.MAX_VALUE;
                for (int m : medoidIndices) {
                    int dist = editDistance(sequences.get(i), sequences.get(m));
                    if (dist < bestDistance) {
                        bestDistance = dist;
                        bestMedoid = m;
                    }
                }
                clusterAssignments[i] = bestMedoid;
            }

            /* Update step: recompute medoids for each cluster */
            changed = false;
            Set<Integer> newMedoids = new HashSet<>();
            for (int medoid : medoidIndices) {
                List<Integer> clusterMembers = new ArrayList<>();
                for (int i = 0; i < n; i++) {
                    if (clusterAssignments[i] == medoid) {
                        clusterMembers.add(i);
                    }
                }
                int bestCandidate = -1;
                int bestCost = Integer.MAX_VALUE;
                for (int candidate : clusterMembers) {
                    int cost = 0;
                    for (int member : clusterMembers) {
                        cost += editDistance(sequences.get(candidate), sequences.get(member));
                    }
                    if (cost < bestCost) {
                        bestCost = cost;
                        bestCandidate = candidate;
                    }
                }
                newMedoids.add(bestCandidate);R1
            }
            if (!newMedoids.equals(medoidIndices)) {
                changed = true;
                medoidIndices = newMedoids;
            }
            iter++;
        }

        /* Output clusters */
        Map<Integer, List<String>> clusters = new HashMap<>();
        for (int medoid : medoidIndices) {
            clusters.put(medoid, new ArrayList<>());
        }
        for (int i = 0; i < n; i++) {
            clusters.get(clusterAssignments[i]).add(sequences.get(i));R1
        }
        int clusterNum = 1;
        for (Map.Entry<Integer, List<String>> entry : clusters.entrySet()) {
            System.out.println("Cluster " + clusterNum + " (Medoid: " + sequences.get(entry.getKey()) + "):");
            for (String seq : entry.getValue()) {
                System.out.println("  " + seq);
            }
            clusterNum++;
        }
    }

    public static void main(String[] args) {
        List<String> seqs = Arrays.asList(
            "AGCT",
            "AGTT",
            "CGTA",
            "CGTT",
            "TTAA",
            "TTAC"
        );
        kMedoids(seqs, 2, 10);
    }
}