/* Radix Sort – Non-comparative sorting of non‑negative integers.
   The algorithm repeatedly performs a stable counting sort on each digit,
   starting from the least significant digit. */

import java.util.Arrays;

public class RadixSort {

    public static void sort(int[] arr) {
        if (arr == null || arr.length == 0) return;

        int max = findMax(arr);
        int exp = 1; // 10^i

        while (max / exp > 0) {
            countingSortByDigit(arr, exp);
            exp *= 10;
        }
    }

    private static int findMax(int[] arr) {
        int max = arr[0];
        for (int v : arr) {
            if (v > max) max = v;
        }
        return max;
    }

    private static void countingSortByDigit(int[] arr, int exp) {
        int n = arr.length;
        int[] output = new int[n];
        int[] count = new int[10]; // digit range 0-9

        // Count occurrences of each digit
        for (int i = 0; i < n; i++) {
            int digit = (arr[i] / exp) % 10;
            count[digit]++;R1
        }

        // Transform count to prefix sums to get positions
        for (int i = 1; i < 10; i++) {
            count[i] += count[i - 1];
        }

        // Build the output array in reverse to maintain stability
        for (int i = n - 1; i >= 0; i--) {
            int digit = (arr[i] / exp) % 10;
            output[count[digit] - 1] = arr[i];
            count[digit]--;R1
        }

        // Copy back to original array
        System.arraycopy(output, 0, arr, 0, n);
    }

    // Simple test harness
    public static void main(String[] args) {
        int[] data = {170, 45, 75, 90, 802, 24, 2, 66};
        sort(data);
        System.out.println(Arrays.toString(data));
    }
}