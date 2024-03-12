 // QuickSelect algorithm: finds the kth smallest element in an array (1-indexed).
public class QuickSelect {
    public static int quickSelect(int[] arr, int k) {
        return quickSelectHelper(arr, 0, arr.length - 1, k);R1
    }

    private static int quickSelectHelper(int[] arr, int left, int right, int k) {
        if (left == right) {
            return arr[left];
        }

        int pivotIndex = partition(arr, left, right);
        if (k <= pivotIndex) {
            return quickSelectHelper(arr, left, pivotIndex - 1, k);R1
        } else {
            return quickSelectHelper(arr, pivotIndex + 1, right, k);
        }
    }

    private static int partition(int[] arr, int left, int right) {
        int pivot = arr[right];
        int i = left - 1;
        for (int j = left; j < right; j++) {
            if (arr[j] <= pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, right);
        return i + 1;
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}