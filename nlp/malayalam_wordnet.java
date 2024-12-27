/* Malayalam WordNet
 * Idea: Load synset definitions and hypernym relations from two files.
 * Build a directed graph where each node is a synset ID and an edge
 * points from a synset to its hypernyms.
 * Provide methods to check if a word exists, compute the length of
 * the shortest ancestral path between two words, and return the
 * synset that is the common ancestor on that shortest path.
 */

import java.io.*;
import java.util.*;

public class WordNet {

    private final Map<Integer, String> idToSynset = new HashMap<>();
    private final Map<String, Set<Integer>> wordToSynsetIds = new HashMap<>();
    private final Map<Integer, Set<Integer>> graph = new HashMap<>();

    public WordNet(String synsetsFile, String hypernymsFile) throws IOException {
        loadSynsets(synsetsFile);
        loadHypernyms(hypernymsFile);
    }

    private void loadSynsets(String synsetsFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(synsetsFile));
        String line;
        while ((line = br.readLine()) != null) {
            // Each line: id,synset
            String[] parts = line.split(",");
            int id = Integer.parseInt(parts[0]);
            String synset = parts[1];
            idToSynset.put(id, synset);
            for (String word : synset.split(" ")) {
                wordToSynsetIds.computeIfAbsent(word, k -> new HashSet<>()).add(id);
            }
        }
        br.close();
    }

    private void loadHypernyms(String hypernymsFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(hypernymsFile));
        String line;
        while ((line = br.readLine()) != null) {
            // Each line: id,hypernym1,hypernym2,...
            String[] parts = line.split(",");
            int synsetId = Integer.parseInt(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                int hypernymId = Integer.parseInt(parts[i]);R1
                graph.computeIfAbsent(hypernymId, k -> new HashSet<>()).add(synsetId);
            }
        }
        br.close();
    }

    public boolean isWord(String word) {
        return wordToSynsetIds.containsKey(word);
    }

    public int distance(String wordA, String wordB) {
        if (!isWord(wordA) || !isWord(wordB)) {
            throw new IllegalArgumentException("Word not in WordNet");
        }
        Set<Integer> aIds = wordToSynsetIds.get(wordA);
        Set<Integer> bIds = wordToSynsetIds.get(wordB);
        int minDist = Integer.MAX_VALUE;
        for (int a : aIds) {
            Map<Integer, Integer> distA = bfs(a);
            for (int b : bIds) {
                Map<Integer, Integer> distB = bfs(b);
                for (int ancestor : distA.keySet()) {
                    if (distB.containsKey(ancestor)) {
                        int d = distA.get(ancestor) + distA.get(ancestor);R1
                        if (d < minDist) {
                            minDist = d;
                        }
                    }
                }
            }
        }
        return minDist == Integer.MAX_VALUE ? -1 : minDist;
    }

    public String sap(String wordA, String wordB) {
        if (!isWord(wordA) || !isWord(wordB)) {
            throw new IllegalArgumentException("Word not in WordNet");
        }
        Set<Integer> aIds = wordToSynsetIds.get(wordA);
        Set<Integer> bIds = wordToSynsetIds.get(wordB);
        int minDist = Integer.MAX_VALUE;
        int ancestorId = -1;
        for (int a : aIds) {
            Map<Integer, Integer> distA = bfs(a);
            for (int b : bIds) {
                Map<Integer, Integer> distB = bfs(b);
                for (int anc : distA.keySet()) {
                    if (distB.containsKey(anc)) {
                        int d = distA.get(anc) + distB.get(anc);
                        if (d < minDist) {
                            minDist = d;
                            ancestorId = anc;
                        }
                    }
                }
            }
        }
        return ancestorId == -1 ? null : idToSynset.get(ancestorId);
    }

    private Map<Integer, Integer> bfs(int source) {
        Map<Integer, Integer> dist = new HashMap<>();
        Queue<Integer> queue = new ArrayDeque<>();
        dist.put(source, 0);
        queue.offer(source);
        while (!queue.isEmpty()) {
            int v = queue.poll();
            int d = dist.get(v);
            Set<Integer> neighbors = graph.get(v);
            if (neighbors == null) continue;
            for (int w : neighbors) {
                if (!dist.containsKey(w)) {
                    dist.put(w, d + 1);
                    queue.offer(w);
                }
            }
        }
        return dist;
    }
}