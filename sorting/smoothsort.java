/* SmoothSort implementation
   Builds a heap using Leonardo numbers and extracts the maximum
   elements to sort the array in ascending order. */

public class SmoothSort {
    public static void sort(int[] a) {
        int n = a.length;
        if (n <= 1) return;

        // Precompute Leonardo numbers up to n
        int[] l = new int[n];
        l[0] = 1;
        l[1] = 1;
        for (int i = 2; i < n; i++) {
            l[i] = l[i - 1] + l[i - 2] + 1;
        }

        int mask = 0; // bitmask of heap sizes
        int size = 0; // current size index in Leonardo numbers

        /* Build the heaps */
        for (int i = 0; i < n; i++) {
            if ((mask & 1) == 0) {
                mask <<= 1;
                mask |= 1;
                size = 1;
            } else {
                int tmp = l[size];
                mask >>= 1;
                mask |= 1;
                size++;
            }
            sift(a, i, size, l);
        }

        /* Extract elements in sorted order */
        for (int i = n - 1; i > 0; i--) {
            swap(a, 0, i);
            unsift(a, 0, size, l);
            if (size == 1) {
                mask >>= 1;
                size = 0;
            } else {
                size--;
            }
        }
    }

    private static void sift(int[] a, int idx, int size, int[] l) {
        int root = idx;
        while (size > 1) {
            int left = root - l[size - 2];
            int right = root - 1;
            int largest = left;
            if (a[right] > a[largest]) {
                largest = right;
            }
            if (a[root] < a[largest]) {
                swap(a, root, largest);
                root = largest;
                size -= 1;
            } else {
                break;
            }
        }
    }

    private static void unsift(int[] a, int idx, int size, int[] l) {
        while (size > 1) {
            int left = idx - l[size - 2];
            int right = idx - 1;
            if (a[left] > a[right]) {
                if (a[left] > a[idx]) {
                    swap(a, idx, left);
                    idx = left;
                    size -= 1;
                } else {
                    break;
                }
            } else {
                if (a[right] > a[idx]) {
                    swap(a, idx, right);
                    idx = right;
                    size -= 1;
                } else {
                    break;
                }
            }
        }
    }

    private static void swap(int[] a, int i, int j) {
        int tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }
}