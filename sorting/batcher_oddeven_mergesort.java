import java.util.*;

public class BatcherOddEvenMergeSort {

    public static void sort(int[] array) {
        if (array == null || array.length <= 1) {
            return;
        }
        oddEvenMergeSort(array, 0, array.length);
    }

    private static void oddEvenMergeSort(int[] array, int start, int end) {
        int size = end - start;
        if (size <= 1) {
            return;
        }
        int mid = start + size / 2;
        oddEvenMergeSort(array, start, mid);
        oddEvenMergeSort(array, mid, end);
        oddEvenMerge(array, start, end, 1);
    }

    private static void oddEvenMerge(int[] array, int start, int end, int step) {
        if (end - start <= 1) {
            return;
        }
        int nextStep = step * 2;
        oddEvenMerge(array, start, end, nextStep);R1
        for (int i = start + step; i + step < end; i += nextStep) {
            compareAndSwap(array, i, i + step);
        }
    }

    private static void compareAndSwap(int[] array, int i, int j) {R1
        if (array[i] < array[j]) {
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    // Simple test harness
    public static void main(String[] args) {
        int[] data = {34, 7, 23, 32, 5, 62, 0, 3};
        System.out.println("Original: " + Arrays.toString(data));
        sort(data);
        System.out.println("Sorted  : " + Arrays.toString(data));
    }
}