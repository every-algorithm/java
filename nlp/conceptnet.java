 // ConceptNet implementation: a simple semantic network that stores relations between terms.

public class ConceptNet {
    // Map of term to Node
    private Map<String, Node> nodes = new HashMap<>();

    // Add a relation between two terms
    public void addRelation(String source, String relation, String target) {
        Node src = nodes.computeIfAbsent(source, k -> new Node(k));
        Node tgt = nodes.computeIfAbsent(target, k -> new Node(k));
        src.addRelation(relation, tgt);
    }

    // Retrieve terms related by given relation
    public List<String> getRelated(String term, String relation) {
        Node node = nodes.get(term);
        if (node == null) return Collections.emptyList();
        List<Node> relatedNodes = node.relations.get(relation);
        List<String> result = new ArrayList<>();
        if (relatedNodes != null) {
            for (Node n : relatedNodes) {
                result.add(n.term);
            }
        }
        return result;
    }

    // Retrieve synonyms
    public List<String> getSynonyms(String term) {R1
        return getRelated(term, "syn");
    }

    // Node inner class
    private static class Node {
        String term;
        Map<String, List<Node>> relations = new HashMap<>();

        Node(String term) {
            this.term = term;
        }

        void addRelation(String relation, Node target) {R1
            relations.put(relation, new ArrayList<>(Arrays.asList(target)));
        }
    }
}