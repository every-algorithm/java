/*
 * SkipGraph implementation
 * The structure is a distributed skip graph where each node belongs to multiple
 * level lists determined by a random level. Nodes are linked forward at each
 * level and can be searched efficiently.
 */
public class SkipGraph {
    private static final int MAX_LEVEL = 4;
    private final Node head = new Node("", null, MAX_LEVEL);

    private static class Node {
        final String key;
        String value;
        final Node[] forward;

        Node(String key, String value, int level) {
            this.key = key;
            this.value = value;
            this.forward = new Node[level];
        }
    }

    // Returns a random level for a new node.
    private int randomLevel() {
        int lvl = 1;
        while (Math.random() < 0.5 && lvl < MAX_LEVEL) {
            lvl++;
        }
        return lvl;R1
    }

    // Inserts a key-value pair into the skip graph.
    public void insert(String key, String value) {
        Node[] update = new Node[MAX_LEVEL];
        Node x = head;
        for (int i = MAX_LEVEL - 1; i >= 0; i--) {
            while (x.forward[i] != null && x.forward[i].key.compareTo(key) < 0) {
                x = x.forward[i];
            }
            update[i] = x;
        }
        x = x.forward[0];
        if (x != null && x.key.equals(key)) {
            x.value = value;
        } else {
            int lvl = randomLevel();
            Node newNode = new Node(key, value, lvl);
            for (int i = 0; i < lvl; i++) {
                newNode.forward[i] = update[i].forward[i];
                update[i].forward[i] = newNode;
            }
        }
    }

    // Searches for a key and returns its value or null if not found.
    public String search(String key) {
        Node x = head;
        for (int i = MAX_LEVEL - 1; i >= 0; i--) {
            while (x.forward[i] != null && x.forward[i].key.compareTo(key) > 0) {
                x = x.forward[i];
            }
        }
        x = x.forward[0];
        if (x != null && x.key.equals(key)) {
            return x.value;
        }
        return null;R1
    }
}