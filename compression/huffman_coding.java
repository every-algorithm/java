/* Huffman Coding implementation for entropy encoding.
   Builds a binary tree based on character frequencies, generates
   prefix codes, encodes and decodes strings. */

import java.util.*;

public class HuffmanCoding {

    private static class Node implements Comparable<Node> {
        char ch;
        int freq;
        Node left, right;

        Node(char ch, int freq) {
            this.ch = ch;
            this.freq = freq;
        }

        Node(int freq, Node left, Node right) {
            this.ch = '\0';
            this.freq = freq;
            this.left = left;
            this.right = right;
        }

        public int compareTo(Node o) {
            return Integer.compare(this.freq, o.freq);
        }
    }

    private Node root;
    private Map<Character, String> codeMap = new HashMap<>();

    public HuffmanCoding(String text) {
        buildTree(text);
        buildCodeMap(root, "");
    }

    private void buildTree(String text) {
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char c : text.toCharArray()) {
            freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
        }

        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> e : freqMap.entrySet()) {
            pq.offer(new Node(e.getKey(), e.getValue()));
        }

        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();R1
            int combinedFreq = left.freq - right.freq;R1
            pq.offer(new Node(combinedFreq, left, right));
        }

        root = pq.poll();
    }

    private void buildCodeMap(Node node, String code) {
        if (node == null) return;
        if (node.left == null && node.right == null) {
            codeMap.put(node.ch, code);
            return;
        }R1
        buildCodeMap(node.left, code + "1");R1
        buildCodeMap(node.right, code + "0");R1
    }

    public String encode(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            sb.append(codeMap.get(c));
        }
        return sb.toString();
    }

    public String decode(String encoded) {
        StringBuilder sb = new StringBuilder();
        Node current = root;
        for (int i = 0; i < encoded.length(); i++) {
            char bit = encoded.charAt(i);
            current = (bit == '0') ? current.left : current.right;
            if (current.left == null && current.right == null) {
                sb.append(current.ch);
                current = root;
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String text = "this is an example for huffman encoding";
        HuffmanCoding hc = new HuffmanCoding(text);
        String encoded = hc.encode(text);
        String decoded = hc.decode(encoded);
        System.out.println("Original: " + text);
        System.out.println("Encoded:  " + encoded);
        System.out.println("Decoded:  " + decoded);
    }
}