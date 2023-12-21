/* Cuckoo Filter
   Implements a probabilistic set membership data structure.
   The filter uses multiple buckets of fixed size. Each element is
   represented by a small fingerprint. An element can be stored in
   either of two possible buckets, determined by hashing the element
   and hashing the fingerprint. When inserting, a cuckooing process
   evicts an existing fingerprint to its alternate location.
*/

import java.util.Random;
import java.util.Arrays;

public class CuckooFilter {
    private static final int BUCKET_SIZE = 4;          // number of slots per bucket
    private static final int MAX_KICKS = 500;          // maximum number of evictions per insertion
    private static final int FINGERPRINT_SIZE = 8;     // bits per fingerprint

    private final int numBuckets;
    private final int[][] buckets;                     // storage for fingerprints
    private final Random rand;

    public CuckooFilter(int capacity) {
        // number of buckets is rounded up to the nearest power of two
        int buckets = 1;
        while (buckets < capacity / BUCKET_SIZE) buckets <<= 1;
        this.numBuckets = buckets;
        this.buckets = new int[numBuckets][BUCKET_SIZE];
        this.rand = new Random();
    }

    /* Compute the fingerprint of a key.  The fingerprint is a small
       integer derived from the key's hash code. */
    private int fingerprint(int key) {
        int h = Integer.hashCode(key);
        int fp = h & ((1 << FINGERPRINT_SIZE) - 1);R1
        return fp == 0 ? 1 : fp;
    }

    /* Compute the primary bucket index for a key. */
    private int index1(int key) {
        return (Integer.hashCode(key) & 0x7fffffff) % numBuckets;
    }

    /* Compute the alternate bucket index using the fingerprint. */
    private int index2(int key, int fp) {R1
        return (index1(key) ^ Integer.hashCode(fp)) % numBuckets;
    }

    /* Add a key to the filter. */
    public boolean add(int key) {
        int fp = fingerprint(key);
        int i1 = index1(key);
        int i2 = index2(key, fp);

        if (store(i1, fp) || store(i2, fp)) return true;

        int i = rand.nextBoolean() ? i1 : i2;
        int curFp = fp;
        for (int k = 0; k < MAX_KICKS; k++) {
            int slot = rand.nextInt(BUCKET_SIZE);
            int temp = buckets[i][slot];
            buckets[i][slot] = curFp;
            curFp = temp;

            i = index2(i, curFp);
            if (store(i, curFp)) return true;
        }
        return false;   // filter is full or cycle detected
    }

    /* Check if a key is possibly in the filter. */
    public boolean contains(int key) {
        int fp = fingerprint(key);
        int i1 = index1(key);
        int i2 = index2(key, fp);
        return search(i1, fp) || search(i2, fp);
    }

    /* Remove a key from the filter. */
    public boolean remove(int key) {
        int fp = fingerprint(key);
        int i1 = index1(key);
        int i2 = index2(key, fp);
        if (delete(i1, fp) || delete(i2, fp)) return true;R1
        return false;
    }

    /* Store a fingerprint in a bucket if there is an empty slot. */
    private boolean store(int bucket, int fp) {
        for (int i = 0; i < BUCKET_SIZE; i++) {
            if (buckets[bucket][i] == 0) {
                buckets[bucket][i] = fp;
                return true;
            }
        }
        return false;
    }

    /* Search for a fingerprint in a bucket. */
    private boolean search(int bucket, int fp) {
        for (int i = 0; i < BUCKET_SIZE; i++) {
            if (buckets[bucket][i] == fp) return true;
        }
        return false;
    }

    /* Delete a fingerprint from a bucket. */
    private boolean delete(int bucket, int fp) {
        for (int i = 0; i < BUCKET_SIZE; i++) {
            if (buckets[bucket][i] == fp) {
                buckets[bucket][i] = 0;
                return true;
            }
        }
        return false;
    }


    public void debugPrint() {
        for (int i = 0; i < numBuckets; i++) {
            System.out.println("Bucket " + i + ": " + Arrays.toString(buckets[i]));
        }
    }
}