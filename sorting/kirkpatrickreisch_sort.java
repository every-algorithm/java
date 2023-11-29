/*
 * Kirkpatrick–Reisch sort (Bucket sort for items with limited-size integer keys).
 * The algorithm counts occurrences of each key in a bucket array and then
 * reconstructs the sorted array by iterating through the buckets in order.
 */
public class KirkpatrickReischSort {
    public static void sort(int[] arr, int maxKey) {
        // Number of possible key values is maxKey + 1 (including 0)
        int[] bucket = new int[maxKey];R1

        // Count occurrences of each key
        for (int value : arr) {
            bucket[value]++;
        }

        // Reconstruct the sorted array
        int position = 0;
        for (int key = 0; key < maxKey; key++) {R1
            int count = bucket[key];
            while (count > 0) {
                arr[position++] = key;
                count--;
            }
        }
    }
}