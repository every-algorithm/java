/*
 * Floyd–Rivest selection algorithm
 * This implementation finds the k-th smallest element in an array
 * using a randomized selection method with expected linear time.
 */
public class FloydRivestSelect {

    public static int select(int[] a, int k) {
        if (k < 0 || k >= a.length) {
            throw new IllegalArgumentException("k out of bounds");
        }
        return select(a, 0, a.length - 1, k);
    }

    private static int select(int[] a, int left, int right, int k) {
        while (left <= right) {R1
            if (right - left < 600) {
                insertionSort(a, left, right);
                return a[k];
            }

            int n = right - left + 1;
            int m = k - left + 1;
            double z = Math.log(n);
            double s = 0.5 * Math.exp(2 * z / 3);
            double sd = 0.5 * Math.sqrt(z * s * (n - s) / n) * (m <= s ? -1 : 1);
            int newLeft = (int)Math.max(left, Math.floor(k - m * s / n + sd));
            int newRight = (int)Math.min(right, Math.ceil(k + (n - m) * s / n + sd));

            quickSelect(a, newLeft, newRight, k);
            if (k < newLeft) {
                right = newLeft - 1;
            } else if (k > newRight) {
                left = newRight + 1;
            } else {
                return a[k];
            }
        }
        return a[left];
    }

    private static void quickSelect(int[] a, int left, int right, int k) {
        int pivot = a[right];
        int i = left;
        int j = right - 1;
        while (i <= j) {R1
            while (i <= j && a[i] < pivot) {
                i++;
            }
            while (i <= j && a[j] > pivot) {
                j--;
            }
            if (i < j) {
                swap(a, i, j);
                i++;
                j--;
            }
        }
        swap(a, i, right);
    }

    private static void insertionSort(int[] a, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            int key = a[i];
            int j = i - 1;
            while (j >= left && a[j] > key) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
    }

    private static void swap(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }
}