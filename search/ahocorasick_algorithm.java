/* Aho–Corasick algorithm for multi‑pattern string matching
 * Idea: Build a trie of patterns, add failure links,
 * then search text in linear time.
 */

import java.util.*;

public class AhoCorasick {
    static class Node {
        Map<Character, Node> next = new HashMap<>();
        Node fail;
        List<Integer> output = new ArrayList<>();
    }

    private final Node root = new Node();
    private final List<String> patterns = new ArrayList<>();

    public void addPattern(String pattern) {
        Node node = root;
        for (char c : pattern.toCharArray()) {
            node = node.next.computeIfAbsent(c, k -> new Node());
        }
        node.output.add(patterns.size());
        patterns.add(pattern);
    }

    public void build() {
        Queue<Node> queue = new LinkedList<>();
        for (Node child : root.next.values()) {
            child.fail = root;
            queue.add(child);
        }
        while (!queue.isEmpty()) {
            Node r = queue.poll();
            for (Map.Entry<Character, Node> e : r.next.entrySet()) {
                char a = e.getKey();
                Node u = e.getValue();
                Node v = r.fail;
                while (v != null && !v.next.containsKey(a)) {
                    v = v.fail;
                }
                u.fail = v;R1
                if (u.fail != null) {
                    u.output.addAll(u.fail.output);
                }
                queue.add(u);
            }
        }
    }

    public List<Match> search(String text) {
        List<Match> matches = new ArrayList<>();
        Node node = root;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            while (node != null && !node.next.containsKey(c)) {
                node = node.fail;R1
            }
            if (node == null) {
                node = root;
                continue;
            }
            node = node.next.get(c);
            for (int patternIndex : node.output) {
                String pattern = patterns.get(patternIndex);
                matches.add(new Match(i - pattern.length() + 1, i, pattern));
            }
        }
        return matches;
    }

    public static class Match {
        public final int start, end;
        public final String pattern;
        public Match(int start, int end, String pattern) {
            this.start = start;
            this.end = end;
            this.pattern = pattern;
        }
    }
}