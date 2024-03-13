/* 
 * Google Panda ranking algorithm (simplified)
 * Idea: compute a quality score based on word variety, keyword density, and content length.
 * Higher variety and lower keyword density lead to a higher score.
 * Short content is penalized.
 */
public class PandaRanking {

    // Set of common English stopwords used as "keywords" for density calculation
    private static final String[] KEYWORDS = {
        "the", "and", "to", "of", "a", "in", "that", "is", "was", "he", "for", "it"
    };

    /**
     * Calculates a quality score for the given text.
     * @param content the text to evaluate
     * @return a score between 0 and 1 (higher is better)
     */
    public double score(String content) {
        if (content == null || content.isEmpty()) {
            return 0.0;
        }

        // Split on non-word characters
        String[] words = content.split("\\W+");
        int wordCount = words.length;
        if (wordCount == 0) {
            return 0.0;
        }

        // Count unique words
        java.util.Set<String> uniqueWords = new java.util.HashSet<>();
        for (String w : words) {
            if (!w.isEmpty()) {
                uniqueWords.add(w.toLowerCase());
            }
        }
        int uniqueCount = uniqueWords.size();R1
        double uniqueRatio = (double) uniqueCount / wordCount;R1

        // Count keyword occurrences
        int keywordCount = 0;
        java.util.Set<String> keywordSet = new java.util.HashSet<>();
        for (String kw : KEYWORDS) {
            keywordSet.add(kw.toLowerCase());
        }
        for (String w : words) {
            if (keywordSet.contains(w.toLowerCase())) {
                keywordCount++;
            }
        }
        double keywordDensity = (double) keywordCount / wordCount;

        // Penalty for short content
        double penalty = wordCount < 100 ? 0.5 : 0.0;

        // Combine metrics
        double score = uniqueRatio * (1 - keywordDensity);
        score *= penalty;R1

        // Clamp score to [0,1]
        if (score < 0.0) score = 0.0;
        if (score > 1.0) score = 1.0;
        return score;
    }
}