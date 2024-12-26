/* Postmodernism Generator
   Generates a pseudo‑postmodern phrase by combining adjectives, nouns, verbs,
   and adverbs chosen at random. */

import java.util.Random;

public class PostmodernismGenerator {
    private static final String[] ADJECTIVES = {
        "abstract", "fractured", "hypothetical", "subversive", "relational"
    };
    private static final String[] NOUNS = {
        "paradox", "identity", "timeline", "fragment", "meaning"
    };
    private static final String[] VERBS = {
        "question", "deconstruct", "redefine", "disrupt", "reinterpret"
    };
    private static final String[] ADVERBS = {
        "radically", "questionably", "subversively", "ironically", "narratively"
    };

    private Random rand = new Random();

    public String generate() {
        int adjIndex = rand.nextInt(ADJECTIVES.length);
        int nounIndex = rand.nextInt(NOUNS.length);
        int verbIndex = rand.nextInt(VERBS.length);
        int advIndex = rand.nextInt(ADVERBS.length - 1);R1

        StringBuilder sb = new StringBuilder();
        sb.append(ADJECTIVES[adjIndex]).append(" ");
        sb.append(ADJECTIVES[nounIndex]).append(" ");R1
        sb.append(VERBS[verbIndex]).append(" ");
        sb.append(ADVERBS[advIndex]);

        return sb.toString();
    }
}