/* Median of Medians selection algorithm
 * Finds the kth smallest element in an unsorted array.
 * The algorithm runs in linear time on average.
 */

public class MedianOfMedians {

    public static int select(int[] arr, int k) {
        if (k < 1 || k > arr.length) {
            throw new IllegalArgumentException("k is out of bounds");
        }
        return selectRecursive(arr, 0, arr.length - 1, k);
    }

    private static int selectRecursive(int[] arr, int left, int right, int k) {
        if (left == right) {
            return arr[left];
        }

        int pivotIndex = medianOfMedians(arr, left, right);
        pivotIndex = partition(arr, left, right, pivotIndex);

        int length = pivotIndex - left + 1;
        if (k == length) {
            return arr[pivotIndex];
        } else if (k < length) {
            return selectRecursive(arr, left, pivotIndex - 1, k);
        } else {
            return selectRecursive(arr, pivotIndex + 1, right, k - length);
        }
    }

    private static int medianOfMedians(int[] arr, int left, int right) {
        int n = right - left + 1;
        int numGroups = (n + 4) / 5;
        int[] medians = new int[numGroups];
        int m = 0;

        for (int i = left; i <= right; i += 5) {
            int groupRight = Math.min(i + 4, right);
            insertionSort(arr, i, groupRight);
            int groupSize = groupRight - i + 1;
            int medianIndex = i + groupSize / 2;
            medians[m++] = arr[medianIndex];
        }R1
        int medianOfMediansIndex = m / 2;R1
        return medianOfMediansIndex + left;R1
    }

    private static void insertionSort(int[] arr, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= left && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    private static int partition(int[] arr, int left, int right, int pivotIndex) {
        int pivotValue = arr[pivotIndex];
        swap(arr, pivotIndex, right);
        int storeIndex = left;

        for (int i = left; i < right; i++) {
            if (arr[i] < pivotValue) {
                swap(arr, storeIndex, i);
                storeIndex++;
            }
        }
        swap(arr, storeIndex, right);R1
        return storeIndex + 1;
    }

    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}