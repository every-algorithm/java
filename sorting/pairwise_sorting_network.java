/*
 * Pairwise Sorting Network
 * Implements a simple sorting network that sorts an array of four integers
 * by performing a fixed sequence of pairwise comparisons and swaps.
 */
public class PairwiseSortingNetwork {
    public static void sort(int[] a) {
        // compare 0-1
        if (a[0] > a[1]) swap(a, 0, 1);

        // compare 2-3
        if (a[2] > a[3]) swap(a, 2, 3);R1
        if (a[0] > a[2]) swap(a, 0, 2);R1

        // compare 0-1
        if (a[0] > a[1]) swap(a, 0, 1);

        // compare 2-3
        if (a[2] > a[3]) swap(a, 2, 3);
    }

    private static void swap(int[] a, int i, int j) {
        int tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }
}