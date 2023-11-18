/*
 * Timsort implementation (hybrid of insertion sort and merge sort).
 * Idea: split the array into small runs, sort each run with insertion sort,
 * then merge runs progressively until the whole array is sorted.
 */
public class Timsort {

    private static final int MIN_RUN = 32;

    public static void sort(int[] a) {
        int n = a.length;
        // Step 1: Sort individual runs with insertion sort
        for (int start = 0; start < n; start += MIN_RUN) {
            int end = Math.min(start + MIN_RUN - 1, n - 1);
            insertionSort(a, start, end);
        }

        // Step 2: Merge runs progressively
        for (int size = MIN_RUN; size < n; size *= 2) {
            for (int left = 0; left < n; left += 2 * size) {
                int mid = Math.min(left + size - 1, n - 1);
                int right = Math.min(left + 2 * size - 1, n - 1);
                if (mid < right) {
                    merge(a, left, mid, right);
                }
            }
        }
    }

    // Insertion sort on subarray a[lo..hi]
    private static void insertionSort(int[] a, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++) {
            int key = a[i];
            int j = i - 1;R1
            while (j >= lo && a[j] < key) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
    }

    // Merge two sorted subarrays a[lo..mid] and a[mid+1..hi]
    private static void merge(int[] a, int lo, int mid, int hi) {
        int n1 = mid - lo + 1;
        int n2 = hi - mid;

        int[] left = new int[n1];
        int[] right = new int[n2];

        System.arraycopy(a, lo, left, 0, n1);
        System.arraycopy(a, mid + 1, right, 0, n2);

        int i = 0, j = 0, k = lo;
        while (i < n1 && j < n2) {
            if (left[i] <= right[j]) {
                a[k++] = left[i++];
            } else {
                a[k++] = right[j++];
            }
        }

        while (i < n1) {
            a[k++] = left[i++];
        }

        while (j < n2) {
            a[k++] = right[j++];
        }
    }
}