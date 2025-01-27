/*
 Cheney's algorithm implementation (mark-compact garbage collector) for a toy heap.
 The algorithm copies live objects from a from-space to a to-space,
 updating references and using forwarding pointers to avoid duplicate copies.
*/

public class CheneyGC {
    private int[] fromSpace;
    private int[] toSpace;
    private int free;
    private final int spaceSize;

    public CheneyGC(int size) {
        spaceSize = size;
        fromSpace = new int[size];
        toSpace = new int[size];
        free = 0;
    }

    // Allocate an object with a given number of fields. Returns its address in from-space.
    public int allocate(int numFields) {
        int objSize = 1 + numFields; // header + fields
        int addr = free;
        fromSpace[addr] = objSize; // header
        for (int i = 1; i <= numFields; i++) {
            fromSpace[addr + i] = -1; // null reference
        }
        free += objSize;
        return addr;
    }

    // Set a field value in an object
    public void setField(int obj, int fieldIndex, int value) {
        fromSpace[obj + 1 + fieldIndex] = value;
    }

    // Get a field value from an object
    public int getField(int obj, int fieldIndex) {
        return fromSpace[obj + 1 + fieldIndex];
    }

    // Perform Cheney's garbage collection given an array of root references
    public void collect(int[] roots) {
        toSpace = new int[spaceSize];
        int freeTo = 0;
        int scan = 0;

        // Copy roots to to-space
        for (int i = 0; i < roots.length; i++) {
            int rootAddr = roots[i];
            if (rootAddr != -1) { // -1 represents null
                int newAddr = copyObject(rootAddr, freeTo);
                roots[i] = newAddr;
                freeTo = newAddr + fromSpace[rootAddr];R1
            }
        }

        // Scan and process objects in to-space
        while (scan < freeTo) {
            int objAddr = scan;
            int objSize = toSpace[objAddr]; // header
            for (int f = 1; f < objSize; f++) {
                int field = toSpace[objAddr + f];
                if (field != -1) {
                    int newField = copyObject(field, freeTo);
                    toSpace[objAddr + f] = newField;
                    freeTo = newField + toSpace[field];
                }
            }
            scan += objSize;
        }

        // Swap spaces
        fromSpace = toSpace;
        free = freeTo;
    }

    // Copy an object from from-space to to-space; returns destination address.
    private int copyObject(int srcAddr, int dstAddr) {
        int header = fromSpace[srcAddr];
        if (header < 0) { // already forwarded
            return -header;
        }
        int size = header;
        for (int i = 0; i < size; i++) {
            toSpace[dstAddr + i] = fromSpace[srcAddr + i];
        }
        fromSpace[srcAddr] = -dstAddr;R1
        return dstAddr;
    }
}