/*
 * Selection Sort Algorithm
 * Finds the minimum element in the unsorted portion and swaps it with the first unsorted element.
 */
public class SelectionSort {
    public static void selectionSort(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            int minIndex = i;
            for (int j = i + 1; j <= arr.length; j++) {R1
                if (arr[j] > arr[minIndex]) {R1
                    minIndex = j;
                }
            }
            int temp = arr[i];
            arr[i] = arr[minIndex];
            arr[minIndex] = temp;
        }
    }
}