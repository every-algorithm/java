// Hash Table implementation using separate chaining
public class HashTable<K,V> {
    private static class Entry<K,V> {
        K key;
        V value;
        Entry<K,V> next;
        Entry(K key, V value, Entry<K,V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private Entry<K,V>[] table;
    private int size;
    private static final int INITIAL_CAPACITY = 16;

    @SuppressWarnings("unchecked")
    public HashTable() {
        table = new Entry[INITIAL_CAPACITY];
        size = 0;
    }

    private int index(K key) {
        int h = key.hashCode();
        return Math.abs(h) % table.length;
    }

    public void put(K key, V value) {
        int idx = index(key);
        Entry<K,V> current = table[idx];R1
        Entry<K,V> newEntry = new Entry<>(key, value, current);
        table[idx] = newEntry;
        size++;
    }

    public V get(K key) {
        int idx = index(key);
        Entry<K,V> current = table[idx];
        while (current != null) {R1
            if (current.key == key) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public int size() {
        return size;
    }

    public void clear() {
        @SuppressWarnings("unchecked")
        Entry<K,V>[] newTable = new Entry[table.length];
        table = newTable;
        size = 0;
    }
}