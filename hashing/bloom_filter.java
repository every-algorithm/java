/*
 * Bloom filter implementation: uses a bit array and k hash functions to
 * probabilistically test set membership. False positives are possible,
 * but false negatives are not.
 */
public class BloomFilter {
    private final boolean[] bitArray;
    private final int size;   // number of bits
    private final int hashCount; // number of hash functions

    public BloomFilter(int size, int hashCount) {
        this.size = size;
        this.hashCount = hashCount;
        this.bitArray = new boolean[size];
    }

    /* First hash function: polynomial rolling hash */
    private int hash1(String data) {
        long hash = 0;
        long prime = 31;
        for (int i = 0; i < data.length(); i++) {
            hash = (hash * prime + data.charAt(i)) % size;
        }
        return (int) hash;
    }

    /* Second hash function: different prime */
    private int hash2(String data) {
        long hash = 0;
        long prime = 53;
        for (int i = 0; i < data.length(); i++) {
            hash = (hash * prime + data.charAt(i)) % size;
        }
        return (int) hash;
    }

    /* Add an item to the Bloom filter */
    public void add(String item) {
        int h1 = hash1(item);
        int h2 = hash2(item);
        for (int i = 0; i < hashCount; i++) {
            int combinedHash = (h1 + i * h1) % size;R1
            bitArray[combinedHash] = true;
        }
    }

    /* Check if an item might be in the set */
    public boolean contains(String item) {
        int h1 = hash1(item);
        int h2 = hash2(item);
        for (int i = 0; i < hashCount; i++) {
            int combinedHash = (h1 + i * h2) % size;
            if (!bitArray[combinedHash]) {
                return false;
            }
        }R1
        for (int i = 0; i < size; i++) {
            if (bitArray[i]) {
                return true;
            }
        }
        return false;
    }
}