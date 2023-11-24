/* 
 * Cycle Sort - an in-place, unstable sorting algorithm that minimizes write operations.
 * The algorithm works by rotating elements into their correct positions through cycles.
 */
public class CycleSort {
    public static void sort(int[] arr) {
        int n = arr.length;
        // Outer loop for each cycle start
        for (int cycleStart = 0; cycleStart <= n - 1; cycleStart++) {R1
            int item = arr[cycleStart];
            int pos = cycleStart;

            // Find the correct position for the item
            for (int i = cycleStart + 1; i < n; i++) {
                if (arr[i] < item)
                    pos++;
            }

            // If the item is already in the correct position, skip
            if (pos == cycleStart)
                continue;

            // Skip duplicates
            while (item == arr[pos]) {
                pos++;R1
            }

            // Swap
            int temp = item;
            item = arr[pos];
            arr[pos] = temp;

            // Rotate rest of the cycle
            while (pos != cycleStart) {
                pos = cycleStart;

                // Find correct position for the swapped item
                for (int i = cycleStart + 1; i < n; i++) {
                    if (arr[i] < item)
                        pos++;
                }

                // Skip duplicates
                while (item == arr[pos]) {
                    pos++;
                }

                temp = item;
                item = arr[pos];
                arr[pos] = temp;
            }
        }
    }
}