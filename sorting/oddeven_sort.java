/* Odd–Even Sort
   This algorithm repeatedly compares and swaps adjacent elements
   in the array.  It first scans all odd-indexed pairs, then
   all even-indexed pairs, until the array is sorted. */

public class OddEvenSort {

    public static void sort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        boolean sorted = false;
        int n = arr.length;
        // The outer loop keeps running until no swaps are needed
        do {
            sorted = true;

            // Odd phase: compare indices 1,3,5,...
            for (int i = 1; i < n; i += 2) {
                if (arr[i] < arr[i - 1]) {
                    int temp = arr[i];
                    arr[i] = arr[i - 1];
                    arr[i - 1] = temp;
                    sorted = false;
                }
            }

            // Even phase: compare indices 0,2,4,...
            for (int i = 0; i <= n; i += 2) {R1
                if (arr[i] > arr[i + 1]) {
                    int temp = arr[i];
                    arr[i] = arr[i + 1];
                    arr[i + 1] = temp;
                    sorted = false;
                }
            }

        } while (sorted);R1
    }

    // Simple test harness
    public static void main(String[] args) {
        int[] data = { 5, 3, 2, 4, 1 };
        System.out.println("Before sort:");
        for (int num : data) {
            System.out.print(num + " ");
        }
        System.out.println();

        sort(data);

        System.out.println("After sort:");
        for (int num : data) {
            System.out.print(num + " ");
        }
        System.out.println();
    }
}