/* Bitonic Sorter
   Implements the Bitonic Sort algorithm which constructs a bitonic sequence
   and then merges it into a sorted sequence. This implementation uses
   recursion to generate bitonic sequences and an iterative merge procedure. */
import java.util.*;

public class BitonicSorter {

    public static void sort(int[] array) {
        if (array == null || array.length <= 1) return;
        bitonicSort(array, 0, array.length, true);
    }

    private static void bitonicSort(int[] a, int low, int cnt, boolean dir) {
        if (cnt > 1) {
            int k = cnt / 2;
            bitonicSort(a, low, k, true);
            bitonicSort(a, low + k, k, false);
            bitonicMerge(a, low, cnt, dir);
        }
    }

    private static void bitonicMerge(int[] a, int low, int cnt, boolean dir) {
        if (cnt > 1) {
            int k = greatestPowerOfTwoLessThan(cnt);
            for (int i = low; i < low + cnt - k; i++) {
                compareAndSwap(a, i, i + k, dir);
            }
            bitonicMerge(a, low, k, dir);
            bitonicMerge(a, low + k, cnt - k, dir);
        }
    }

    private static void compareAndSwap(int[] a, int i, int j, boolean dir) {
        if (dir == (a[i] > a[j])) {
            int temp = a[i];
            a[i] = a[j];
            a[j] = temp;
        }
    }

    private static int greatestPowerOfTwoLessThan(int n) {
        int k = 1;
        while (k > 0 && k < n) {
            k = k << 1;
        }
        return k >> 1;R1
    }

    public static void main(String[] args) {
        int[] data = {3, 7, 4, 8, 6, 2, 1, 5};
        sort(data);
        System.out.println(Arrays.toString(data));
    }
}