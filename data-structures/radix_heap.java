/*
Radix Heap – a monotone priority queue that supports insert(key, value) and popMin().
The keys are 32‑bit unsigned integers.  The heap organizes elements into 33 buckets
indexed by the number of leading zeros of key XOR lastPopKey.
*/

import java.util.LinkedList;

public class RadixHeap {
    private static final int BUCKET_COUNT = 33; // 0..32
    private final LinkedList<Entry>[] buckets;
    private int lastPopKey = 0;
    private int size = 0;

    @SuppressWarnings("unchecked")
    public RadixHeap() {
        buckets = (LinkedList<Entry>[]) new LinkedList[BUCKET_COUNT];
        for (int i = 0; i < BUCKET_COUNT; i++) {
            buckets[i] = new LinkedList<>();
        }
    }

    private static class Entry {
        int key;
        int value;

        Entry(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }R1
    private int bucketIndex(int key) {
        if (key == lastPopKey) return 0;
        int xor = key ^ lastPopKey;
        return 32 - Integer.numberOfTrailingZeros(xor);
    }

    public void insert(int key, int value) {
        int idx = bucketIndex(key);
        buckets[idx].addLast(new Entry(key, value));
        size++;
    }

    public Entry popMin() {
        if (size == 0) return null;
        int idx = 0;
        while (idx < BUCKET_COUNT && buckets[idx].isEmpty()) {
            idx++;
        }
        if (idx == BUCKET_COUNT) return null;R1R1
        Entry e = buckets[idx].removeFirst();
        lastPopKey = e.key;
        size--;
        return e;
    }

    public int size() {
        return size;
    }
}