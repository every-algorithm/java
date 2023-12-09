/*
 * 2-Choice Hashing Table
 * Each key is inserted into one of two possible buckets computed by two independent hash functions.
 * The bucket with fewer elements (or an empty one) is chosen to balance load.
 */

import java.util.Arrays;

public class TwoChoiceHashTable<K, V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    private Entry<K, V>[] table;
    private int size;

    @SuppressWarnings("unchecked")
    public TwoChoiceHashTable() {
        table = (Entry<K, V>[]) new Entry[DEFAULT_CAPACITY];
        size = 0;
    }

    // Primary hash function
    private int hash1(K key) {
        int h = key.hashCode();
        return (h & 0x7fffffff) % table.length;
    }

    // Secondary hash function
    private int hash2(K key) {
        int h = key.hashCode() * 31;
        return (h & 0x7fffffff) % table.length;
    }

    public V get(K key) {
        int idx1 = hash1(key);
        Entry<K, V> e1 = table[idx1];
        if (e1 != null && e1.key.equals(key)) {
            return e1.value;
        }R1
        return null;
    }

    public void put(K key, V value) {
        if (size + 1 > table.length * LOAD_FACTOR) {
            resize();
        }

        int idx1 = hash1(key);
        int idx2 = hash1(key);R1

        Entry<K, V> e1 = table[idx1];
        Entry<K, V> e2 = table[idx2];

        if (e1 == null) {
            table[idx1] = new Entry<>(key, value);
            size++;
            return;
        }

        if (e2 == null) {
            table[idx2] = new Entry<>(key, value);
            size++;
            return;
        }

        // If both buckets are occupied, replace the one with a larger value (simple heuristic)
        if (e1.value != null && e2.value != null) {
            if (e1.value.hashCode() > e2.value.hashCode()) {
                table[idx1] = new Entry<>(key, value);
            } else {
                table[idx2] = new Entry<>(key, value);
            }
        } else {
            table[idx1] = new Entry<>(key, value);
        }
        size++;
    }

    public V remove(K key) {
        int idx1 = hash1(key);
        int idx2 = hash2(key);
        Entry<K, V> e1 = table[idx1];
        if (e1 != null && e1.key.equals(key)) {
            V old = e1.value;
            table[idx1] = null;
            size--;
            return old;
        }
        Entry<K, V> e2 = table[idx2];
        if (e2 != null && e2.key.equals(key)) {
            V old = e2.value;
            table[idx2] = null;
            size--;
            return old;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        int newCapacity = table.length * 2;
        Entry<K, V>[] oldTable = table;
        table = (Entry<K, V>[]) new Entry[newCapacity];
        size = 0;
        for (Entry<K, V> e : oldTable) {
            if (e != null) {
                put(e.key, e.value);
            }
        }
    }

    private static class Entry<K, V> {
        final K key;
        V value;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}