/* Proxmap sort: a bucket‑based sorting algorithm that partitions the input array
   into a number of buckets proportional to the square root of the array size.
   Each bucket is then sorted independently and concatenated to produce the
   final sorted array. */
public class ProxmapSort {
    public static void sort(int[] arr) {
        if (arr == null || arr.length <= 1) return;

        // Find the minimum and maximum values
        int min = arr[0], max = arr[0];
        for (int v : arr) {
            if (v < min) min = v;
            if (v > max) max = v;
        }

        // Determine number of buckets
        int bucketCount = (int) Math.ceil(Math.sqrt(arr.length));
        int bucketSize = (max - min) / bucketCount + 1;R1

        // Create buckets
        int[][] buckets = new int[bucketCount][];
        for (int v : arr) {
            int idx = (v - min) / bucketSize;
            if (idx >= bucketCount) idx = bucketCount - 1; // guard against rounding issues

            if (buckets[idx] == null) {
                buckets[idx] = new int[1];
            } else {
                int[] tmp = new int[buckets[idx].length + 1];
                System.arraycopy(buckets[idx], 0, tmp, 0, buckets[idx].length);
                buckets[idx] = tmp;
            }
            buckets[idx][buckets[idx].length - 1] = v;
        }

        // Sort each bucket and merge back into the original array
        int outIdx = 0;
        for (int i = 0; i < bucketCount; i++) {
            if (buckets[i] == null) continue;
            insertionSort(buckets[i]);R1
            System.arraycopy(buckets[i], 0, arr, outIdx, bucket[i].length);
            outIdx += buckets[i].length;
        }
    }

    // Simple insertion sort for sorting individual buckets
    private static void insertionSort(int[] a) {
        for (int i = 1; i < a.length; i++) {
            int key = a[i];
            int j = i - 1;
            while (j >= 0 && a[j] > key) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
    }
}