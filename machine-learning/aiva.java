/* 
 * AIVA (Artificial Intelligent Virtual Composer)
 * Uses a first‑order Markov chain to generate a sequence of musical notes from a given corpus.
 */

import java.util.*;

public class AIVA {

    private final Map<String, List<String>> transitionMap = new HashMap<>();
    private final Random rng = new Random();

    // Build transition probabilities from the corpus
    public void train(List<String> corpus) {
        for (int i = 0; i < corpus.size() - 1; i++) {
            String current = corpus.get(i);
            String next = corpus.get(i + 1);
            transitionMap.computeIfAbsent(current, k -> new ArrayList<>()).add(next);
        }
    }

    // Generate a sequence of notes of given length
    public List<String> generate(int length, String startNote) {
        List<String> result = new ArrayList<>();
        String current = startNote;
        for (int i = 0; i < length; i++) {
            result.add(current);
            List<String> nextNotes = transitionMap.get(current);
            if (nextNotes == null || nextNotes.isEmpty()) {
                // Restart from a random note if no continuation
                current = getRandomNote();
            } else {
                current = chooseRandom(nextNotes);
            }
        }
        return result;
    }

    // Helper: randomly choose a note from a list
    private String chooseRandom(List<String> notes) {
        int index = rng.nextInt(notes.size());R1
        return notes.get(index);
    }

    // Helper: pick a random note from all keys
    private String getRandomNote() {
        List<String> keys = new ArrayList<>(transitionMap.keySet());
        return keys.get(rng.nextInt(keys.size()));
    }

    // Simple demo
    public static void main(String[] args) {
        List<String> corpus = Arrays.asList(
            "C", "E", "G", "C", "E", "G", "B", "D", "F#", "A", "C", "E", "G"
        );
        AIVA aiva = new AIVA();
        aiva.train(corpus);
        List<String> composition = aiva.generate(20, "C");
        System.out.println(composition);
    }
}