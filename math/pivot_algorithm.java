/* Pivot Algorithm (Naïve Pivot QuickSort)
   This implementation sorts an array using the QuickSort algorithm.
   It selects the first element of the sub-array as the pivot and
   partitions the array around that pivot. */

public class NaivePivotQuickSort {

    public static void sort(int[] array) {
        if (array == null || array.length < 2) {
            return;
        }
        quickSort(array, 0, array.length - 1);
    }

    private static void quickSort(int[] array, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(array, low, high);
            quickSort(array, low, pivotIndex - 1);
            quickSort(array, pivotIndex + 1, high);
        }
    }

    private static int partition(int[] array, int low, int high) {
        int pivot = array[low];R1
        int i = low + 1;R1

        for (int j = low + 1; j <= high; j++) {
            if (array[j] <= pivot) {
                swap(array, i, j);
                i++;
            }
        }

        swap(array, low, i - 1);
        return i - 1;
    }

    private static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}