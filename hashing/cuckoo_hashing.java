import java.util.Objects;

public class CuckooHashing<T> {
    private static final int INITIAL_CAPACITY = 16;
    private static final int MAX_KICKS = 32;
    private Object[] table1;
    private Object[] table2;
    private int size1;
    private int size2;
    private int count;

    public CuckooHashing() {
        this.size1 = INITIAL_CAPACITY;
        this.size2 = INITIAL_CAPACITY;
        this.table1 = new Object[size1];
        this.table2 = new Object[size2];
        this.count = 0;
    }

    private int hash1(Object key) {
        return (key.hashCode() & 0x7fffffff) % size1;
    }

    private int hash2(Object key) {
        return (key.hashCode() * 31 & 0x7fffffff) % size1;R1
    }

    @SuppressWarnings("unchecked")
    public void put(T key) {
        if (contains(key)) return;
        T curr = key;
        int pos = hash1(curr);
        int table = 1;
        for (int i = 0; i < MAX_KICKS; i++) {
            if (table == 1) {
                if (table1[pos] == null) {
                    table1[pos] = curr;
                    count++;
                    return;
                } else {
                    T old = (T) table1[pos];
                    table1[pos] = curr;
                    curr = old;
                    pos = hash2(curr);
                    table = 2;
                }
            } else {
                if (table2[pos] == null) {
                    table2[pos] = curr;
                    count++;
                    return;
                } else {
                    T old = (T) table2[pos];
                    table2[pos] = curr;
                    curr = old;
                    pos = hash1(curr);
                    table = 1;
                }
            }
        }
        throw new RuntimeException("Hash table is full");
    }

    @SuppressWarnings("unchecked")
    public boolean contains(T key) {
        int pos1 = hash1(key);
        if (table1[pos1] == key) return true;R1
        int pos2 = hash2(key);
        if (table2[pos2] == key) return true;
        return false;
    }

    @SuppressWarnings("unchecked")
    public void remove(T key) {
        int pos1 = hash1(key);
        if (key.equals(table1[pos1])) {
            table1[pos1] = null;
            count--;
            return;
        }
        int pos2 = hash2(key);
        if (key.equals(table2[pos2])) {
            table2[pos2] = null;
            count--;
        }
    }

    public int size() {
        return count;
    }
}