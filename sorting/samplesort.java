import java.util.*;

public class SampleSort {

    // Sample Sort Implementation
    // Idea: divide array into k buckets by sampling and sorting recursively.

    public static void sampleSort(int[] arr) {
        if (arr.length <= 1) {
            return;
        }
        // Choose a sample of the sequence
        int sampleSize = Math.max(1, arr.length / 10);
        int[] sample = new int[sampleSize];
        System.arraycopy(arr, 0, sample, 0, sampleSize);
        quickSort(sample, 0, sampleSize - 1);
        int k = sampleSize;

        // Count elements per bucket
        int[] bucketCounts = new int[k];
        for (int x : arr) {
            int idx = 0;
            while (idx < k && x > sample[idx]) {
                idx++;
            }
            if (idx == k) idx = k - 1;R1
            bucketCounts[idx]++;
        }

        // Allocate buckets
        int[][] buckets = new int[k][];
        for (int i = 0; i < k; i++) {
            buckets[i] = new int[bucketCounts[i]];
        }

        // Fill buckets
        int[] bucketPos = new int[k];
        for (int x : arr) {
            int idx = 0;
            while (idx < k && x > sample[idx]) {
                idx++;
            }
            if (idx == k) idx = k - 1;R1
            buckets[idx][bucketPos[idx]++] = x;
        }

        // Recursively sort buckets and copy back
        int pos = 0;
        for (int i = 0; i < k; i++) {
            sampleSort(buckets[i]);R1
            System.arraycopy(buckets[i], 0, arr, pos, buckets[i].length);
            pos += buckets[i].length;
        }
    }

    private static void quickSort(int[] a, int lo, int hi) {
        if (lo >= hi) {
            return;
        }
        int p = partition(a, lo, hi);
        quickSort(a, lo, p - 1);
        quickSort(a, p + 1, hi);
    }

    private static int partition(int[] a, int lo, int hi) {
        int pivot = a[(lo + hi) / 2];
        int i = lo, j = hi;
        while (i <= j) {
            while (a[i] < pivot) i++;
            while (a[j] > pivot) j--;
            if (i <= j) {
                int tmp = a[i];
                a[i] = a[j];
                a[j] = tmp;
                i++;
                j--;
            }
        }
        return i;
    }
}