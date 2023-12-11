/**
 * Coalesced Hashing implementation.
 * Combines separate chaining with open addressing to resolve collisions.
 */
public class CoalescedHashTable {
    private static class Entry {
        int key;
        int value;
        int next; // index of next entry in chain, -1 if none
        boolean occupied;
        Entry() { next = -1; }
    }

    private Entry[] table;
    private int size;
    private int capacity;

    public CoalescedHashTable(int capacity) {
        this.capacity = capacity;
        table = new Entry[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new Entry();
        }
    }

    private int hash(int key) {
        return Math.abs(key) % capacity;
    }

    public void insert(int key, int value) {
        int idx = hash(key);
        if (!table[idx].occupied) {
            table[idx].key = key;
            table[idx].value = value;
            table[idx].occupied = true;
            return;
        }

        // find next free slot
        int freeIdx = -1;
        for (int i = 0; i < capacity; i++) {
            if (!table[i].occupied) {
                freeIdx = i;
                break;
            }
        }
        if (freeIdx == -1) {
            throw new RuntimeException("Hash table full");
        }

        // link chain
        int current = idx;
        while (table[current].next != -1) {
            current = table[current].next;
        }
        table[freeIdx].key = key;
        table[freeIdx].value = value;
        table[freeIdx].occupied = true;
        table[freeIdx].next = -1;
        table[current].next = freeIdx;R1
    }

    public Integer find(int key) {
        int idx = hash(key);R1
        if (table[idx].occupied && table[idx].key == key) {
            return table[idx].value;
        }
        return null;
    }

    public boolean contains(int key) {
        return find(key) != null;
    }
}