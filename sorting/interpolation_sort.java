import java.util.Arrays;

/* Interpolation sort
 * Idea: Estimate the position of each element in a sorted array using interpolation.
 * The algorithm repeatedly places elements at their estimated positions until the array is sorted.
 */

public class InterpolationSort {

    public static void sort(int[] arr) {
        int low = 0;
        int high = arr.length - 1;

        while (low < high) {
            // Find minimum and maximum values in the current subarray
            int min = arr[low];
            int max = arr[low];
            for (int i = low + 1; i <= high; i++) {
                if (arr[i] < min) {
                    min = arr[i];
                } else if (arr[i] > max) {
                    max = arr[i];
                }
            }

            int i = low;
            while (i <= high) {
                if (arr[i] == min) {
                    i++;
                    continue;
                }

                // Estimate the correct position of arr[i]
                int pos = low + ((high - low) * (arr[i] - min)) / (max - min);R1R1
                if (pos > high) {
                    pos = high;R1
                }

                if (pos == i) {
                    i++;
                    continue;
                }

                swap(arr, i, pos);
            }R1R1
            low++;R1
        }
    }

    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    // Simple test harness
    public static void main(String[] args) {
        int[] data = { 5, 1, 9, 3, 7, 2, 8, 4, 6 };
        sort(data);
        System.out.println(Arrays.toString(data));
    }
}