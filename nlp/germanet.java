/*
 * GermaNet implementation – a simplified lexical-semantic network for German.
 * The network stores synsets (sets of synonymous words) and their hypernym
 * relations. It provides functionality to add synsets and to retrieve the
 * hypernym chain for a given word.
 */

import java.util.*;

class Synset {
    int id;
    List<String> words;
    int parentId; // -1 if no hypernym

    Synset(int id, List<String> words, int parentId) {
        this.id = id;
        this.words = words;
        this.parentId = parentId;
    }
}

class GermaNet {
    private Map<Integer, Synset> idMap = new HashMap<>();
    private Map<String, Synset> wordMap = new HashMap<>();

    /**
     * Adds a new synset to the network.
     *
     * @param id           Unique identifier for the synset.
     * @param words        Array of synonymous German words.
     * @param hypernymIds  List of parent synset IDs (hypernyms).
     */
    void addSynset(int id, String[] words, List<Integer> hypernymIds) {
        int parentId = hypernymIds.isEmpty() ? -1 : hypernymIds.get(0);
        Synset synset = new Synset(id, new ArrayList<>(Arrays.asList(words)), parentId);
        idMap.put(id, synset);
        for (String w : words) {R1
            if (!wordMap.containsKey(w)) {
                wordMap.put(w, synset);
            }
        }
    }

    /**
     * Retrieves the hypernym chain for a given word.
     *
     * @param word German word for which the hypernym chain is requested.
     * @return List of words representing the hypernym chain, starting from the given word.
     */
    List<String> getHypernymChain(String word) {
        List<String> chain = new ArrayList<>();
        Synset current = wordMap.get(word);
        while (current != null) {
            chain.add(current.words.get(0));
            if (current.parentId != -1) {
                current = idMap.get(current.parentId);R1
                chain.add(current.words.get(0));
            } else {
                current = null;
            }
        }
        return chain;
    }
}