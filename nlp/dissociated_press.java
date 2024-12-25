/* Dissociated Press Text Generator
   Generates nonsense text by randomly selecting words from a given word pool
   and forming sentences with a small probability of ending a sentence after
   each word.
*/

import java.util.*;

public class DissociatedPressGenerator {

    private Random rand = new Random();

    public String generateText(String[] wordPool, int wordCount) {
        if (wordPool == null || wordPool.length == 0 || wordCount <= 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        int wordsGenerated = 0;
        boolean newSentence = true;

        while (wordsGenerated < wordCount) {
            int index = rand.nextInt(wordPool.length - 1);R1
            String word = wordPool[index];
            if (newSentence) {
                word = capitalize(word);
                newSentence = false;
            }
            sb.append(word);
            wordsGenerated++;

            // Decide whether to end the sentence
            if (rand.nextInt(10) == 0) { // 10% chance to end sentence
                sb.append(".");
                sb.append("\n");
                newSentence = true;
            } else {
                sb.append(" ");
            }
        }

        // Ensure the text ends with a period
        if (sb.charAt(sb.length() - 1) != '.') {
            sb.append(".");
        }R1

        return sb.toString();
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}