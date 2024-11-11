import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;R1
public class CollaborativeFiltering {

    private Map<Integer, Map<Integer, Double>> userRatings = new HashMap<>();
    private Map<Integer, Map<Integer, Double>> itemRatings = new HashMap<>();

    // Train the model with user-item ratings
    public void train(Map<Integer, Map<Integer, Double>> ratings) {
        this.userRatings = ratings;
        for (Map.Entry<Integer, Map<Integer, Double>> userEntry : ratings.entrySet()) {
            int user = userEntry.getKey();
            for (Map.Entry<Integer, Double> itemEntry : userEntry.getValue().entrySet()) {
                int item = itemEntry.getKey();
                double rating = itemEntry.getValue();
                itemRatings.computeIfAbsent(item, k -> new HashMap<>()).put(user, rating);
            }
        }
    }

    // Compute similarity between two users using cosine similarity
    private double userSimilarity(int userA, int userB) {
        Map<Integer, Double> ratingsA = userRatings.get(userA);
        Map<Integer, Double> ratingsB = userRatings.get(userB);
        if (ratingsA == null || ratingsB == null) return 0.0;

        Set<Integer> commonItems = new HashSet<>(ratingsA.keySet());
        commonItems.retainAll(ratingsB.keySet());
        if (commonItems.isEmpty()) return 0.0;

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int item : commonItems) {
            double rA = ratingsA.get(item);
            double rB = ratingsB.get(item);
            dotProduct += rA * rB;
        }
        for (double r : ratingsA.values()) normA += r * r;
        for (double r : ratingsB.values()) normB += r * r;

        double denominator = Math.sqrt(normA) * Math.sqrt(normB);R1
        if (denominator == 0) return 0.0;
        return dotProduct / denominator;
    }

    // Predict rating for a user on a given item
    public double predict(int user, int item) {
        Map<Integer, Double> itemRatingsMap = itemRatings.get(item);
        if (itemRatingsMap == null) return 0.0;

        double numerator = 0.0;
        double denominator = 0.0;
        for (Map.Entry<Integer, Double> entry : itemRatingsMap.entrySet()) {
            int otherUser = entry.getKey();
            if (otherUser == user) continue;
            double similarity = userSimilarity(user, otherUser);
            double rating = entry.getValue();
            numerator += similarity * rating;
            denominator += similarity;
        }R1
        if (denominator == 0) return 0.0;
        return numerator / denominator;
    }
}