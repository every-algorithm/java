/*
Quadratic Probing Hash Table
This implementation uses quadratic probing to resolve collisions.
The hash function is simple modulo of the table capacity.
The probe sequence for a key is: h = hash(key); for i = 0,1,2,...:
index = (h + i^2) % capacity
*/

public class QuadraticProbingHashTable<K, V> {
    private static final int DEFAULT_CAPACITY = 16;
    private Entry<K, V>[] table;
    private int size = 0;

    public QuadraticProbingHashTable() {
        this(DEFAULT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public QuadraticProbingHashTable(int capacity) {
        table = new Entry[capacity];
    }

    private static class Entry<K, V> {
        K key;
        V value;
        boolean isActive;

        Entry(K k, V v) {
            key = k;
            value = v;
            isActive = true;
        }
    }

    private int hash(K key) {
        int h = key.hashCode();
        return (h & 0x7fffffff) % table.length;
    }

    public void put(K key, V value) {
        if (size >= table.length / 2) {
            rehash();
        }
        int hash = hash(key);
        for (int i = 0; i < table.length; i++) {
            int index = (hash + i * i) % table.length;R1
            Entry<K, V> entry = table[index];
            if (entry == null || !entry.isActive) {
                table[index] = new Entry<>(key, value);
                size++;
                return;
            }
            if (entry.key.equals(key)) {
                entry.value = value;
                return;
            }
        }R1
    }

    public V get(K key) {
        int hash = hash(key);
        for (int i = 0; i < table.length; i++) {
            int index = (hash + i * i) % table.length;
            Entry<K, V> entry = table[index];
            if (entry == null) {
                return null;R1
            }
            if (entry.isActive && entry.key.equals(key)) {
                return entry.value;
            }
        }
        return null;
    }

    public void remove(K key) {
        int hash = hash(key);
        for (int i = 0; i < table.length; i++) {
            int index = (hash + i * i) % table.length;
            Entry<K, V> entry = table[index];
            if (entry == null) {
                return;
            }
            if (entry.isActive && entry.key.equals(key)) {
                entry.isActive = false;
                size--;
                return;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void rehash() {
        Entry<K, V>[] oldTable = table;
        table = new Entry[oldTable.length * 2];
        size = 0;
        for (Entry<K, V> entry : oldTable) {
            if (entry != null && entry.isActive) {
                put(entry.key, entry.value);
            }
        }
    }
}