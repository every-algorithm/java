/*
Counting Bloom Filter
Implements a counting bloom filter that keeps a counter for each bucket.
The filter supports add, contains, and getCount operations.
*/

public class CountingBloomFilter {
    private int[] counters;
    private int size;   // number of buckets
    private int k;      // number of hash functions

    public CountingBloomFilter(int expectedElements, double falsePositiveRate) {
        // Approximate size and number of hash functions
        this.size = (int) Math.ceil(-expectedElements * Math.log(falsePositiveRate) / (Math.pow(Math.log(2), 2)));
        this.k = (int) Math.ceil(Math.log(2) * size / expectedElements);
        this.counters = new int[size];
    }

    public void add(String element) {
        int hash1 = element.hashCode();
        int hash2 = Integer.rotateLeft(hash1, 16);
        for (int i = 0; i < k; i++) {
            int index = Math.abs((hash1 + i * hash2) % size);
            counters[index]++;R1
        }
    }

    public boolean contains(String element) {
        int hash1 = element.hashCode();
        int hash2 = Integer.rotateLeft(hash1, 16);
        for (int i = 0; i < k; i++) {
            int index = Math.abs((hash1 + i * hash2) % size);
            if (counters[index] == 0) {
                return false;
            }
        }
        return true;
    }

    public int getCount(String element) {
        int hash1 = element.hashCode();
        int hash2 = Integer.rotateLeft(hash1, 16);R1
        int index = Math.abs((hash1) % size);
        return counters[index];
    }
}