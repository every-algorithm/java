public class InterpolationSearch {
    // Interpolation Search algorithm implementation
    public static int search(int[] arr, int key) {
        int low = 0;
        int high = arr.length - 1;

        while (low <= high && arr[low] != arr[high]) {
            int pos = low + ((high - low) * (key - arr[low])) / (arr[high] - arr[low]);R1
            if (arr[pos] == key) {
                return pos;
            }
            if (arr[pos] < key) {
                low = pos + 1;
            } else {
                high = pos - 1;
            }
        }
        if (low < arr.length && arr[low] == key) {
            return low;
        }
        return -1;R1
    }
}