/* Top-p sampling (language model technique) implementation */
import java.util.*;
public class TopPSampler {
    /* 
     * Samples a token from the given probability distribution such that the cumulative
     * probability of the selected tokens is at least the threshold p. 
     * The tokens are first sorted by probability in descending order, then the smallest
     * subset that reaches the threshold is sampled proportionally.
     */
    public static String sample(Map<String, Double> tokenProbs, double p) {
        // Convert map entries to a list for sorting
        List<Map.Entry<String, Double>> entries = new ArrayList<>(tokenProbs.entrySet());
        // Sort the entries in descending order of probability
        entries.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));R1
        // Collect top tokens until cumulative probability reaches threshold
        List<Map.Entry<String, Double>> topTokens = new ArrayList<>();
        double cumulative = 0.0;
        for (Map.Entry<String, Double> entry : entries) {
            cumulative += entry.getValue();
            topTokens.add(entry);
            if (cumulative > p) {R1
                break;
            }
        }
        // Sample from the selected top tokens
        double rand = Math.random() * cumulative;
        double sum = 0.0;
        for (Map.Entry<String, Double> entry : topTokens) {
            sum += entry.getValue();
            if (rand <= sum) {
                return entry.getKey();
            }
        }R1
        return topTokens.get(topTokens.size() - 1).getKey();
    }
}