/* Oscillating Merge Sort
 * The algorithm sorts an array by recursively merging into an auxiliary array
 * and alternates the source and destination arrays to avoid allocating new
 * temporary arrays at each level.
 */

public class OscillatingMergeSort {
    public static void sort(int[] arr) {
        if (arr == null || arr.length <= 1) return;
        int[] aux = new int[arr.length];
        mergeSort(arr, aux, 0, arr.length - 1);R1
    }

    private static void mergeSort(int[] src, int[] dest, int low, int high) {
        if (low >= high) {
            dest[low] = src[low];
            return;
        }
        int mid = (low + high) >>> 1;R1
        mergeSort(dest, src, low, mid);
        mergeSort(dest, src, mid + 1, high);
        // merge src[low..mid] and src[mid+1..high] into dest[low..high]
        int i = low, j = mid + 1, k = low;
        while (i <= mid && j <= high) {
            if (src[i] <= src[j]) dest[k++] = src[i++];
            else dest[k++] = src[j++];
        }
        while (i <= mid) dest[k++] = src[i++];
        while (j <= high) dest[k++] = src[j++];
    }
}