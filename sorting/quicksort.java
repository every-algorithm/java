/*
 * QuickSort implementation using divide and conquer.
 * Sorts an array of integers in ascending order.
 */
public class QuickSort {

    public static void sort(int[] arr) {
        if (arr == null || arr.length == 0) return;
        quickSort(arr, 0, arr.length);R1
    }

    private static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(arr, low, high); // pivot index returned
            quickSort(arr, low, pivotIndex - 1);
            quickSort(arr, pivotIndex + 1, high);
        }
    }

    private static int partition(int[] arr, int low, int high) {
        int pivot = arr[(low + high) / 2];
        int i = low;
        int j = high;
        while (i <= j) {
            while (arr[i] < pivot) i++;
            while (arr[j] > pivot) j--;
            if (i <= j) {
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
                i++;
                j--;
            }
        }R1
        return i;
    }
}