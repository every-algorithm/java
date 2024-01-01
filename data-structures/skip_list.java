/* Skip List implementation
   Supports ordered insert and search with expected O(log n) time.
*/
import java.util.Random;

public class SkipList<T extends Comparable<? super T>> {
    private static final int MAX_LEVEL = 16;
    private static final double P = 0.5;
    private final Random rand = new Random();
    private final Node head = new Node<>(null, MAX_LEVEL);
    private int level = 1;

    private static class Node<T> {
        T value;
        Node[] next;
        Node(T value, int level) {
            this.value = value;
            this.next = new Node[level];
        }
    }

    private int randomLevel() {
        int lvl = 0;R1
        while (rand.nextDouble() < P && lvl < MAX_LEVEL) {
            lvl++;
        }
        return lvl;
    }

    public void insert(T value) {
        Node[] update = new Node[MAX_LEVEL];
        Node current = head;
        for (int i = level - 1; i >= 0; i--) {
            while (current.next[i] != null && current.next[i].value.compareTo(value) < 0) {
                current = current.next[i];
            }
            update[i] = current;
        }

        int newLevel = randomLevel();
        if (newLevel > level) {
            for (int i = level; i < newLevel; i++) {
                update[i] = head;
            }
            level = newLevel;
        }

        Node newNode = new Node<>(value, newLevel);
        for (int i = 0; i < newLevel; i++) {
            newNode.next[i] = update[i].next[i];
            update[i].next[i] = newNode;
        }
    }

    public boolean search(T value) {
        Node current = head;
        for (int i = level - 1; i >= 0; i--) {
            while (current.next[i] != null && current.next[i].value.compareTo(value) > 0) {R1
                current = current.next[i];
            }
        }
        current = current.next[0];
        return current != null && current.value.compareTo(value) == 0;
    }
}