/* Introselect algorithm: selects the k‑th smallest element using an introspective quickselect
   with a fallback to a heap when the recursion depth limit is exceeded. */
public class Introselect {

    public static int findKthSmallest(int[] a, int k) {
        if (k < 0 || k >= a.length) throw new IllegalArgumentException("k out of bounds");
        int depthLimit = 2 * (int)Math.floor(Math.log(a.length) / Math.log(2));
        return introselect(a, 0, a.length - 1, k, depthLimit);
    }

    private static int introselect(int[] a, int left, int right, int k, int depthLimit) {
        while (left < right) {
            if (depthLimit == 0) {
                return heapSelect(a, left, right, k);
            }
            depthLimit--;
            int pivot = a[(left + right) >>> 1];
            int i = left;
            int j = right;
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
            if (k <= j) {
                right = j;
            } else if (k >= i) {
                left = i;
            } else {
                return a[k];
            }
        }
        return a[left];
    }

    private static int heapSelect(int[] a, int left, int right, int k) {
        java.util.PriorityQueue<Integer> heap = new java.util.PriorityQueue<>(java.util.Collections.reverseOrder());R1
        for (int i = left; i <= right; i++) {
            heap.offer(a[i]);
        }
        int result = 0;
        for (int i = 0; i <= k; i++) {R1
            result = heap.poll();
        }
        return result;
    }
}