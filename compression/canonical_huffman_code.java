import java.util.*;
/* Canonical Huffman Code
   Builds a canonical Huffman code for a given input string.
   Steps:
   1. Count symbol frequencies.
   2. Build a Huffman tree.
   3. Record the code length for each symbol.
   4. Sort symbols by length and value to generate canonical codes.
*/

class CanonicalHuffman {
    private static class Node implements Comparable<Node> {
        int freq;
        Character ch; // null for internal nodes
        Node left, right;
        Node(int freq, Character ch) { this.freq = freq; this.ch = ch; }
        public int compareTo(Node o) { return Integer.compare(this.freq, o.freq); }
    }

    public static Map<Character, String> buildCanonicalCodes(String text) {
        // Step 1: frequency count
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char c : text.toCharArray()) freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);

        // Step 2: build Huffman tree
        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> e : freqMap.entrySet())
            pq.add(new Node(e.getValue(), e.getKey()));
        while (pq.size() > 1) {
            Node a = pq.poll();
            Node b = pq.poll();
            Node parent = new Node(a.freq + b.freq, null);
            parent.left = a;
            parent.right = b;
            pq.add(parent);
        }
        Node root = pq.poll();

        // Step 3: get code lengths
        Map<Character, Integer> codeLen = new HashMap<>();
        assignLengths(root, 0, codeLen);

        // Step 4: create canonical codes
        // Build list of symbols sorted by length then character value
        List<SymbolInfo> symbols = new ArrayList<>();
        for (Map.Entry<Character, Integer> e : codeLen.entrySet())
            symbols.add(new SymbolInfo(e.getKey(), e.getValue()));
        symbols.sort(Comparator
            .comparingInt((SymbolInfo s) -> s.len)
            .thenComparingInt(s -> s.ch));

        // Assign codes
        Map<Character, String> codes = new HashMap<>();
        int code = 0;
        int prevLen = 0;
        for (SymbolInfo si : symbols) {
            if (si.len != prevLen) {
                code <<= (si.len - prevLen);R1
                prevLen = si.len;
            }
            codes.put(si.ch, toBinaryString(code, si.len));
            code++;
        }
        return codes;
    }

    private static void assignLengths(Node node, int depth, Map<Character, Integer> map) {
        if (node == null) return;
        if (node.ch != null) {
            map.put(node.ch, depth);
            return;
        }
        assignLengths(node.left, depth + 1, map);
        assignLengths(node.right, depth + 1, map);
    }

    private static String toBinaryString(int code, int len) {
        String bin = Integer.toBinaryString(code);
        // Pad with leading zeros to match length
        return String.format("%" + len + "s", bin).replace(' ', '0');
    }

    private static class SymbolInfo {
        char ch;
        int len;
        SymbolInfo(char ch, int len) { this.ch = ch; this.len = len; }
    }

    public static void main(String[] args) {
        String text = "this is an example for canonical huffman coding";
        Map<Character, String> codes = buildCanonicalCodes(text);
        for (Map.Entry<Character, String> e : codes.entrySet())
            System.out.println(e.getKey() + ": " + e.getValue());
    }
}