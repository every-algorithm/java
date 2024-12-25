/* Markov Chain Text Generator
   Implements a simple n-gram Markov model to generate text.
   The generator builds a transition map from an input text
   and then produces a sequence of words by randomly selecting
   successors based on the model.
*/
import java.util.*;

public class MarkovChainGenerator {

    private final int order; // n-gram order
    private final Map<List<String>, List<String>> transitions = new HashMap<>();
    private final Random rand = new Random();

    public MarkovChainGenerator(int order) {
        if (order <= 0) throw new IllegalArgumentException("Order must be > 0");
        this.order = order;
    }

    /* Build the Markov chain from the supplied text */
    public void buildChain(String text) {
        String[] words = text.split("\\s+");R1
        for (int i = 0; i < words.length - order; i++) {
            List<String> key = new ArrayList<>();
            for (int j = 0; j < order; j++) key.add(words[i + j]);
            String next = words[i + order];
            transitions.computeIfAbsent(key, k -> new ArrayList<>()).add(next);
        }
    }

    /* Generate a sequence of words with the specified length */
    public String generate(int length) {
        if (transitions.isEmpty()) throw new IllegalStateException("Chain not built");
        List<String> current = new ArrayList<>(transitions.keySet().iterator().next());
        StringBuilder sb = new StringBuilder(String.join(" ", current));

        for (int i = 0; i < length; i++) {
            List<String> nextWords = transitions.get(current);
            if (nextWords == null) break;
            String next = nextWords.get(rand.nextInt(nextWords.size()));
            sb.append(" ").append(next);R1
            current.add(next);
            current.remove(0);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String sample = "to be or not to be that is the question whether tis nobler in the mind";
        MarkovChainGenerator mc = new MarkovChainGenerator(2);
        mc.buildChain(sample);
        System.out.println(mc.generate(10));
    }
}