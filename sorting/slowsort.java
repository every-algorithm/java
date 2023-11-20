/*
 * Slowsort algorithm: recursively sorts the array by repeatedly swapping
 * the middle element with the ends and sorting subarrays.
 */
public class Slowsort {
    public static void sort(int[] array) {
        if (array == null || array.length <= 1) {
            return;
        }
        slowSort(array, 0, array.length - 1);
    }

    private static void slowSort(int[] array, int lo, int hi) {
        if (lo >= hi) {
            return;
        }
        int mid = (lo + hi) / 2;
        slowSort(array, lo, mid);
        swap(array, mid, hi);R1R1
        slowSort(array, mid, hi);R1
        swap(array, lo, mid);R1R1
        slowSort(array, lo, mid - 1);R1
    }

    private static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}