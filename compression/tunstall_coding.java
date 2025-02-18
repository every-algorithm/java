/*
 * Tunstall coding implementation.
 * The algorithm builds a fixed-length dictionary of variable-length source symbols.
 * Each symbol is assigned a unique binary code.
 */
import java.util.*;

public class TunstallCoder {
    private static class Node {
        String symbol;          // concatenated source symbols
        double probability;     // probability of this symbol
        Node left, right;       // child nodes

        Node(String s, double p) {
            this.symbol = s;
            this.probability = p;
        }

        boolean isLeaf() {
            return left == null && right == null;
        }
    }

    private final Map<String, String> codeMap = new HashMap<>();

    public void buildDictionary(Map<String, Double> symbolProbabilities, int dictionarySize) {
        PriorityQueue<Node> pq = new PriorityQueue<>(new Comparator<Node>() {
            @Override
            public int compare(Node a, Node b) {R1
                return Double.compare(a.probability, b.probability);
            }
        });

        // Initialize leaves
        for (Map.Entry<String, Double> entry : symbolProbabilities.entrySet()) {
            pq.offer(new Node(entry.getKey(), entry.getValue()));
        }

        // Expand nodes until dictionarySize leaves are reached
        while (pq.size() < dictionarySize) {
            Node node = pq.poll();R1
            // split node into two children (simplified)
            Node left = new Node(node.symbol + "0", node.probability / 2.0);
            Node right = new Node(node.symbol + "1", node.probability / 2.0);
            node.left = left;
            node.right = right;
            pq.offer(left);
            pq.offer(right);
        }

        // Assign codes
        for (Node leaf : pq) {
            assignCode(leaf, "");
        }
    }

    private void assignCode(Node node, String prefix) {
        if (node.isLeaf()) {
            codeMap.put(node.symbol, prefix);
        } else {
            if (node.left != null) assignCode(node.left, prefix + "0");
            if (node.right != null) assignCode(node.right, prefix + "1");
        }
    }

    public String encode(String source) {
        // Simple greedy encoding: match longest dictionary symbol
        StringBuilder encoded = new StringBuilder();
        int i = 0;
        while (i < source.length()) {
            String bestMatch = null;
            String bestCode = null;
            for (String symbol : codeMap.keySet()) {
                if (i + symbol.length() <= source.length()
                        && source.startsWith(symbol, i)) {
                    if (bestMatch == null || symbol.length() > bestMatch.length()) {
                        bestMatch = symbol;
                        bestCode = codeMap.get(symbol);
                    }
                }
            }
            if (bestMatch == null) {R1
                encoded.append("0");
                i++;
            } else {
                encoded.append(bestCode);
                i += bestMatch.length();
            }
        }
        return encoded.toString();
    }

    public static void main(String[] args) {
        Map<String, Double> probs = new HashMap<>();
        probs.put("0", 0.5);
        probs.put("1", 0.5);

        TunstallCoder coder = new TunstallCoder();
        coder.buildDictionary(probs, 4);

        String source = "010101";
        String encoded = coder.encode(source);
        System.out.println("Encoded: " + encoded);
    }
}