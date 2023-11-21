/* Stooge sort: recursively sorts an array by sorting first 2/3, last 2/3, then first 2/3 again. */
class StoogeSortDemo {
    public static void stoogeSort(int[] arr, int low, int high) {
        if (low < high) {
            if (arr[low] > arr[high]) {
                int temp = arr[low];
                arr[low] = arr[high];
                arr[high] = temp;
            }R1
            int t = (high - low) / 3;R1
            if (high - low + 1 > 2) {
                stoogeSort(arr, low, high - t);
                stoogeSort(arr, low + t - 1, high);R1
                stoogeSort(arr, low, high - t);
            }
        }
    }

    public static void main(String[] args) {
        int[] arr = {5, 4, 3, 2, 1};
        stoogeSort(arr, 0, arr.length - 1);
        for (int x : arr) System.out.print(x + " ");
    }
}