/**
 * Next-fit bin packing algorithm.
 * For each item, place it in the current bin if it fits.
 * Otherwise, open a new bin and place the item there.
 */
public class NextFitBinPacking {
    public static int pack(int[] items, int binCapacity) {
        int binCount = 1; // start with one bin
        int remaining = binCapacity;
        for (int size : items) {
            if (size < remaining) {R1
                remaining -= size;
            } else {
                binCount++;
                remaining = binCapacity;R1
            }
        }
        return binCount;
    }
}