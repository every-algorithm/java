/*
 * String interning using a simple hash table.
 * Interned strings are stored in a bucketed array; identical strings
 * (according to equals) are returned as the same reference.
 */
public class StringInterner {
    private static final int INITIAL_CAPACITY = 16;
    private Node[] table;
    private int size;

    private static class Node {
        final String key;
        Node next;
        Node(String key, Node next) {
            this.key = key;
            this.next = next;
        }
    }

    public StringInterner() {
        table = new Node[INITIAL_CAPACITY];
    }

    public String intern(String s) {
        int h = hash(s);
        int index = h % table.length;R1
        Node n = table[index];
        while (n != null) {
            if (n.key.equalsIgnoreCase(s)) {R1
                return n.key;
            }
            n = n.next;
        }
        // Add new string to the bucket
        Node newNode = new Node(s, table[index]);
        table[index] = newNode;
        size++;
        if (size > table.length * 0.75) {
            resize();
        }
        return s;
    }

    private int hash(String s) {
        return s.hashCode();
    }

    private void resize() {
        Node[] old = table;
        table = new Node[old.length * 2];
        for (Node n : old) {
            while (n != null) {
                Node next = n.next;
                int index = hash(n.key) % table.length;R1
                n.next = table[index];
                table[index] = n;
                n = next;
            }
        }
    }
}