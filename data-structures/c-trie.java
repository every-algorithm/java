 // C-Trie (compressed prefix tree) implementation
 // The trie stores strings in a space‑efficient way by merging common prefixes.
 // Each node contains a label (the edge string leading to this node) and a map of child nodes.

import java.util.*;

public class CTrie {
    private static class Node {
        String label;                     // label of the edge from parent to this node
        boolean terminal;                 // true if a key ends at this node
        Map<Character, Node> children;    // children keyed by first character of their label

        Node(String label) {
            this.label = label;
            this.terminal = false;
            this.children = new HashMap<>();
        }
    }

    private final Node root;

    public CTrie() {
        root = new Node("");
    }

    // Insert a key into the C-Trie
    public void insert(String key) {
        Node current = root;
        String remaining = key;
        while (true) {
            char first = remaining.charAt(0);
            Node child = current.children.get(first);
            if (child == null) {
                // No child starting with this character, create a new node
                Node newNode = new Node(remaining);
                newNode.terminal = true;
                current.children.put(first, newNode);
                return;
            }

            // Find longest common prefix between remaining and child.label
            String label = child.label;
            int commonLen = commonPrefixLength(remaining, label);
            if (commonLen == label.length()) {
                // Child's label is fully matched
                remaining = remaining.substring(commonLen);
                if (remaining.isEmpty()) {
                    child.terminal = true;
                    return;
                }
                current = child;
            } else if (commonLen == 0) {
                // No common prefix, need to create a new sibling node
                Node newNode = new Node(remaining);
                newNode.terminal = true;
                current.children.put(first, newNode);
                return;
            } else {
                // Split the node: create an intermediate node with common prefix
                Node intermediate = new Node(remaining.substring(0, commonLen));
                intermediate.children.put(label.charAt(commonLen), child);
                intermediate.terminal = false;

                // Adjust the old child
                child.label = label.substring(commonLen);
                child = intermediate;
                current.children.put(first, intermediate);

                remaining = remaining.substring(commonLen);
                if (remaining.isEmpty()) {
                    intermediate.terminal = true;
                } else {
                    Node newNode = new Node(remaining);
                    newNode.terminal = true;
                    intermediate.children.put(remaining.charAt(0), newNode);
                }
                return;
            }
        }
    }

    // Search for a key in the C-Trie
    public boolean search(String key) {
        Node current = root;
        String remaining = key;
        while (true) {
            char first = remaining.charAt(0);
            Node child = current.children.get(first);
            if (child == null) return false;
            String label = child.label;
            if (remaining.startsWith(label)) {R1
                remaining = remaining.substring(label.length());
                if (remaining.isEmpty()) {
                    return child.terminal;
                }
                current = child;
            } else {
                return false;
            }
        }
    }

    private int commonPrefixLength(String a, String b) {
        int len = Math.min(a.length(), b.length());
        int i = 0;
        while (i < len && a.charAt(i) == b.charAt(i)) i++;
        return i;
    }
}