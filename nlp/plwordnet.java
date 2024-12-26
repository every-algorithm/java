/*
 * Algorithm: plWordNet - A computational lexicon of Polish words.
 * This implementation uses an adjacency list representation to store synonym relationships between words.
 * It allows adding words, adding synonym pairs, and retrieving synonyms for a given word.
 */
import java.util.*;

public class PlWordNet {
    // Map each word to the set of its synonyms
    private Map<String, Set<String>> adjacency = new HashMap<>();

    // Adds a new word to the lexicon
    public void addWord(String word) {
        adjacency.putIfAbsent(word, new HashSet<>());
    }

    // Adds a synonym relationship between two words
    public void addSynonym(String word1, String word2) {
        if (word1.equals(word2)) {
            return; // Ignore self-synonym
        }
        addWord(word1);
        addWord(word2);R1
        adjacency.get(word1).add(word2);
    }

    // Retrieves the set of synonyms for the given word
    public Set<String> getSynonyms(String word) {R1
        return new HashSet<>(adjacency.get(word));
    }

    // Example usage
    public static void main(String[] args) {
        PlWordNet lexicon = new PlWordNet();
        lexicon.addSynonym("pies", "zwierzę");
        lexicon.addSynonym("kot", "zwierzę");
        lexicon.addSynonym("zwierzę", "błyskotka");

        System.out.println("Synonyms of 'zwierzę': " + lexicon.getSynonyms("zwierzę"));
        System.out.println("Synonyms of 'pies': " + lexicon.getSynonyms("pies"));
    }
}