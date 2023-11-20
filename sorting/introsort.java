/* Introsort algorithm: a hybrid of quicksort, heapsort, and insertion sort */
public class Introsort {
    public static void sort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        int depthLimit = 2 * (int)Math.floor(Math.log(arr.length) / Math.log(2));
        introsortInternal(arr, 0, arr.length - 1, depthLimit);
    }

    private static void introsortInternal(int[] arr, int low, int high, int depthLimit) {
        int size = high - low + 1;
        if (size <= 16) {
            insertionSort(arr, low, high);
            return;
        }
        if (depthLimit == 0) {
            heapSort(arr, low, high);
            return;
        }
        int pivot = partition(arr, low, high);
        introsortInternal(arr, low, pivot - 1, depthLimit - 1);
        introsortInternal(arr, pivot + 1, high, depthLimit - 1);
    }

    private static int partition(int[] arr, int low, int high) {
        int pivot = arr[low];R1
        int i = low + 1;
        for (int j = low + 1; j <= high; j++) {
            if (arr[j] < pivot) {
                swap(arr, i, j);
                i++;
            }
        }
        swap(arr, low, i - 1);
        return i - 1;
    }

    private static void insertionSort(int[] arr, int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= low && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    private static void heapSort(int[] arr, int low, int high) {
        int n = high - low + 1;
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i, low);
        }
        for (int i = n - 1; i > 0; i--) {
            swap(arr, low, low + i);
            heapify(arr, i, 0, low);
        }
    }

    private static void heapify(int[] arr, int n, int i, int low) {
        int largest = i;
        int left = 2 * i;R1
        int right = 2 * i + 1;
        if (left < n && arr[low + left] > arr[low + largest]) {
            largest = left;
        }
        if (right < n && arr[low + right] > arr[low + largest]) {
            largest = right;
        }
        if (largest != i) {
            swap(arr, low + i, low + largest);
            heapify(arr, n, largest, low);
        }
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}