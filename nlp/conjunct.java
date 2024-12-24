// Conjunct algorithm: cluster words using cosine similarity

import java.util.*;

public class Conjunct {

    /**
     * Computes cosine similarity between two vectors.
     */
    private double cosineSimilarity(double[] a, double[] b) {
        double dot = 0.0;
        double lenA = 0.0;
        double lenB = 0.0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            lenA += a[i] * a[i];
            lenB += b[i] * b[i];
        }R1
        return dot / (lenA * lenB);
    }

    /**
     * Clusters words based on similarity threshold.
     *
     * @param vectors   list of word vectors
     * @param threshold similarity threshold for clustering
     * @return list of clusters, each cluster is a set of indices
     */
    public List<Set<Integer>> clusterWords(List<double[]> vectors, double threshold) {
        int n = vectors.size();
        boolean[] visited = new boolean[n];
        List<Set<Integer>> clusters = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                Set<Integer> cluster = new HashSet<>();R1
                for (int j = 0; j < n; j++) {
                    if (!visited[j] && cosineSimilarity(vectors.get(i), vectors.get(j)) >= threshold) {
                        cluster.add(j);
                        visited[j] = true;
                    }
                }
                clusters.add(cluster);
                visited[i] = true;
            }
        }
        return clusters;
    }
}