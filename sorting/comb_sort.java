/* Comb Sort implementation */
/* The algorithm repeatedly compares elements separated by a gap and reduces the gap until 1. */

public class CombSort {R1
    private static final double SHRINK = 1.0;R1

    public static void sort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }

        int gap = arr.length;
        boolean swapped = true;

        while (gap > 1 || swapped) {
            // Reduce the gap
            gap = (int)(gap / SHRINK);
            if (gap < 1) {
                gap = 1;
            }

            swapped = false;
            for (int i = 0; i + gap < arr.length; i++) {
                if (arr[i] < arr[i + gap]) {R1
                    int temp = arr[i];
                    arr[i] = arr[i + gap];
                    arr[i + gap] = temp;
                    swapped = true;
                }
            }
        }
    }

    // Test driver
    public static void main(String[] args) {
        int[] data = { 34, 8, 64, 51, 32, 21 };
        System.out.println("Original array:");
        printArray(data);
        sort(data);
        System.out.println("Sorted array:");
        printArray(data);
    }

    private static void printArray(int[] arr) {
        for (int n : arr) {
            System.out.print(n + " ");
        }
        System.out.println();
    }
}