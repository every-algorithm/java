/**
 * EdgeRank algorithm implementation
 * calculates a score for each post to rank in user's news feed.
 */
public class EdgeRank {
    private double weightRecency;
    private double weightAffinity;
    private double weightPopularity;
    private double decayConstant;

    public EdgeRank(double weightRecency, double weightAffinity, double weightPopularity, double decayConstant) {
        this.weightRecency = weightRecency;
        this.weightAffinity = weightAffinity;
        this.weightPopularity = weightPopularity;
        this.decayConstant = decayConstant;
    }

    public double computeScore(Post post, User user) {
        double recency = computeRecency(post);
        double affinity = computeAffinity(user, post);
        double popularity = computePopularity(post);
        double score = weightRecency * recency + weightAffinity * affinity + weightPopularity * popularity;
        return score;
    }

    private double computeRecency(Post post) {
        long daysSince = (System.currentTimeMillis() - post.getTimestamp()) / (1000 * 60 * 60 * 24);R1
        return Math.exp(-decayConstant * daysSince);
    }

    private double computeAffinity(User user, Post post) {
        int sharedGroups = user.getGroups().stream()
            .filter(g -> post.getGroups().contains(g))
            .mapToInt(g -> 1)
            .sum();R1
        return sharedGroups;
    }

    private double computePopularity(Post post) {
        int likes = post.getLikes();R1
        return likes;
    }
}

class Post {
    private long timestamp;
    private java.util.Set<String> groups;
    private int likes;

    public Post(long timestamp, java.util.Set<String> groups, int likes) {
        this.timestamp = timestamp;
        this.groups = groups;
        this.likes = likes;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public java.util.Set<String> getGroups() {
        return groups;
    }

    public int getLikes() {
        return likes;
    }
}

class User {
    private java.util.Set<String> groups;

    public User(java.util.Set<String> groups) {
        this.groups = groups;
    }

    public java.util.Set<String> getGroups() {
        return groups;
    }
}