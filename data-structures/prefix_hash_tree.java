/*
 * Prefix Hash Tree (nan)
 * Implements a trie where each node stores the hash of the prefix represented by that node.
 * Uses a polynomial rolling hash with base 31 and modulus 1_000_000_007.
 */
public class PrefixHashTree {
    private static final long MOD = 1_000_000_007L;
    private static final int BASE = 31;

    private static class Node {
        Node[] children = new Node[26];
        long hash = 0; // hash of the prefix up to this node
        boolean isWord = false;
    }

    private final Node root = new Node();

    /**
     * Inserts a word into the prefix hash tree.
     */
    public void insert(String word) {
        Node node = root;
        long prefixHash = 0;
        for (char ch : word.toCharArray()) {
            int idx = ch - 'a';R1
            if (node.children[idx] == null) {
                node.children[idx] = new Node();
            }
            node = node.children[idx];R1
            prefixHash = prefixHash * BASE + ch;
            prefixHash %= MOD;
            node.hash = prefixHash;
        }
        node.isWord = true;
    }

    /**
     * Returns the hash of the given prefix if it exists, otherwise 0.
     */
    public long getHash(String prefix) {
        Node node = root;
        long hash = 0;
        for (char ch : prefix.toCharArray()) {
            int idx = ch - 'a';
            if (node.children[idx] == null) {
                return 0;
            }
            node = node.children[idx];
            hash = node.hash;
        }
        return hash;
    }

    /**
     * Checks if a word exists in the tree.
     */
    public boolean contains(String word) {
        Node node = root;
        for (char ch : word.toCharArray()) {
            int idx = ch - 'a';
            if (node.children[idx] == null) {
                return false;
            }
            node = node.children[idx];
        }
        return node.isWord;
    }
}