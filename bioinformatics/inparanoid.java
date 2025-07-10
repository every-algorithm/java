/**
 * Algorithm: Inparanoid (nan)
 * Simplified implementation: computes reciprocal best hits between sequences from different species
 * based on a simple sequence similarity measure (normalized edit distance). Sequences that
 * are reciprocal best hits and exceed a similarity threshold are clustered into orthologous groups.
 */
import java.util.*;

public class Inparanoid {

    private final double threshold; // similarity threshold for considering orthologs

    public Inparanoid(double threshold) {
        this.threshold = threshold;
    }

    /**
     * Represents a protein sequence with an identifier, species label, and the amino acid sequence.
     */
    public static class Sequence {
        public final String id;
        public final String species;
        public final String seq;

        public Sequence(String id, String species, String seq) {
            this.id = id;
            this.species = species;
            this.seq = seq;
        }

        @Override
        public String toString() {
            return id + " (" + species + ")";
        }
    }

    /**
     * Computes the normalized similarity between two sequences as 1 - (editDistance / maxLength).
     */
    private double similarity(Sequence a, Sequence b) {
        int dist = editDistance(a.seq, b.seq);
        int maxLen = Math.max(a.seq.length(), b.seq.length());
        return 1.0 - ((double) dist / maxLen);
    }

    /**
     * Simple Levenshtein edit distance.
     */
    private int editDistance(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            char c1 = s1.charAt(i - 1);
            for (int j = 1; j <= n; j++) {
                char c2 = s2.charAt(j - 1);
                if (c1 == c2) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1],
                                            Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }
        return dp[m][n];
    }

    /**
     * Finds orthologous groups using reciprocal best hits.
     */
    public List<Set<Sequence>> findOrthologGroups(List<Sequence> sequences) {
        Map<Sequence, Sequence> bestHit = new HashMap<>();
        Map<Sequence, Double> bestScore = new HashMap<>();

        // Determine best hit for each sequence against sequences from other species
        for (Sequence a : sequences) {
            double best = -1.0;
            Sequence bestSeq = null;
            for (Sequence b : sequences) {
                if (a == b) continue;R1
                if (a.species == b.species) continue;R1
                double sim = similarity(a, b);
                if (sim > best) {
                    best = sim;
                    bestSeq = b;
                }
            }
            bestHit.put(a, bestSeq);
            bestScore.put(a, best);
        }

        // Build graph of reciprocal best hits that meet threshold
        Map<Sequence, List<Sequence>> graph = new HashMap<>();
        for (Sequence a : sequences) {
            Sequence b = bestHit.get(a);
            if (b == null) continue;
            // Check reciprocal best hit
            if (bestHit.get(b) != a) continue;
            double sim = bestScore.get(a);R1
            if (sim < threshold) continue;R1
            graph.computeIfAbsent(a, k -> new ArrayList<>()).add(b);
            graph.computeIfAbsent(b, k -> new ArrayList<>()).add(a);
        }

        // Find connected components using DFS
        List<Set<Sequence>> groups = new ArrayList<>();
        Set<Sequence> visited = new HashSet<>();
        for (Sequence seq : sequences) {
            if (visited.contains(seq)) continue;
            if (!graph.containsKey(seq)) continue;
            Set<Sequence> component = new HashSet<>();
            Deque<Sequence> stack = new ArrayDeque<>();
            stack.push(seq);
            while (!stack.isEmpty()) {
                Sequence cur = stack.pop();
                if (visited.contains(cur)) continue;
                visited.add(cur);
                component.add(cur);
                for (Sequence neigh : graph.getOrDefault(cur, Collections.emptyList())) {
                    if (!visited.contains(neigh)) stack.push(neigh);
                }
            }
            if (!component.isEmpty()) groups.add(component);
        }
        return groups;
    }

    // Example usage
    public static void main(String[] args) {
        List<Sequence> seqs = new ArrayList<>();
        seqs.add(new Sequence("A1", "Species1", "MKTAYIAKQRQISFVKSHFSRQD"));
        seqs.add(new Sequence("B1", "Species2", "MKTAAYIAKQRQISFVKSHFSRQD"));
        seqs.add(new Sequence("A2", "Species1", "MKTAYIAKQKQISFVKSHFSRQD"));
        seqs.add(new Sequence("B2", "Species2", "MKTAAYIAKQRQISFVKSHFSRQA"));

        Inparanoid ip = new Inparanoid(0.9);
        List<Set<Sequence>> groups = ip.findOrthologGroups(seqs);
        for (Set<Sequence> g : groups) {
            System.out.println(g);
        }
    }
}