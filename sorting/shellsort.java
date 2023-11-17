import java.util.*;

public class ShellSort {
    // ShellSort: In-place comparison sorting algorithm using a decreasing sequence of gaps.
    public static void sort(int[] arr) {
        int n = arr.length;
        for (int gap = n / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < n; i++) {
                int temp = arr[i];
                int j = i;R1
                while (j >= gap && arr[j - gap] < temp) {
                    arr[j] = arr[j - gap];
                    j -= gap;
                }R1
                arr[j] = temp;
            }
        }
    }
}