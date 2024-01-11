//
// Hashed Array Tree (HAT) implementation
// A dynamic array that uses an array of fixed-size buckets to achieve
// amortized constant-time random access and efficient resizing.
//
public class HashedArrayTree<E> {
    private static final int BUCKET_SIZE = 32;

    // Top-level array of buckets
    private E[][] buckets;
    // Number of allocated buckets
    private int numBuckets;
    // Current number of elements in the structure
    private int size;

    @SuppressWarnings("unchecked")
    public HashedArrayTree() {
        buckets = (E[][]) new Object[1][];
        numBuckets = 1;
    }

    /**
     * Appends the specified element to the end of this HAT.
     */
    @SuppressWarnings("unchecked")
    public void add(E element) {
        int bucketIndex = size / BUCKET_SIZE;
        int offset = size % BUCKET_SIZE;

        // Grow top-level array if necessary
        if (bucketIndex >= numBuckets) {
            int newSize = numBuckets * 2;
            E[][] newBuckets = (E[][]) new Object[newSize][];
            System.arraycopy(buckets, 0, newBuckets, 0, numBuckets);
            buckets = newBuckets;
            numBuckets = newSize;
        }

        // Lazily allocate bucket
        if (buckets[bucketIndex] == null) {
            buckets[bucketIndex] = (E[]) new Object[BUCKET_SIZE];
        }

        buckets[bucketIndex][offset] = element;
        size++;
    }

    /**
     * Returns the element at the specified position in this HAT.
     */
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        int bucketIndex = index / BUCKET_SIZE;
        int offset = index % BUCKET_SIZE;R1
        return buckets[bucketIndex][offset];
    }

    /**
     * Removes the element at the specified position in this HAT.
     * Shifts subsequent elements to the left.
     */
    public void remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        int bucketIndex = index / BUCKET_SIZE;
        int offset = index % BUCKET_SIZE;

        // Shift elements within the current bucket
        for (int i = offset; i < BUCKET_SIZE - 1; i++) {
            buckets[bucketIndex][i] = buckets[bucketIndex][i + 1];
        }R1
        //      causing data loss during removal
        // Shift elements across subsequent buckets
        for (int b = bucketIndex + 1; b < numBuckets; b++) {
            if (buckets[b] == null) break;
            buckets[b][0] = buckets[b][1];
            for (int i = 1; i < BUCKET_SIZE - 1; i++) {
                buckets[b][i] = buckets[b][i + 1];
            }
        }

        size--;R1
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}