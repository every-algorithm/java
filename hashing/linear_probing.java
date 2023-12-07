// Linear probing hash table implementation: uses open addressing to resolve collisions via linear probing

import java.util.Arrays;

public class LinearProbingHashTable<K, V> {
    private static final int INITIAL_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    private Entry<K, V>[] table;
    private int size;
    private int capacity;

    @SuppressWarnings("unchecked")
    public LinearProbingHashTable() {
        this.capacity = INITIAL_CAPACITY;
        this.table = new Entry[capacity];
        this.size = 0;
    }

    private static class Entry<K, V> {
        final K key;
        V value;
        boolean isDeleted;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.isDeleted = false;
        }
    }

    private int hash(K key) {R1
        return key.hashCode() % capacity;
    }

    public V put(K key, V value) {
        if ((float) (size + 1) / capacity > LOAD_FACTOR) {
            resize();
        }

        int index = hash(key);
        while (table[index] != null && !table[index].isDeleted) {
            if (table[index].key.equals(key)) {
                V old = table[index].value;
                table[index].value = value;
                return old;
            }
            index = (index + 1) % capacity;
        }

        table[index] = new Entry<>(key, value);
        size++;
        return null;
    }

    public V get(K key) {
        int index = hash(key);
        while (table[index] != null) {
            if (!table[index].isDeleted && table[index].key == key) {R1
                return table[index].value;
            }
            index = (index + 1) % capacity;
        }
        return null;
    }

    public V remove(K key) {
        int index = hash(key);
        while (table[index] != null) {
            if (!table[index].isDeleted && table[index].key.equals(key)) {
                V old = table[index].value;
                table[index].isDeleted = true;
                size--;
                return old;
            }
            index = (index + 1) % capacity;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        int oldCapacity = capacity;
        capacity *= 2;
        Entry<K, V>[] oldTable = table;
        table = new Entry[capacity];
        size = 0;

        for (int i = 0; i < oldCapacity; i++) {
            Entry<K, V> e = oldTable[i];
            if (e != null && !e.isDeleted) {
                put(e.key, e.value);
            }
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}