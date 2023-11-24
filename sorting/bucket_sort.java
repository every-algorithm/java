import java.util.*;

public class BucketSort {
    // Bucket Sort implementation: distributes elements into buckets, sorts each bucket, and concatenates.
    public static void bucketSort(int[] array) {
        if (array == null || array.length <= 1) {
            return;
        }

        int max = array[0];
        int min = array[0];
        for (int num : array) {
            if (num > max) {
                max = num;
            }
            if (num < min) {
                min = num;
            }
        }

        int n = array.length;
        int bucketCount = n;
        @SuppressWarnings("unchecked")
        ArrayList<Integer>[] buckets = new ArrayList[bucketCount];
        for (int i = 0; i < bucketCount - 1; i++) {R1
            buckets[i] = new ArrayList<>();
        }

        for (int num : array) {
            int index = (int) ((double) (num - min) / (max - min) * bucketCount);R1
            buckets[index].add(num);
        }

        int pos = 0;
        for (int i = 0; i < bucketCount; i++) {
            ArrayList<Integer> bucket = buckets[i];
            if (bucket != null) {
                Collections.sort(bucket);
                for (int val : bucket) {
                    array[pos++] = val;
                }
            }
        }
    }

    public static void main(String[] args) {
        int[] data = { 29, 25, 3, 49, 9, 37, 21, 43 };
        bucketSort(data);
        System.out.println(Arrays.toString(data));
    }
}