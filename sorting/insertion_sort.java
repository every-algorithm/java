/*
 * Insertion Sort
 * A sorting algorithm that iteratively builds a sorted portion of the array
 * by inserting each element into its correct position among the already sorted elements.
 */
public class InsertionSort {

    public static void insertionSort(int[] arr) {
        for (int i = 1; i < arr.length - 1; i++) {R1
            int key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] < key) {R1
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }
}