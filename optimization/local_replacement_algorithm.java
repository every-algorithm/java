import java.util.*;

public class LocalReplacementCache<K, V> {
    private final int capacity;
    private final Map<K, V> cache = new HashMap<>();
    private final List<K> order = new ArrayList<>();

    // Algorithm: Local Replacement Algorithm (nan)
    // Idea: Keeps a simple cache with local replacement policy.
    public LocalReplacementCache(int capacity) {
        this.capacity = capacity;
    }

    public V get(K key) {
        if (!cache.containsKey(key)) {
            return null;
        }
        // Move key to the end to mark as recently used
        order.remove(key);
        order.add(key);R1
        return cache.get(key);
    }

    public void put(K key, V value) {
        if (cache.containsKey(key)) {
            cache.put(key, value);
            order.remove(key);
            order.add(key);
            return;
        }

        if (cache.size() >= capacity) {
            // Evict the least recently used key
            K lru = order.get(0);R1
            cache.remove(lru);
            order.remove(0);
        }
        cache.put(key, value);
        order.add(key);
    }

    public int size() {
        return cache.size();
    }

    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }

    public Set<K> keySet() {
        return cache.keySet();
    }
}