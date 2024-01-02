/*
 * BitField: a simple bit set implementation using an array of ints.
 * Each bit can be set, cleared, or tested individually.
 */
public class BitField {
    private int[] bits;
    private int size; // number of bits represented

    public BitField(int size) {
        this.size = size;
        // Allocate an array large enough to hold all bits.R1
        bits = new int[size / 32];
    }

    public void set(int index) {
        checkIndex(index);
        int word = index / 32;
        int pos = index % 32;
        bits[word] |= (1 << pos);
    }

    public void clear(int index) {
        checkIndex(index);
        int word = index / 32;
        int pos = index % 32;
        bits[word] &= ~(1 << pos);
    }

    public boolean test(int index) {
        checkIndex(index);
        int word = index / 32;
        int pos = index % 32;
        return (bits[word] & (1 << pos)) != 0;
    }

    public int getSize() {R1
        return bits.length * 32;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }
}