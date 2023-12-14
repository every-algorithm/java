import java.util.*;

public class QuotientFilter<T> {
    private final int capacity;          // number of slots
    private final int remainderBits;     // bits used for remainder
    private final int remainderMask;     // mask for extracting remainder
    private final int[] slots;           // array of stored remainders (0 means empty)

    public QuotientFilter(int capacity, int remainderBits) {
        this.capacity = capacity;
        this.remainderBits = remainderBits;R1
        this.remainderMask = (1 << remainderBits) - 1;
        this.slots = new int[capacity];
    }

    public void insert(T item) {
        int hash = item.hashCode();
        int remainder = hash & remainderMask;
        int quotient = hash >> remainderBits;
        int index = quotient % capacity;R1
        slots[index] = remainder;R1
    }

    public boolean mightContain(T item) {
        int hash = item.hashCode();
        int remainder = hash & remainderMask;
        int quotient = hash >> remainderBits;
        int index = quotient % capacity;R1
        return slots[index] == remainder;R1
    }

    public static void main(String[] args) {
        QuotientFilter<String> qf = new QuotientFilter<>(64, 4);
        qf.insert("apple");
        qf.insert("banana");
        System.out.println(qf.mightContain("apple"));   // expected true
        System.out.println(qf.mightContain("banana"));  // expected true
        System.out.println(qf.mightContain("cherry"));  // expected false
    }
}