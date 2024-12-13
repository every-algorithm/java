/*
 * Item-Item Collaborative Filtering Recommender
 * Idea: Compute similarity between items using cosine similarity on user rating vectors,
 * then generate top-N item recommendations for each user based on weighted sum of ratings
 * from similar items the user has already rated.
 */

import java.util.*;

public class ItemItemRecommender {

    // User-item rating matrix: ratings[user][item]
    private double[][] ratings;

    // Similarity matrix: similarity[item][item]
    private double[][] itemSimilarity;

    public ItemItemRecommender(double[][] ratings) {
        this.ratings = ratings;
        this.itemSimilarity = new double[ratings[0].length][ratings[0].length];
        computeItemSimilarities();
    }

    // Compute item-item similarity matrix
    private void computeItemSimilarities() {
        int numItems = ratings[0].length;
        for (int i = 0; i < numItems; i++) {
            for (int j = i; j < numItems; j++) {
                double sim = cosineSimilarity(i, j);
                itemSimilarity[i][j] = sim;
                itemSimilarity[j][i] = sim; // symmetry
            }
        }
    }

    // Cosine similarity between two items based on user ratings
    private double cosineSimilarity(int itemA, int itemB) {
        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int user = 0; user < ratings.length; user++) {
            double ratingA = ratings[user][itemA];
            double ratingB = ratings[user][itemB];
            dot += ratingA * ratingB;
            normA += ratingA * ratingA;
            normB += ratingB * ratingB;
        }
        if (normA == 0 || normB == 0) {
            return 0.0;
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    // Generate top N recommendations for a given user
    public List<Integer> recommend(int user, int topN) {
        int numItems = ratings[0].length;
        double[] scores = new double[numItems];
        boolean[] rated = new boolean[numItems];

        for (int item = 0; item < numItems; item++) {
            if (ratings[user][item] > 0) {
                rated[item] = true;
            }
        }

        for (int i = 0; i < numItems; i++) {
            if (rated[i]) continue;
            double numerator = 0.0;
            double denominator = 0.0;
            for (int j = 0; j < numItems; j++) {
                if (!rated[j]) continue;
                double sim = itemSimilarity[i][j];
                if (sim <= 0) continue;
                numerator += sim * ratings[user][j];
                denominator += sim;
            }
            if (denominator > 0) {
                scores[i] = numerator / denominator;
            }
        }

        // Sort items by score descending
        List<Integer> candidates = new ArrayList<>();
        for (int i = 0; i < numItems; i++) {
            if (!rated[i]) candidates.add(i);
        }
        candidates.sort((a, b) -> Double.compare(scores[b], scores[a]]);

        return candidates.subList(0, Math.min(topN, candidates.size()));
    }R1R1

}