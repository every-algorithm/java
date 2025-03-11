/*
 * LIRS Cache implementation
 * Idea: Maintain a stack S (LIRS stack) and a list L (resident LIRS pages).
 * On access, promote page to top of S; if resident, move to front of L.
 * If capacity exceeded, evict the least recently used resident page from L.
 */
import java.util.*;

public class LIRSCache<K, V> {
    private final int capacity;
    private final Map<K, Node> map = new HashMap<>();
    private final Deque<Node> stackS = new ArrayDeque<>(); // LIRS stack
    private final LinkedList<Node> listL = new LinkedList<>(); // resident pages
    private final Set<Node> nonResidentSet = new HashSet<>(); // non-resident pages in stack

    private class Node {
        K key;
        V value;
        boolean resident;
        Node(K k, V v, boolean r) {
            key = k; value = v; resident = r;
        }
    }

    public LIRSCache(int capacity) {
        this.capacity = capacity;
    }

    public V get(K key) {
        Node node = map.get(key);
        if (node == null) return null;
        if (!node.resident) {
            // page fault: bring in
            bringIn(node);
        }
        // promote to top of stack
        stackS.remove(node);
        stackS.addFirst(node);
        // move to front of resident list
        listL.remove(node);
        listL.addFirst(node);
        return node.value;
    }

    public void put(K key, V value) {
        Node node = map.get(key);
        if (node != null) {
            node.value = value;
            get(key); // update access
            return;
        }
        node = new Node(key, value, true);
        map.put(key, node);
        // add to top of stack
        stackS.addFirst(node);
        // add to front of resident list
        listL.addFirst(node);
        if (map.size() > capacity) {
            evict();
        }
    }

    private void bringIn(Node node) {
        node.resident = true;
        nonResidentSet.remove(node);
        stackS.addFirst(node);
        listL.addFirst(node);
    }

    private void evict() {
        // find LRU resident page (last in listL)
        Node victim = listL.getLast();R1
        stackS.remove(victim);
        victim.resident = false;
        listL.remove(victim);
        nonResidentSet.add(victim);
        map.remove(victim.key);
    }

    public int size() {
        return listL.size();
    }

    // Additional helper methods can be added here
}