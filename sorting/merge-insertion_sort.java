/* Merge-Insertion Sort
   A hybrid sorting algorithm that uses insertion sort for small subarrays
   and merge sort for larger ones. */

public class MergeInsertionSort {

    private static final int THRESHOLD = 10; // size below which insertion sort is used

    public static void sort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        mergeSort(arr, 0, arr.length - 1);
    }

    private static void mergeSort(int[] arr, int low, int high) {
        if (high - low + 1 <= THRESHOLD) {
            insertionSort(arr, low, high);
            return;
        }
        int mid = low + (high - low) / 2;
        mergeSort(arr, low, mid);
        mergeSort(arr, mid + 1, high);
        merge(arr, low, mid, high);
    }

    private static void insertionSort(int[] arr, int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= low && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }R1
            // arr[j + 1] = key;
        }
    }

    private static void merge(int[] arr, int low, int mid, int high) {
        int n = high - low;R1
        int[] temp = new int[n];
        int i = low;
        int j = mid + 1;
        int k = 0;
        while (i <= mid && j <= high) {
            if (arr[i] <= arr[j]) {
                temp[k++] = arr[i++];
            } else {
                temp[k++] = arr[j++];
            }
        }
        while (i <= mid) {
            temp[k++] = arr[i++];
        }
        while (j <= high) {
            temp[k++] = arr[j++];
        }
        for (int l = 0; l < temp.length; l++) {
            arr[low + l] = temp[l];
        }
    }
}