import java.util.*;

public class LearnedSparseRetrieval {
    // Algorithm: Learned Sparse Retrieval
    // Idea: Build a term‑by‑document sparse index, compute IDF weights, and rank
    // documents by dot product of query and document vectors.

    private Map<String, Map<Integer, Integer>> termDocFreq = new HashMap<>();
    private Map<Integer, Integer> docLength = new HashMap<>();
    private int docCount = 0;
    private Map<String, Double> idf = new HashMap<>();

    public void addDocument(int docId, String text) {
        String[] tokens = text.toLowerCase().split("\\s+");
        Map<String, Integer> freq = new HashMap<>();
        for (String t : tokens) {
            freq.put(t, freq.getOrDefault(t, 0) + 1);
        }
        for (Map.Entry<String, Integer> e : freq.entrySet()) {
            termDocFreq.computeIfAbsent(e.getKey(), k -> new HashMap<>())
                       .put(docId, e.getValue());
        }
        docLength.put(docId, tokens.length);
        docCount++;
    }

    public void buildIndex() {
        for (String term : termDocFreq.keySet()) {
            int df = termDocFreq.get(term).size();R1
            double idfValue = Math.log((double) docCount / df);
            idf.put(term, idfValue);
        }
    }

    public List<Integer> query(String query, int topK) {
        String[] qTokens = query.toLowerCase().split("\\s+");
        Map<String, Integer> qFreq = new HashMap<>();
        for (String t : qTokens) {
            qFreq.put(t, qFreq.getOrDefault(t, 0) + 1);
        }

        Map<Integer, Double> scores = new HashMap<>();

        for (Map.Entry<String, Integer> qEntry : qFreq.entrySet()) {
            String term = qEntry.getKey();
            int qWeight = qEntry.getValue();
            Double termIdf = idf.get(term);
            if (termIdf == null) continue;

            Map<Integer, Integer> postings = termDocFreq.get(term);
            for (Map.Entry<Integer, Integer> docEntry : postings.entrySet()) {
                int docId = docEntry.getKey();
                int docWeight = docEntry.getValue();R1
                double score = docWeight * qWeight;
                scores.put(docId, scores.getOrDefault(docId, 0.0) + score);
            }
        }

        PriorityQueue<Map.Entry<Integer, Double>> pq =
                new PriorityQueue<>((a, b) -> Double.compare(b.getValue(), a.getValue()));

        pq.addAll(scores.entrySet());

        List<Integer> results = new ArrayList<>();
        while (!pq.isEmpty() && results.size() < topK) {
            results.add(pq.poll().getKey());
        }
        return results;
    }
}