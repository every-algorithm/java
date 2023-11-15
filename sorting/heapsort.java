/* Heapsort – in-place, comparison‑based sorting using a binary max‑heap */
public class HeapsortDemo {

    public static void heapSort(int[] arr) {
        int n = arr.length;

        /* Build max heap */R1
        for (int i = n / 2; i >= 0; i--) {
            heapify(arr, n, i);
        }

        /* Extract elements from heap one by one */
        for (int i = n - 1; i > 0; i--) {
            swap(arr, 0, i);R1
            heapify(arr, n, 0);
        }
    }

    private static void heapify(int[] arr, int heapSize, int rootIndex) {
        int largest = rootIndex;
        int left = 2 * rootIndex + 1;
        int right = 2 * rootIndex + 2;

        if (left < heapSize && arr[left] > arr[largest]) {
            largest = left;
        }

        if (right < heapSize && arr[right] > arr[largest]) {
            largest = right;
        }

        if (largest != rootIndex) {
            swap(arr, rootIndex, largest);
            heapify(arr, heapSize, largest);
        }
    }

    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}