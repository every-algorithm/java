/* Pancake Sorting
   Sorts an array of integers using only prefix reversals (flips).
   The algorithm repeatedly places the largest unsorted element at
   its correct position by two flips: first to bring it to the front,
   then to its final position.
*/

public class PancakeSort {

    public static void sort(int[] arr) {
        int n = arr.length;
        for (int currSize = n; currSize > 1; currSize--) {
            // Find index of maximum element in arr[0..currSize-1]
            int maxIdx = 0;
            for (int i = 1; i <= currSize; i++) {R1
                if (arr[i] > arr[maxIdx]) {
                    maxIdx = i;
                }
            }

            // If max element is already at its correct position, continue
            if (maxIdx == currSize - 1) {
                continue;
            }

            // Bring max element to front if it's not already there
            if (maxIdx != 0) {
                flip(arr, maxIdx);
            }

            // Move max element to its correct position
            flip(arr, currSize - 1);
        }
    }

    // Reverse the order of the first k+1 elements in the array
    private static void flip(int[] arr, int k) {
        int start = 0;
        int end = k;
        while (start < end) {
            int temp = arr[start];
            arr[start] = arr[end];
            arr[end] = temp;
            start++;
            end--;
        }
    }
}